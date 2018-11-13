package org.example.dataprovider.csv;

import org.example.domain.AccessRecord;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.io.File;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DefaultCsvDataProviderTest {

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private DefaultCsvDataProvider sut;

    @Before
    public void setUp() {
        sut = new DefaultCsvDataProvider(new SimpleDateFormat(DATE_FORMAT));
    }

    @Test
    public void reads_a_csv_line() throws ParseException {
        var csvLine = "2017-01-01 23:59:46.201|192.168.159.230|\"GET / HTTP/1.1\"|200|\"Fancy UA\"";
        var expected = AccessRecord.builder()
                .accessDate(formatDate("2017-01-01 23:59:46.201"))
                .ip("192.168.159.230")
                .request("GET / HTTP/1.1")
                .status(200)
                .userAgent("Fancy UA")
                .build();

        var result = sut.readCsvLine(csvLine);

        StepVerifier
                .create(result)
                .expectNext(expected)
                .verifyComplete();
    }

    @Test
    public void reads_all_lines_from_a_file() throws URISyntaxException {
        var file = new File(this.getClass().getResource("example.log").toURI());

        var result = sut.readFileLines(file);

        StepVerifier.create(result)
                .expectNext("first line")
                .expectNext("second line")
                .verifyComplete();
    }

    private Date formatDate(String date) throws ParseException {
        var formatter = new SimpleDateFormat(DATE_FORMAT);

        return formatter.parse(date);
    }
}
