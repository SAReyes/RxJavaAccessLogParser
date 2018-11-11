package org.example.port.csv;

import org.example.domain.AccessRecord;
import org.junit.Before;
import org.junit.Test;
import reactor.test.StepVerifier;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReadCsvEntryTest {

    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    private ReadCsvEntry sut;

    @Before
    public void setUp() {
        sut = new ReadCsvEntry(new SimpleDateFormat(DATE_FORMAT));
    }

    @Test
    public void parses_the_line_properly() throws ParseException {
        var csvLine = "2017-01-01 23:59:46.201|192.168.159.230|\"GET / HTTP/1.1\"|200|\"Fancy UA\"" +
                "(KHTML, like Gecko) Mobile/14G60\"";
        var expected = AccessRecord.builder()
                .accessDate(formatDate("2017-01-01 23:59:46.201"))
                .ip("192.168.159.230")
                .request("GET / HTTP/1.1")
                .status(200)
                .userAgent("Fancy UA")
                .build();

        var response = sut.readLine(csvLine);

        StepVerifier
                .create(response)
                .expectNext(expected)
                .verifyComplete();
    }

    private Date formatDate(String date) throws ParseException {
        var formatter = new SimpleDateFormat(DATE_FORMAT);

        return formatter.parse(date);
    }
}
