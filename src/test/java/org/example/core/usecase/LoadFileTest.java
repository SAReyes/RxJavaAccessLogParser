package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.core.port.ReadAccessLog;
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
    private ReadAccessLog readAccessLog;

    @Mock
    private SaveAccessRecord saveAccessRecord;

    @Before
    public void setUp() {
        sut = new LoadFileImpl(readAccessLog, saveAccessRecord);
    }

    @Test
    public void loads_a_file() {
        var file = "/path/to/file";
        var records = new AccessRecord[]{
                AccessRecord.builder().userAgent("first UA").build(),
                AccessRecord.builder().userAgent("second UA").build()
        };
        var savedRecords = new Integer[]{1, 2};

        doReturn(Flowable.fromArray(records)).when(readAccessLog).readAccessLog(file);
        doReturn(Flowable.just(savedRecords[0])).when(saveAccessRecord).saveAccessRecord(records[0]);
        doReturn(Flowable.just(savedRecords[1])).when(saveAccessRecord).saveAccessRecord(records[1]);

        sut.loadFile(file)
                .test()
                .assertComplete()
                .assertValueCount(2)
                .assertResult(savedRecords);
    }
}
