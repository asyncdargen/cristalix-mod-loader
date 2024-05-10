package ru.dargen.loader.mapping;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@UtilityClass
public class MappingReader {

    private final Gson GSON = new Gson();
    private final TypeToken<Map<String, ClassMapping>> MAP_TYPE = new TypeToken<Map<String, ClassMapping>>() {};

    @SneakyThrows
    public void readMappings(Path path, Map<String, ClassMapping> mappingMap) {
        try (val reader = new BufferedReader(new InputStreamReader(Files.newInputStream(path)))) {
            Map<String, ClassMapping> mappings = GSON.fromJson(reader, MAP_TYPE.getType());
            mappingMap.putAll(mappings);
            System.out.println("Loaded mappings from " + path.getFileName().toString() + ": " + mappingMap.size());
        }
    }

}
