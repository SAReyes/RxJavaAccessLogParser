package org.example.core.port;

import io.reactivex.Flowable;
import org.example.domain.AccessRecord;

public interface ReadAccessLog {

    Flowable<AccessRecord> readAccessLog(String filename);
}
