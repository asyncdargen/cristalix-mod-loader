cmake_minimum_required(VERSION 3.28)
project(mod_loader)

set(CMAKE_CXX_STANDARD 14)

add_library(mod_loader SHARED dll/main.cpp dll/injector/injector.cpp dll/class/class_injector.cpp
        dll/class/classes.h)

set_target_properties(mod_loader PROPERTIES
        OUTPUT_NAME "loader"
        RUNTIME_OUTPUT_DIRECTORY "../out"
        PREFIX "")

set(INJECTING_CLASS_PATH "../loader")
set(JAVA_VERSION "1.8")

set(JAVA_DIR "../java")

add_custom_target(compile_injecting_classes
        COMMAND ${CMAKE_COMMAND} -E chdir ${INJECTING_CLASS_PATH} javac -source ${JAVA_VERSION} -target ${JAVA_VERSION} *.java
        COMMENT "Compile injecting classes"
)
add_custom_target(compile_header_preparer_class
        COMMAND ${CMAKE_COMMAND} -E chdir ${JAVA_DIR} javac -source ${JAVA_VERSION} -target ${JAVA_VERSION} JavaClassPreparer.java
        COMMENT "Compile header preparer class"
)
add_custom_target(prepare_injecting_classes_header
        COMMAND ${CMAKE_COMMAND} -E chdir ${JAVA_DIR} java JavaClassPreparer ${INJECTING_CLASS_PATH}
        DEPENDS compile_injecting_classes compile_header_preparer_class
        COMMENT "Prepare injecting classes header"
)

add_dependencies(mod_loader prepare_injecting_classes_header)