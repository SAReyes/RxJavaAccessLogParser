package org.example.core.port;

import io.reactivex.Flowable;
import org.example.domain.AccessRecord;

public interface SaveAccessRecord {

    Flowable<Integer> saveAccessRecord(AccessRecord accessRecord);
}
