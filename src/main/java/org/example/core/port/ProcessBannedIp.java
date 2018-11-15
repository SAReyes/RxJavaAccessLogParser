package org.example.core.port;

import io.reactivex.Flowable;

public interface ProcessBannedIp {

    Flowable<String> processBannedIp(String ip, String message);
}
