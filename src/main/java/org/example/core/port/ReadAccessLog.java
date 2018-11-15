package org.example.core.port;

import io.reactivex.Flowable;
import org.example.domain.AccessRecord;

import java.io.InputStream;

public interface ReadAccessLog {

    Flowable<AccessRecord> readAccessLog(InputStream inputStream);
}
