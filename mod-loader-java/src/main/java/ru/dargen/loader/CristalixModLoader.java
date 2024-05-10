package ru.dargen.loader;

import lombok.SneakyThrows;
import ru.dargen.loader.cristalix.obfuscator.CristalixObfuscator;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.dargen.loader.cristalix.CristalixWrapper.*;

public class CristalixModLoader {

    private static final List<Object> loadedMods = new ArrayList<>();

    @SneakyThrows
    public static void inject() {
        System.out.println("Cristalix Mod Loader by dargen (https://github.com/asyncdargen)");
        if (!loadedMods.isEmpty()) {
            System.out.println("Loaded mods " + loadedMods.size() + ", unloading...");

            for (Object mod : loadedMods) {
                try {
                    unload(mod);
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
            loadedMods.clear();
        }

        List<Path> modFiles = new ArrayList<>();
        collectMods(modFiles, Paths.get("C:/cristalix-mods"));
        collectMods(modFiles, Paths.get("C:/Xenoceal/mods"));

        Object minecraft = getMinecraft();
        Object modManager = getModManager(minecraft);
        Object clientApi = getClientApi(modManager);

        postMainThread(minecraft, () -> {
            for (Path mod : modFiles) {
                try (InputStream is = CristalixObfuscator.transformMod(mod)) {
                    System.out.println("Loading mod " + mod);
                    Object loadedMod = load(modManager, clientApi, is, null, false); //flag - don`t override already loaded mod
                    loadedMods.add(loadedMod);
                } catch (Throwable t) {
                    System.out.println("Error while mod loading " + mod);
                    t.printStackTrace();
                }
            }
        });
    }

    private static void collectMods(List<Path> paths, Path folder) throws Throwable {
        if (!Files.exists(folder)) return;

        Files.list(folder)
                .filter(file -> file.getFileName().toString().endsWith(".jar"))
                .forEach(paths::add);
    }

}