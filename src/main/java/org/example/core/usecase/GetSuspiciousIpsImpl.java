package org.example.core.usecase;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.example.core.port.FindIpsBy;
import org.example.core.port.ProcessBannedIp;
import org.example.domain.Duration;

import java.util.Date;

public class GetSuspiciousIpsImpl implements GetSuspiciousIps {

    private FindIpsBy findIpsBy;
    private ProcessBannedIp logBannedIp;
    private ProcessBannedIp saveBannedIp;

    public GetSuspiciousIpsImpl(FindIpsBy findIpsBy, ProcessBannedIp logBannedIp, ProcessBannedIp saveBannedIp) {
        this.findIpsBy = findIpsBy;
        this.logBannedIp = logBannedIp;
        this.saveBannedIp = saveBannedIp;
    }

    @Override
    public Flowable<String> getSuspiciousIps(Date startDate, Duration duration, int threshold) {
        var durationMessage = duration.equals(Duration.DAILY) ? "a day" : "an hour";
        var bannedMessage = "Ip '%s' hit the server for '%d' times at " + startDate.toString()
                + " within " + durationMessage;
        return findIpsBy.findIpsByDateDurationAndThreshold(startDate, duration, threshold)
                .map(it -> Tuple2.create(it._1(), String.format(bannedMessage, it._1(), it._2())))
                .flatMap(it ->
                        saveBannedIp.processBannedIp(it._1(), it._2())
                                .zipWith(
                                        logBannedIp.processBannedIp(it._1(), it._2()),
                                        (save, log) -> save
                                )
                );
    }
}
