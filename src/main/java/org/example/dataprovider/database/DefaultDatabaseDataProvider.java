package org.example.dataprovider.database;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.Database;
import org.example.core.port.CreateSchema;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;

public class DefaultDatabaseDataProvider implements SaveAccessRecord, CreateSchema {

    private Database db;

    public DefaultDatabaseDataProvider(Database db) {
        this.db = db;
    }

    @Override
    public Flowable<Integer> saveAccessRecord(AccessRecord accessRecord) {
        return db
                .update("insert into ACCESS_LOG(access_date, ip, request, status, user_agent) values(?,?,?,?,?)")
                .parameters(
                        accessRecord.getAccessDate(),
                        accessRecord.getIp(),
                        accessRecord.getRequest(),
                        accessRecord.getStatus(),
                        accessRecord.getUserAgent()
                )
                .returnGeneratedKeys()
                .getAs(Integer.class);
    }

    @Override
    public Flowable<Integer> createSchema() {
        var accessCounts = db.update("CREATE TABLE IF NOT EXISTS ACCESS_LOG (" +
                "    access_id int NOT NULL AUTO_INCREMENT," +
                "    access_date date," +
                "    ip varchar(255)," +
                "    request varchar(255)," +
                "    status int," +
                "    user_agent varchar(255)," +
                "    PRIMARY KEY (access_id)\n" +
                ");\n")
                .counts();

        var bansCounts = db.update("CREATE TABLE IF NOT EXISTS BANNED_IP (" +
                "   banned_id int NOT NULL AUTO_INCREMENT," +
                "   ip varchar(255) UNIQUE," +
                "   comment text," +
                "   PRIMARY KEY (banned_id)" +
                ")")
                .counts();

        return accessCounts.zipWith(bansCounts, (access, bans) -> access + bans);
    }
}
