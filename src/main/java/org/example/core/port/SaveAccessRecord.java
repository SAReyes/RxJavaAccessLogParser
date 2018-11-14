package org.example.core.port;

import org.example.domain.AccessRecord;
import reactor.core.publisher.Mono;

public interface SaveAccessRecord {

    Mono<AccessRecord> saveAccessRecord(AccessRecord accessRecord);
}
