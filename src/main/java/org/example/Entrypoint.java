package org.example;

import io.airlift.airline.Command;
import io.airlift.airline.Help;
import io.airlift.airline.Option;
import io.airlift.airline.ParseOptionMissingException;
import io.airlift.airline.ParseOptionMissingValueException;
import io.airlift.airline.SingleCommand;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Slf4j
@Command(name = "parse", description = "Parse the given access log, upload it to the database if required and get suspicious ips")
public class Entrypoint {

    @Option(name = {"-f", "--file"}, description = "access log file")
    private String file;

    @Option(name = {"-dburl"}, description = "Database url, e.g. jdbc:mysql://localhost:3306/logs")
    private String databaseUrl = "jdbc:mysql://localhost:3306/logs";

    @Option(name = {"-dbuser"}, description = "Database user")
    private String databaseUser = "logs";

    @Option(name = {"-dbpwd"}, description = "Database pwd")
    private String databasePassword = "logs";

    @Option(name = {"--startDate"}, description = "initial date", required = true)
    private String startDate;

    @Option(name = {"--duration"}, description = "duration", required = true, allowedValues = {"daily", "hourly"})
    private String duration;

    @Option(name = {"--threshold"}, description = "count threshold", required = true)
    private int threshold;

    public static void main(String[] args) throws ParseException, FileNotFoundException {
        Entrypoint app = getParams(args);

        var connectionProvider = ConnectionProvider.from(
                app.databaseUrl,
                app.databaseUser,
                app.databasePassword
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
                .blockingSubscribe(it -> log.info("CREATE TABLES IF NOT EXIST"));

        // Seed DB
        if (0 == databaseDataProvider.countAccessLog().blockingFirst()) {
            var inputStream = app.file == null ? null//Entrypoint.class.getResourceAsStream("access.log")
                    : new FileInputStream(new File(app.file));

            loadFile.loadFile(inputStream)
                    .window(5000)
                    .blockingSubscribe(it -> log.info("Saving item '" + it.lastElement().blockingGet() + "'"));
        }

        // Parse Request
        getSuspiciousIps.getSuspiciousIps(
                new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss").parse(app.startDate),
                "daily".equals(app.duration) ? Duration.DAILY : Duration.HOURLY,
                app.threshold
        ).blockingSubscribe();

        log.info("DONE");
        System.exit(0);
    }

    private static Entrypoint getParams(String[] args) {
        var cmd = SingleCommand.singleCommand(Entrypoint.class);
        try {
            return cmd.parse(args);
        } catch (ParseOptionMissingException | ParseOptionMissingValueException e) {
            Help.help(cmd.getCommandMetadata());
            System.exit(-1);
            return new Entrypoint();
        }
    }
}
