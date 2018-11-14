package org.example.core.usecase;

import org.example.core.port.ReadNginxLog;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;
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
    public void loads_a_file() throws IOException {
        var file = File.createTempFile("nginx_log_test", ".log");
        var records = new AccessRecord[] {
                AccessRecord.builder().userAgent("first UA").build(),
                AccessRecord.builder().userAgent("second UA").build()
        };
        var savedRecords = new AccessRecord[] {
                AccessRecord.builder().userAgent("first UA saved").build(),
                AccessRecord.builder().userAgent("second UA saved").build()
        };

        doReturn(Flux.fromArray(records)).when(readNginxLog).readNginxLog(file);
        doReturn(Mono.just(savedRecords[0])).when(saveAccessRecord).saveAccessRecord(records[0]);
        doReturn(Mono.just(savedRecords[1])).when(saveAccessRecord).saveAccessRecord(records[1]);

        var result = sut.loadFile(file);

        StepVerifier.create(result)
                .expectNext(savedRecords[0])
                .expectNext(savedRecords[1])
                .verifyComplete();
    }
}
