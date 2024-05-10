package ru.dargen.loader.cristalix.obfuscator;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import ru.dargen.loader.util.Downloader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class CristalixObfuscatorData {

    private final String REPO_URL = "https://raw.githubusercontent.com/asyncdargen/cristalix-obfuscator/master/";

    private final List<String> LIBRARIES_NAMES = Arrays.asList(
            "minecraft",
            "libraries/core-mod-1.12.2",
            "libraries/font-mod-1.12.2",
            "libraries/keybind-mod-1.12.2",
            "libraries/p13n-minecraft-mod-1.12.2"
    );

    private final Path DATA_PATH = Paths.get(System.getProperty("user.home"), ".cristalix-loader");
    public final Path MAPPINGS_PATH = DATA_PATH.resolve("mappings");
    public final Path MODS_PATH = DATA_PATH.resolve("mods");

    private boolean initialized = false;

    public void init() {
        if (initialized) return;
        initialized = true;

        initPaths();
        updateMappings();
    }

    @SneakyThrows
    private void initPaths() {
        if (!Files.exists(DATA_PATH)) Files.createDirectories(DATA_PATH);
        if (!Files.exists(MAPPINGS_PATH)) Files.createDirectories(MAPPINGS_PATH);
        if (!Files.exists(MODS_PATH)) Files.createDirectories(MODS_PATH);
    }

    @SneakyThrows
    private void updateMappings() {
        for (String library : LIBRARIES_NAMES) {
            val path = MAPPINGS_PATH.resolve(library + ".json");
            val url = REPO_URL + "mappings/" + library + ".json";

            Downloader.download(url, path);
        }
    }

}
