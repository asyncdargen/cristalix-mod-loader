package ru.dargen.loader.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

@UtilityClass
public class Jars {

    @SneakyThrows
    public Map<String, byte[]> readJar(InputStream inputStream) {
        val entries = new HashMap<String, byte[]>();
        try (val jis = new JarInputStream(inputStream)) {
            JarEntry entry;

            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory()) entries.put(entry.getName(), readAllBytes(jis));
            }
        }

        return entries;
    }

    @SneakyThrows
    public InputStream writeJar(Map<String, byte[]> entries) {
        try (val baos = new ByteArrayOutputStream(); val jos = new JarOutputStream(baos)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                val name = entry.getKey();
                val bytes = entry.getValue();

                jos.putNextEntry(new JarEntry(name));
                jos.write(bytes);
                jos.closeEntry();
            }

            return new ByteArrayInputStream(baos.toByteArray());
        }
    }

    public String getEntryClassName(String entry) {
        return entry.substring(0, entry.lastIndexOf('.'));
    }

    @SneakyThrows
    public void addAppClassPath(Path path) {
        val classLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader();

        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, path.toUri().toURL());
    }

    private final byte[] BUFFER = new byte[4096];

    @SneakyThrows
    public byte[] readAllBytes(InputStream stream) {
        int count;
        try (val baos = new ByteArrayOutputStream()) {
            while ((count = stream.read(BUFFER)) != -1) {
                baos.write(BUFFER, 0, count);
            }

            return baos.toByteArray();
        }
    }

}
