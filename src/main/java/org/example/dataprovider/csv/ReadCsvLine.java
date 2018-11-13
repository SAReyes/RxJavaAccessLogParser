package org.example.dataprovider.csv;

import org.example.domain.AccessRecord;
import reactor.core.publisher.Mono;

public interface ReadCsvLine {

    Mono<AccessRecord> readCsvLine(String csvLine);
}
