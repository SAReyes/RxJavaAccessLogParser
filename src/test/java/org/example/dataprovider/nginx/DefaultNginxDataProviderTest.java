package org.example.dataprovider.nginx;

import org.example.dataprovider.csv.ReadFileLines;
import org.example.domain.AccessRecord;
import org.example.dataprovider.csv.ReadCsvLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DefaultNginxDataProviderTest {

    private DefaultNginxDataProvider sut;

    @Mock
    private ReadCsvLine readCsvLine;

    @Mock
    private ReadFileLines readFileLines;

    @Before
    public void setUp() {
        sut = new DefaultNginxDataProvider(readCsvLine, readFileLines);
    }

    @Test
    public void reads_a_log_properly() throws IOException {
        var aFile = File.createTempFile("nginx_log_test", ".log");
        var stringEntries = new String[]{
                "First access",
                "Second access"
        };
        var accessRecords = new AccessRecord[]{
                AccessRecord.builder().userAgent("First UA").build(),
                AccessRecord.builder().userAgent("Second UA").build()
        };

        doReturn(Flux.fromArray(stringEntries)).when(readFileLines).readFileLines(aFile);
        doReturn(Mono.just(accessRecords[0])).when(readCsvLine).readCsvLine(stringEntries[0]);
        doReturn(Mono.just(accessRecords[1])).when(readCsvLine).readCsvLine(stringEntries[1]);

        var result = sut.readNginxLog(aFile);

        StepVerifier.create(result)
                .expectNext(accessRecords[0])
                .expectNext(accessRecords[1])
                .verifyComplete();
    }
}
