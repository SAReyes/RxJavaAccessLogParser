package org.example.dataprovider.nginx;

import org.example.core.port.ReadNginxLog;
import org.example.dataprovider.csv.ReadFileLines;
import org.example.domain.AccessRecord;
import org.example.dataprovider.csv.ReadCsvLine;
import reactor.core.publisher.Flux;

import java.io.File;

public class DefaultNginxDataProvider implements ReadNginxLog {

    private ReadCsvLine readCsvLine;
    private ReadFileLines readFileLines;

    public DefaultNginxDataProvider(ReadCsvLine readCsvLine, ReadFileLines readFileLines) {
        this.readCsvLine = readCsvLine;
        this.readFileLines = readFileLines;
    }

    @Override
    public Flux<AccessRecord> readNginxLog(File file) {
        return readFileLines.readFileLines(file)
                .flatMap(it -> readCsvLine.readCsvLine(it));
    }
}
