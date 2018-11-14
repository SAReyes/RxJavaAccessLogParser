package org.example.core.port;

import io.reactivex.Flowable;
import org.example.domain.AccessRecord;

public interface ReadNginxLog {

    Flowable<AccessRecord> readNginxLog(String filename);
}
