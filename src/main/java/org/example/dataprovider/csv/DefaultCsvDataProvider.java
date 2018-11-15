package org.example.dataprovider.csv;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.example.domain.AccessRecord;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultCsvDataProvider implements ReadCsvLine, ReadFileLines {

    private SimpleDateFormat parser;

    public DefaultCsvDataProvider(SimpleDateFormat dateFormatParser) {
        parser = dateFormatParser;
    }

    @Override
    public Single<AccessRecord> readCsvLine(String csvLine) {
        return Single.just(csvLine)
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
    public Flowable<String> readFileLines(InputStream inputStream) {
        return Flowable.using(
                () -> new BufferedReader(new InputStreamReader(inputStream)),
                reader -> Flowable.fromIterable(() -> reader.lines().iterator()),
                BufferedReader::close
        );
    }

    private String trimWrappingQuotation(String input) {
        return input.replaceAll("^\"", "").replaceAll("\"$", "");
    }
}
