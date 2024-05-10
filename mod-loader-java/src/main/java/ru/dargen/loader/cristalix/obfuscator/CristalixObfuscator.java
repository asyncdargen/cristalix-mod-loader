package ru.dargen.loader.cristalix.obfuscator;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.dargen.loader.mapping.Mappings;
import ru.dargen.loader.transformer.ClassTransformer;
import ru.dargen.loader.transformer.InstructionTransformer;
import ru.dargen.loader.util.Asm;
import ru.dargen.loader.util.Jars;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Properties;

@UtilityClass
public class CristalixObfuscator {

    private ClassTransformer transformer;

    @SneakyThrows
    public InputStream transformMod(Path modPath) {
        val entries = Jars.readJar(Files.newInputStream(modPath));

        if (!entries.containsKey("mod.properties")) {
            return Files.newInputStream(modPath);
        }

        val properties = new Properties();
        properties.load(new ByteArrayInputStream(entries.get("mod.properties")));

        if ("true".equals(properties.getProperty("mappings"))) {
            transformClasses(entries);
            Files.copy(Jars.writeJar(entries), CristalixObfuscatorData.MODS_PATH.resolve(modPath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            return Jars.writeJar(entries);
        }

        return Files.newInputStream(modPath);
    }

    private static void transformClasses(Map<String, byte[]> entries) {
        initTransformer();
        entries.entrySet().stream()
                .filter(entry -> entry.getKey().endsWith(".class"))
                .forEach(entry -> entries.put(
                        entry.getKey(),
                        transformClass(Jars.getEntryClassName(entry.getKey()), entry.getValue())
                ));
    }

    private static byte[] transformClass(String className, byte[] bytecode) {
        val classNode = Asm.readClass(bytecode);
        transformer.transform(classNode);
        return Asm.toByteCode(classNode);
    }

    private void initTransformer() {
        if (transformer == null) {
            CristalixObfuscatorData.init();

            val mappings = new Mappings(CristalixObfuscatorData.MAPPINGS_PATH);
            val instructionTransformer = new InstructionTransformer(mappings);
            transformer = new ClassTransformer(mappings, instructionTransformer);
        }
    }

}
