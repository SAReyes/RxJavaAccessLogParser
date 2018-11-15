package org.example.dataprovider.database;

import io.reactivex.Flowable;
import org.davidmoten.rx.jdbc.Database;
import org.davidmoten.rx.jdbc.tuple.Tuple2;
import org.example.core.port.CountAccessRecords;
import org.example.core.port.CreateSchema;
import org.example.core.port.FindIpsBy;
import org.example.core.port.ProcessBannedIp;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;
import org.example.domain.Duration;

import java.util.Calendar;
import java.util.Date;

public class DefaultDatabaseDataProvider implements SaveAccessRecord, CreateSchema, FindIpsBy, ProcessBannedIp,
        CountAccessRecords {

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
                "    access_date DATETIME," +
                "    ip varchar(255)," +
                "    request varchar(255)," +
                "    status int," +
                "    user_agent varchar(255)," +
                "    PRIMARY KEY (access_id)" +
                ");")
                .counts();

        var bansCounts = db.update("CREATE TABLE IF NOT EXISTS BANNED_IP (" +
                "   banned_id int NOT NULL AUTO_INCREMENT," +
                "   ip varchar(255)," +
                "   comment text," +
                "   created_date DATETIME," +
                "   PRIMARY KEY (banned_id)" +
                ")")
                .counts();

        return accessCounts.zipWith(bansCounts, (access, bans) -> access + bans);
    }

    @Override
    public Flowable<Tuple2<String, Integer>> findIpsByDateDurationAndThreshold(Date startDate, Duration duration, int threshold) {
        return db.select("select ip, count(*) as cnt from ACCESS_LOG" +
                " where access_date > ? and access_date < ?" +
                " group by ip" +
                " having cnt > ?")
                .parameters(startDate, getEndDate(startDate, duration), threshold)
                .getAs(String.class, Integer.class);
    }

    @Override
    public Flowable<String> processBannedIp(String ip, String message) {
        return db
                .update("INSERT INTO BANNED_IP(ip,comment,created_date) values(?,?,?)")
                .parameters(ip, message, new Date())
                .counts()
                .map(it -> ip);
    }

    @Override
    public Flowable<Integer> countAccessLog() {
        return db
                .select("select count(*) from ACCESS_LOG")
                .getAs(Integer.class);
    }

    private Date getEndDate(Date startDate, Duration duration) {
        var cal = Calendar.getInstance();
        cal.setTime(startDate);

        cal.add(duration.equals(Duration.DAILY) ? Calendar.DATE : Calendar.HOUR, +1);

        return cal.getTime();
    }
}
