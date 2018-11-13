package org.example.dataprovider.csv;

import org.apache.commons.io.FileUtils;
import org.example.domain.AccessRecord;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultCsvDataProvider implements ReadCsvLine, ReadFileLines {

    private SimpleDateFormat parser;

    public DefaultCsvDataProvider(SimpleDateFormat dateFormatParser) {
        parser = dateFormatParser;
    }

    @Override
    public Mono<AccessRecord> readCsvLine(String csvLine) {

        return Mono.just(csvLine)
                .map(it -> it.split("\\|"))
                .map(it -> {
                    Date accessDate;
                    try {
                        accessDate = parser.parse(it[0]);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    return AccessRecord.builder()
                            .accessDate(accessDate)
                            .ip(it[1])
                            .request(trimWrappingQuotation(it[2]))
                            .status(Integer.parseInt(it[3]))
                            .userAgent(trimWrappingQuotation(it[4]))
                            .build();
                });
    }

    @Override
    public Flux<String> readFileLines(File file) {
        return Flux.fromStream(() -> {
            try {
                return FileUtils.readLines(file, Charset.defaultCharset()).stream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private String trimWrappingQuotation(String input) {
        return input.replaceAll("^\"", "").replaceAll("\"$", "");
    }
}
