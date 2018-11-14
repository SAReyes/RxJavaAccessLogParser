package org.example.dataprovider.nginx;

import io.reactivex.Flowable;
import org.example.core.port.ReadNginxLog;
import org.example.dataprovider.csv.ReadFileLines;
import org.example.domain.AccessRecord;
import org.example.dataprovider.csv.ReadCsvLine;

public class DefaultNginxDataProvider implements ReadNginxLog {

    private ReadCsvLine readCsvLine;
    private ReadFileLines readFileLines;

    public DefaultNginxDataProvider(ReadCsvLine readCsvLine, ReadFileLines readFileLines) {
        this.readCsvLine = readCsvLine;
        this.readFileLines = readFileLines;
    }

    @Override
    public Flowable<AccessRecord> readNginxLog(String filename) {
        return readFileLines.readFileLines(filename)
                .flatMapSingle(it -> readCsvLine.readCsvLine(it));
    }
}
