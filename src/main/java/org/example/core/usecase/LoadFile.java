package org.example.core.usecase;

import io.reactivex.Flowable;

public interface LoadFile {

    Flowable<Integer> loadFile(String filename);
}
