package org.example;

import lombok.extern.slf4j.Slf4j;
import org.davidmoten.rx.jdbc.ConnectionProvider;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.pool.Pools;
import org.example.core.usecase.GetSuspiciousIpsImpl;
import org.example.core.usecase.LoadFileImpl;
import org.example.dataprovider.accessLog.DefaultAccessLogDataProvider;
import org.example.dataprovider.csv.DefaultCsvDataProvider;
import org.example.dataprovider.database.DefaultDatabaseDataProvider;
import org.example.dataprovider.logger.DefaultLoggerDataProvider;
import org.example.domain.Duration;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
public class Entrypoint {

    public static void main(String[] args) throws ParseException {
        var connectionProvider = ConnectionProvider.from(
                "jdbc:mysql://localhost:3306/metadata",
                "metadata",
                "metadata"
        );
        Database db = Database.from(Pools.nonBlocking()
                .connectionProvider(connectionProvider)
                .build());

        var databaseDataProvider = new DefaultDatabaseDataProvider(db);

        var csvDataProvider = new DefaultCsvDataProvider(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        var accessLogDataProvider = new DefaultAccessLogDataProvider(csvDataProvider, csvDataProvider);
        var loadFile = new LoadFileImpl(accessLogDataProvider, databaseDataProvider);

        var loggerDataProvider = new DefaultLoggerDataProvider();
        var getSuspiciousIps = new GetSuspiciousIpsImpl(databaseDataProvider, loggerDataProvider, databaseDataProvider);

        // Create DB
        databaseDataProvider.createSchema()
                .blockingSubscribe(it -> log.info("Schema created"));

        // Seed DB
//        loadFile.loadFile(Entrypoint.class.getResource("/access.log").getPath()).blockingSubscribe();

        // Parse Request
        getSuspiciousIps.getSuspiciousIps(
                new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").parse("2017-01-01.15:00:00"),
                Duration.DAILY,
                250
        ).blockingSubscribe();

        log.info("Finished");
    }
}
