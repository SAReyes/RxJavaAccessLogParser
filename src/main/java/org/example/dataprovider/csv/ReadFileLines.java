package org.example.dataprovider.csv;

import io.reactivex.Flowable;

public interface ReadFileLines {

    Flowable<String> readFileLines(String filename);
}
