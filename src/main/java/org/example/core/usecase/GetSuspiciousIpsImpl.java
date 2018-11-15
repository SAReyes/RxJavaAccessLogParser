package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.core.port.FindIpsBy;
import org.example.domain.Duration;

import java.util.Date;

public class GetSuspiciousIpsImpl implements GetSuspiciousIps {

    private FindIpsBy findIpsBy;

    public GetSuspiciousIpsImpl(FindIpsBy findIpsBy) {
        this.findIpsBy = findIpsBy;
    }

    @Override
    public Flowable<String> getSuspiciousIps(Date startDate, Duration duration, int threshold) {
        return findIpsBy.findIpsByDateDurationAndThreshold(startDate, duration, threshold);
    }
}
