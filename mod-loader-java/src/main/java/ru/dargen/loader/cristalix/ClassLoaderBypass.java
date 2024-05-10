package ru.dargen.loader.cristalix;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ClassLoaderBypass {

    private ClassLoader baseClassLoader;

    public void bypass() {
        baseClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void back() {
        Thread.currentThread().setContextClassLoader(baseClassLoader);
    }

}
