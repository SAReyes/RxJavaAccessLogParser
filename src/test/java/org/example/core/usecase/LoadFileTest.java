package org.example.core.usecase;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.example.core.port.ReadNginxLog;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class LoadFileTest {

    private LoadFile sut;

    @Mock
    private ReadNginxLog readNginxLog;

    @Mock
    private SaveAccessRecord saveAccessRecord;

    @Before
    public void setUp() {
        sut = new LoadFileImpl(readNginxLog, saveAccessRecord);
    }

    @Test
    public void loads_a_file() {
        var file = "/path/to/file";
        var records = new AccessRecord[] {
                AccessRecord.builder().userAgent("first UA").build(),
                AccessRecord.builder().userAgent("second UA").build()
        };
        var savedRecords = new AccessRecord[] {
                AccessRecord.builder().userAgent("first UA saved").build(),
                AccessRecord.builder().userAgent("second UA saved").build()
        };

        doReturn(Flowable.fromArray(records)).when(readNginxLog).readNginxLog(file);
        doReturn(Single.just(savedRecords[0])).when(saveAccessRecord).saveAccessRecord(records[0]);
        doReturn(Single.just(savedRecords[1])).when(saveAccessRecord).saveAccessRecord(records[1]);

        sut.loadFile(file)
                .test()
                .assertComplete()
                .assertValueCount(2)
                .assertResult(savedRecords);
    }
}
