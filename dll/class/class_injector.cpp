#include "class_injector.h"

#include <string.h>

#include "classes.h"
#include "../injector/injector.h"
#include "../jvm/jni.h"

jobject GetThreadsIterator(JNIEnv *env) {
    auto thread_class = env->FindClass("java/lang/Thread");
    auto get_all_stack_traces_method = env->GetStaticMethodID(thread_class, "getAllStackTraces", "()Ljava/util/Map;");

    auto thread_map = env->CallStaticObjectMethod(thread_class, get_all_stack_traces_method);
    auto map_class = env->GetObjectClass(thread_map);
    auto key_set_method = env->GetMethodID(map_class, "keySet", "()Ljava/util/Set;");

    auto threads_set = env->CallObjectMethod(thread_map, key_set_method);
    auto set_class = env->GetObjectClass(threads_set);
    auto iterator_method = env->GetMethodID(set_class, "iterator", "()Ljava/util/Iterator;");

    auto iterator = env->CallObjectMethod(threads_set, iterator_method);

    return iterator;
}

jobject FindThreadByName(JNIEnv *env, const char *str) {
    auto thread_class = env->FindClass("java/lang/Thread");
    auto thread_name_method = env->GetMethodID(thread_class, "getName", "()Ljava/lang/String;");

    auto iterator = GetThreadsIterator(env);
    if (iterator == nullptr) {
        Debug(L"Threads iterator null");
        return nullptr;
    }

    auto iterator_class = env->GetObjectClass(iterator);
    auto has_next_method = env->GetMethodID(iterator_class, "hasNext", "()Z");
    auto next_method = env->GetMethodID(iterator_class, "next", "()Ljava/lang/Object;");

    while (env->CallBooleanMethod(iterator, has_next_method)) {
        auto thread = env->CallObjectMethod(iterator, next_method);

        if (thread == nullptr) {
            return nullptr;
        }

        auto name = env->CallObjectMethod(thread, thread_name_method);
        if (name == nullptr) {
            continue;
        }
        auto name_str = env->GetStringUTFChars(reinterpret_cast<jstring>(name), nullptr);
        if (name_str == nullptr) {
            continue;
        }

        if (strcmp(name_str, str) == 0) {
            return thread;
        }
    }

    return nullptr;
}


jobject GetClassLoader(JNIEnv *env) {
    auto thread = FindThreadByName(env, "Client thread");
    if (thread == nullptr) {
        Debug(L"Client thread null");
        return nullptr;
    }

    auto class_loader_method = env->GetMethodID(env->FindClass("java/lang/Thread"), "getContextClassLoader",
                                                "()Ljava/lang/ClassLoader;");
    auto class_loader = env->CallObjectMethod(thread, class_loader_method);

    return class_loader;
}

void InjectClasses(JNIEnv *env) {
    auto class_loader = GetClassLoader(env);
    if (class_loader == nullptr) {
        Debug(L"Not found client class loader");
        return;
    }

    for (int i = 0; i < sizeof(classes_sizes) / 4; i++) {
        auto class_bytes = classes_bytes[i];
        auto class_size = classes_sizes[i];

        auto defined_class = env->DefineClass(nullptr, class_loader, class_bytes, class_size);
        if (defined_class == nullptr) {
            defined_class = env->FindClass("CristalixModLoader");
            if (defined_class == nullptr) {
                Debug(L"Couldn`t define class");
                continue;
            }
        }

        auto inject_method = env->GetStaticMethodID(defined_class, "inject", "()V");
        if (inject_method == nullptr) {
            Debug(L"Not found inject method");
            continue;
        }
        env->CallStaticVoidMethod(defined_class, inject_method);
    }
}
