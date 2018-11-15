package org.example.core.port;

import io.reactivex.Flowable;

public interface CountAccessRecords {

    Flowable<Integer> countAccessLog();
}
