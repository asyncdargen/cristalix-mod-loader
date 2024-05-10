package ru.dargen.loader.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class Downloader {

    @SneakyThrows
    public void download(String url, Path path) {
        if (path.getParent() != null) {
            if (!Files.exists(path.getParent())) {
                Files.createDirectories(path.getParent());
            }
        }

        val connection = openConnection(url);
        long fileSize = Files.exists(path) ? Files.size(path) : 0;
        long remoteFileSize = connection.getHeaderFieldLong("Content-Length", -1);

        if (fileSize != remoteFileSize) {
            Files.copy(connection.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @SneakyThrows
    private HttpURLConnection openConnection(String url) {
        return (HttpURLConnection) new URL(url).openConnection();
    }

}
