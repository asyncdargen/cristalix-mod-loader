
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.joining;

public class JavaClassPreparer {

    public static void main(String[] args) throws IOException {
        Path jarPath = Paths.get(args[0]);
        List<byte[]> classes = readJar(jarPath).entrySet().stream()
                .peek(file -> {
                    System.out.println("Preparing header for " + file.getKey());
                })
                .map(Map.Entry::getValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        String holderFileContent = buildClassesHolder(classes);
        Path holderFile = Paths.get("../dll/class/classes.h");

        writeString(holderFile, holderFileContent);
    }

    private static String buildClassesHolder(List<byte[]> classesBytes) {
        StringBuilder builder = new StringBuilder();

        builder
                .append("#ifndef CLASSES_H").append("\n")
                .append("#define CLASSES_H").append("\n\n")

                .append("#include \"../jvm/jni.h\"").append("\n\n");

        for (int i = 0; i < classesBytes.size(); i++) {
            builder.append("const jbyte class_bytes_").append(i).append("[] = ")
                .append(toBytesArrayString(classesBytes.get(i))).append(";\n");
        };

        builder
                .append("const jbyte* classes_bytes[] = {")
                .append(IntStream.range(0, classesBytes.size()).mapToObj(Integer::toString).map("class_bytes_"::concat).collect(joining(", ")))
                .append("};").append("\n")

                .append("const jsize classes_sizes[] = {")
                .append(classesBytes.stream().map(bytes -> Integer.toString(bytes.length)).collect(joining(", ")))
                .append("};").append("\n\n")

                .append("#endif").append("\n\n");

        return builder.toString();
    }

    private static String toBytesArrayString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int i = 0; i < bytes.length; i++) {
            builder.append(bytes[i]);
            if (i < bytes.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("}");

        return builder.toString();
    }

    private static void writeString(Path path, String text) throws IOException {
        Files.writeString(path, text, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static Map<String, byte[]> readJar(Path path) {
        Map<String, byte[]> entries = new HashMap<String, byte[]>();
        try (JarInputStream jis = new JarInputStream(Files.newInputStream(path))) {
            JarEntry entry;

            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory()) entries.put(entry.getName(), readAllBytes(jis));
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return entries;
    }

    private static byte[] readAllBytes(InputStream inputStream) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ) {
            byte[] buffer = new byte[2048];
            int read = -1;
            while ((read = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }

            return baos.toByteArray();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

}