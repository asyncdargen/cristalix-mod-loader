package ru.dargen.loader.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class File {

    @SneakyThrows
    public List<Path> collectFilesTree(Path path) {
        val files = new ArrayList<Path>();
        if (!Files.isDirectory(path)) files.add(path);
        else Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                files.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }

}
