package org.example.dataprovider.database;

import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.example.domain.AccessRecord;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

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
}
