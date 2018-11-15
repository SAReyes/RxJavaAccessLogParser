package org.example.core.port;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.example.domain.Duration;

import java.util.Date;

public interface FindIpsBy {

    Flowable<Tuple2<String, Integer>> findIpsByDateDurationAndThreshold(Date startDate, Duration duration, int threshold);
}
