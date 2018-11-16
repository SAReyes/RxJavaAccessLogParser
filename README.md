A small example using RxJava2 and my approach of CleanArchitecture

# Access Parser
This application uploads an access log into a MySQL database and queries for info about it.

# Usage
```
$ java -jar parser.jar                                                                                                                                                                                   [jvm:10.0.2]  [±master ●●]
NAME
        parse - Parse the given access log, upload it to the database if
        required and get suspicious ips

SYNOPSIS
        parse [-dbpwd <databasePassword>] [-dburl <databaseUrl>]
                [-dbuser <databaseUser>] --duration <duration>
                [(-f <file> | --file <file>)] --startDate <startDate>
                --threshold <threshold>

OPTIONS
        -dbpwd <databasePassword>
            Database pwd

        -dburl <databaseUrl>
            Database url, e.g. jdbc:mysql://localhost:3306/logs

        -dbuser <databaseUser>
            Database user

        --duration <duration>
            duration

        -f <file>, --file <file>
            access log file

        --startDate <startDate>
            initial date

        --threshold <threshold>
            count threshold
 ```
Default values for non-mandatory fields:
 * `-f <file>` - An example file is loaded from the resources
 * `-dburl <databaseUrl>` - jdbc:mysql://localhost:3306/logs
 * `-dbuser <user>` - logs
 * `-dbpwd <pwd>` - logs 

## Note
If the application detects some content on the ACCESS_LOG table, the log files will be ignored and no data will be 
added into the database

# Database
## ACCESS_LOG
```
+-------------+--------------+------+-----+---------+----------------+
| Field       | Type         | Null | Key | Default | Extra          |
+-------------+--------------+------+-----+---------+----------------+
| access_id   | int(11)      | NO   | PRI | NULL    | auto_increment |
| access_date | datetime     | YES  |     | NULL    |                |
| ip          | varchar(255) | YES  |     | NULL    |                |
| request     | varchar(255) | YES  |     | NULL    |                |
| status      | int(11)      | YES  |     | NULL    |                |
| user_agent  | varchar(255) | YES  |     | NULL    |                |
+-------------+--------------+------+-----+---------+----------------+
```
## BANNED_IP
```
+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| banned_id    | int(11)      | NO   | PRI | NULL    | auto_increment |
| ip           | varchar(255) | YES  |     | NULL    |                |
| comment      | text         | YES  |     | NULL    |                |
| created_date | datetime     | YES  |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+
```

# Example

```
$ java -jar parser.jar --duration daily --threshold 250 --startDate 2017-01-01.15:00:00
01:03:47.075 [main] INFO  Entrypoint - CREATE TABLES IF NOT EXIST
01:03:47.336 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.129.191' hit the server for '288' times at Sun Jan 01 15:00:00 CET 2017 within a day
01:03:47.337 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.143.177' hit the server for '275' times at Sun Jan 01 15:00:00 CET 2017 within a day
01:03:47.338 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.203.111' hit the server for '255' times at Sun Jan 01 15:00:00 CET 2017 within a day
01:03:47.338 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.38.77' hit the server for '277' times at Sun Jan 01 15:00:00 CET 2017 within a day
01:03:47.350 [main] INFO  Entrypoint - DONE
```
```
$ java -jar parser.jar --duration hourly --threshold 200 --startDate 2017-01-01.15:00:00
01:03:51.566 [main] INFO  Entrypoint - CREATE TABLES IF NOT EXIST
01:03:51.759 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.106.134' hit the server for '232' times at Sun Jan 01 15:00:00 CET 2017 within an hour
01:03:51.760 [pool-1-thread-2] INFO  DefaultLoggerDataProvider - Ip '192.168.11.231' hit the server for '211' times at Sun Jan 01 15:00:00 CET 2017 within an hour
01:03:51.765 [main] INFO  Entrypoint - DONE
```
Database:
```
mysql> select * from BANNED_IP;
+-----------+-----------------+----------------------------------------------------------------------------------------------------+---------------------+
| banned_id | ip              | comment                                                                                            | created_date        |
+-----------+-----------------+----------------------------------------------------------------------------------------------------+---------------------+
|         1 | 192.168.129.191 | Ip '192.168.129.191' hit the server for '288' times at Sun Jan 01 15:00:00 CET 2017 within a day   | 2018-11-16 01:03:47 |
|         2 | 192.168.143.177 | Ip '192.168.143.177' hit the server for '275' times at Sun Jan 01 15:00:00 CET 2017 within a day   | 2018-11-16 01:03:47 |
|         3 | 192.168.203.111 | Ip '192.168.203.111' hit the server for '255' times at Sun Jan 01 15:00:00 CET 2017 within a day   | 2018-11-16 01:03:47 |
|         4 | 192.168.38.77   | Ip '192.168.38.77' hit the server for '277' times at Sun Jan 01 15:00:00 CET 2017 within a day     | 2018-11-16 01:03:47 |
|         5 | 192.168.106.134 | Ip '192.168.106.134' hit the server for '232' times at Sun Jan 01 15:00:00 CET 2017 within an hour | 2018-11-16 01:03:51 |
|         6 | 192.168.11.231  | Ip '192.168.11.231' hit the server for '211' times at Sun Jan 01 15:00:00 CET 2017 within an hour  | 2018-11-16 01:03:51 |
+-----------+-----------------+----------------------------------------------------------------------------------------------------+---------------------+
```

# Teachincal Aspects
* __airlift__ - CLI interface
* __RxJava2__ - ReactiveX Java foundation, reactive programming
* __rxjava2-jdbc__ - Connect to a database using RxJava
* __Clean architecture__ - RxJava2 is considered part of the core, so it is not isolated
