package org.example.dataprovider.logger;

import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.example.core.port.ProcessBannedIp;

@Slf4j
public class DefaultLoggerDataProvider implements ProcessBannedIp {

    @Override
    public Flowable<String> processBannedIp(String ip, String message) {
        log.info(message);
        return Flowable.just(ip);
    }
}
