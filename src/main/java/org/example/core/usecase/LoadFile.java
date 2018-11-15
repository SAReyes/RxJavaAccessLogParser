package org.example.core.usecase;

import io.reactivex.Flowable;

import java.io.InputStream;

public interface LoadFile {

    Flowable<Integer> loadFile(InputStream inputStream);
}
