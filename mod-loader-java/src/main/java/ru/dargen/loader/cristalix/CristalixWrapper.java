package ru.dargen.loader.cristalix;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

import static ru.dargen.loader.cristalix.CristalixMappings.*;
import static ru.dargen.loader.util.Access.LOOKUP;


//TODO: Make it with original cristalix dependencies
@UtilityClass
public class CristalixWrapper {

    public static Object getMinecraft() throws Throwable {
        return MINECRAFT_INSTANCE_MH.invoke();
    }

    public static Object getModManager(Object minecraft) throws Throwable {
        return MOD_MANAGER_INSTANCE_MH.invoke(minecraft);
    }

    public static Object getClientApi(Object modManager) throws Throwable {
        return CLIENT_API_INSTANCE_MH.invoke(modManager);
    }

    public static Object load(Object modManager, Object clientApi, InputStream inputStream,
                               Object side, boolean flag) throws Throwable {
        return LOAD_MOD_MH.invoke(modManager, clientApi, inputStream, side, flag);
    }

    public static void unload(Object mod) throws Throwable {
        UNLOAD_MOD_MH.invoke(mod);
    }

    public static void postMainThread(Object minecraft, Runnable runnable) throws Throwable {
        POST_MAIN_THREAD_MH.invoke(minecraft, runnable);
    }
    
    static {
        loadClasses();
        loadMethodHandles();
    }

    public MethodHandle MINECRAFT_INSTANCE_MH;
    public MethodHandle MOD_MANAGER_INSTANCE_MH;
    public MethodHandle CLIENT_API_INSTANCE_MH;

    public MethodHandle POST_MAIN_THREAD_MH;
    public MethodHandle LOAD_MOD_MH;
    public MethodHandle UNLOAD_MOD_MH;
    
    @SneakyThrows
    private void loadMethodHandles() {
        MINECRAFT_INSTANCE_MH = LOOKUP.findStaticGetter(MINECRAFT_CLASS, MINECRAFT_INSTANCE_FIELD, Object.class);
        MOD_MANAGER_INSTANCE_MH = LOOKUP.findGetter(MINECRAFT_CLASS, MOD_MANAGER_INSTANCE_FIELD, Object.class);
        CLIENT_API_INSTANCE_MH = LOOKUP.findGetter(MOD_MANAGER_CLASS, CLIENT_INSTANCE_FIELD, Object.class);

        POST_MAIN_THREAD_MH = LOOKUP.findVirtual(MINECRAFT_CLASS, MINECRAFT_POST_MAIN_THREAD,
                MethodType.methodType(Class.forName("com.google.common.util.concurrent.ListenableFuture"), Runnable.class));
        LOAD_MOD_MH = LOOKUP.findStatic(MOD_LOADER_CLASS, MOD_LOADER_LOAD_METHOD,
                MethodType.methodType(MOD_CLASS, MOD_MANAGER_CLASS, CLIENT_API_CLASS, InputStream.class, SIDE_CLASS, boolean.class));
        UNLOAD_MOD_MH = LOOKUP.findStatic(MOD_LOADER_CLASS, MOD_LOADER_UNLOAD_METHOD,
                MethodType.methodType(void.class, MOD_MAIN_CLASS));
    }

    public Class<?> MINECRAFT_CLASS;
    public Class<?> MOD_MANAGER_CLASS;
    public Class<?> MOD_LOADER_CLASS;
    public Class<?> MOD_CLASS;

    public Class<?> CLIENT_API_CLASS;
    public Class<?> MOD_MAIN_CLASS;
    public Class<?> SIDE_CLASS;

    @SneakyThrows
    private void loadClasses() {
        MINECRAFT_CLASS = Class.forName(MINECRAFT_CLASS_NAME);
        MOD_MANAGER_CLASS = Class.forName(MOD_MANAGER_CLASS_NAME);
        MOD_LOADER_CLASS = Class.forName(MOD_LOADER_CLASS_NAME);
        MOD_CLASS = Class.forName(MOD_CLASS_NAME);

        CLIENT_API_CLASS = Class.forName(CLIENT_API_CLASS_NAME);
        MOD_MAIN_CLASS = Class.forName(MOD_MAIN_CLASS_NAME);
        SIDE_CLASS = Class.forName(SIDE_CLASS_NAME);
    }

}
