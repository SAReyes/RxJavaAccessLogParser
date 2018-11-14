package org.example.core.usecase;

import org.example.core.port.ReadNginxLog;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;
import reactor.core.publisher.Flux;

import java.io.File;

public class LoadFileImpl implements LoadFile {

    private ReadNginxLog readNginxLog;
    private SaveAccessRecord saveAccessRecord;

    public LoadFileImpl(ReadNginxLog readNginxLog, SaveAccessRecord saveAccessRecord) {
        this.readNginxLog = readNginxLog;
        this.saveAccessRecord = saveAccessRecord;
    }

    @Override
    public Flux<AccessRecord> loadFile(File file) {
        return readNginxLog.readNginxLog(file)
                .flatMap(saveAccessRecord::saveAccessRecord);
    }
}
