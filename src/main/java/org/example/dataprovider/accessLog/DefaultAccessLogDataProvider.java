package org.example.dataprovider.accessLog;

import io.reactivex.Flowable;
import org.example.core.port.ReadAccessLog;
import org.example.dataprovider.csv.ReadFileLines;
import org.example.domain.AccessRecord;
import org.example.dataprovider.csv.ReadCsvLine;

import java.io.InputStream;

public class DefaultAccessLogDataProvider implements ReadAccessLog {

    private ReadCsvLine readCsvLine;
    private ReadFileLines readFileLines;

    public DefaultAccessLogDataProvider(ReadCsvLine readCsvLine, ReadFileLines readFileLines) {
        this.readCsvLine = readCsvLine;
        this.readFileLines = readFileLines;
    }

    @Override
    public Flowable<AccessRecord> readAccessLog(InputStream inputStream) {
        return readFileLines.readFileLines(inputStream)
                .flatMapSingle(it -> readCsvLine.readCsvLine(it));
    }
}
