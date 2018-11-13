package org.example.dataprovider.nginx;

import org.example.domain.AccessRecord;
import reactor.core.publisher.Flux;

import java.io.File;

public interface ReadNginxLog {

    Flux<AccessRecord> readNginxLog(File file);
}
