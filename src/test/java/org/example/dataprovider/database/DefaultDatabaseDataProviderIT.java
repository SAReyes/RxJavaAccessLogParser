package org.example.dataprovider.database;

import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.example.core.usecase.LoadFileImpl;
import org.example.dataprovider.accessLog.DefaultAccessLogDataProvider;
import org.example.dataprovider.csv.DefaultCsvDataProvider;
import org.example.domain.AccessRecord;
import org.example.domain.Duration;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultDatabaseDataProviderIT {

    private DefaultDatabaseDataProvider sut;

    @Before
    public void setUp() {
        var connectionProvider = ConnectionProvider.from(
                "jdbc:mysql://localhost:3306/metadata",
                "metadata",
                "metadata"
        );
        Database db = Database.from(Pools.nonBlocking()
                .connectionProvider(connectionProvider)
                .build());
        sut = new DefaultDatabaseDataProvider(db);
    }

    @Test
    @Ignore
    public void create_schema() throws InterruptedException {
        sut.createSchema()
                .test()
                .await()
                .assertComplete()
                .assertResult(0);
    }

    @Test
    @Ignore
    public void insert_record() throws InterruptedException {
        var now = Date.from(new Date().toInstant());
        var record = AccessRecord.builder()
                .ip("192.168.0.1")
                .userAgent("Fancy UA")
                .status(200)
                .request("GET /")
                .accessDate(now)
                .build();

        sut.saveAccessRecord(record)
                .test()
                .await()
                .assertComplete()
                .assertResult(1);
    }

    @Test
    @Ignore
    public void load_full_log() {
        var file = this.getClass().getResource("access.log").getPath();
        var csvProvider = new DefaultCsvDataProvider(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        var readAccessLog = new DefaultAccessLogDataProvider(csvProvider, csvProvider);
        var loadFile = new LoadFileImpl(readAccessLog, sut);

        var result = loadFile.loadFile(file)
                .subscribe(System.out::println);

        while (!result.isDisposed()) {
        }
    }

    @Test
    @Ignore
    public void find_blocked_ips() throws ParseException, InterruptedException {
        sut
                .findIpsByDateDurationAndThreshold(
                        new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").parse("2017-01-01.15:00:00"),
                        Duration.HOURLY,
                        200
                )
                .test()
                .await()
                .assertResult(
                        Tuple2.create("192.168.106.134", 232),
                        Tuple2.create("192.168.11.231", 211)
                );
    }

    @Test
    @Ignore
    public void inserts_blocked_ip() throws InterruptedException {
        sut.processBannedIp("192.168.1.1", "The gateway is banned")
                .test()
                .await()
                .assertComplete();
    }
}
