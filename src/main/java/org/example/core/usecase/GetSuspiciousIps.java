package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.domain.Duration;

import java.util.Date;

public interface GetSuspiciousIps {

    Flowable<String> getSuspiciousIps(Date startDate, Duration duration, int threshold);
}
