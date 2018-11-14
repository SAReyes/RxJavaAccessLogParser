package org.example.core.port;

import io.reactivex.Flowable;

public interface CreateSchema {

    Flowable<Integer> createSchema();
}
