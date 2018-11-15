package org.example.core.port;

import io.reactivex.Flowable;
import org.example.domain.Duration;

import java.util.Date;

public interface FindIpsBy {

    Flowable<String> findIpsByDateDurationAndThreshold(Date startDate, Duration duration, int threshold);
}
