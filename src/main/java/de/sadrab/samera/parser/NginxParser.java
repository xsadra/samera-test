package de.sadrab.samera.parser;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NginxParser {

    public List<String> getListOfRequests() {

        String path = "src/main/java/de/sadrab/samera/nginx/nginx.log";

        try {
            return Files.lines(Paths.get(path))
                    .map(line -> line.split("\""))
                    .filter(item -> item.length > 2)
                    .map(item -> item[1])
                    .map(item -> item.replace(" HTTP/1.1", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
