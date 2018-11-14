package org.example.dataprovider.nginx;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.example.dataprovider.csv.ReadFileLines;
import org.example.domain.AccessRecord;
import org.example.dataprovider.csv.ReadCsvLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    public void reads_a_log_properly() {
        var file = "/path/to/file";
        var stringEntries = new String[]{
                "First access",
                "Second access"
        };
        var accessRecords = new AccessRecord[]{
                AccessRecord.builder().userAgent("First UA").build(),
                AccessRecord.builder().userAgent("Second UA").build()
        };

        doReturn(Flowable.fromArray(stringEntries)).when(readFileLines).readFileLines(file);
        doReturn(Single.just(accessRecords[0])).when(readCsvLine).readCsvLine(stringEntries[0]);
        doReturn(Single.just(accessRecords[1])).when(readCsvLine).readCsvLine(stringEntries[1]);

        sut.readNginxLog(file)
                .test()
                .assertComplete()
                .assertValueCount(2)
                .assertResult(accessRecords);
    }
}
