package org.example.dataprovider.csv;

import reactor.core.publisher.Flux;

import java.io.File;

public interface ReadFileLines {

    Flux<String> readFileLines(File file);
}
