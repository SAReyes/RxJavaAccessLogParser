package org.example.dataprovider.csv;

import io.reactivex.Flowable;

import java.io.InputStream;

public interface ReadFileLines {

    Flowable<String> readFileLines(InputStream inputStream);
}
