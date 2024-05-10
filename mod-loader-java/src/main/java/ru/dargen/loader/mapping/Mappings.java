package ru.dargen.loader.mapping;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.dargen.loader.util.File;
import ru.dargen.loader.util.RegEx;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Getter
@RequiredArgsConstructor
public class Mappings {

    private static final Pattern CLASS_NAMES_BRAKES_PATTERN = Pattern.compile("<L(.*?);>");
    private static final Pattern CLASS_NAMES_PATTERN = Pattern.compile("L(.*?);");


    private final Map<String, ClassMapping> mappings;

    @SneakyThrows
    public Mappings(Path path) {
        this(new HashMap<>());

        File.collectFilesTree(path)
                .forEach(file -> MappingReader.readMappings(file, mappings));
    }

    public ClassMapping getMapping(String className) {
        return mappings.get(className);
    }

    public String mapClassName(String className) {
        val mapping = getMapping(className);
        return mapping != null ? mapping.getActual() : className;
    }

    public List<String> mapClassesNames(List<String> classesNames) {
        classesNames.replaceAll(this::mapClassName);

        return classesNames;
    }

    public String mapClassNames(String classNames) {
        classNames = RegEx.replace(classNames, CLASS_NAMES_BRAKES_PATTERN, match -> "<L" + mapClassName(match.group(1)) + ";>");
        classNames = RegEx.replace(classNames, CLASS_NAMES_PATTERN, match -> "L" + mapClassName(match.group(1)) + ";");

        return classNames;
    }

}
