package org.example.port.csv;

import org.example.domain.AccessRecord;
import reactor.core.publisher.Mono;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadCsvEntry {

    private SimpleDateFormat parser;

    public ReadCsvEntry(SimpleDateFormat dateFormatParser) {
        parser = dateFormatParser;
    }

    public Mono<AccessRecord> readLine(String csvLine) {

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

    private String trimWrappingQuotation(String input) {
        return input.replaceAll("^\"", "").replaceAll("\"$", "");
    }
}
