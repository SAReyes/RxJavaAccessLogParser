package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.domain.AccessRecord;

public interface LoadFile {

    Flowable<AccessRecord> loadFile(String filename);
}
