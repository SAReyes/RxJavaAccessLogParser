package org.example.core.port;

import io.reactivex.Single;
import org.example.domain.AccessRecord;

public interface SaveAccessRecord {

    Single<AccessRecord> saveAccessRecord(AccessRecord accessRecord);
}
