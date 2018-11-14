package org.example.core.usecase;

import org.example.domain.AccessRecord;
import reactor.core.publisher.Flux;

import java.io.File;

public interface LoadFile {

    Flux<AccessRecord> loadFile(File file);
}
