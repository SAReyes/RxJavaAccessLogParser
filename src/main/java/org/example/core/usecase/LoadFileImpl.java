package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.core.port.ReadNginxLog;
import org.example.core.port.SaveAccessRecord;
import org.example.domain.AccessRecord;

public class LoadFileImpl implements LoadFile {

    private ReadNginxLog readNginxLog;
    private SaveAccessRecord saveAccessRecord;

    public LoadFileImpl(ReadNginxLog readNginxLog, SaveAccessRecord saveAccessRecord) {
        this.readNginxLog = readNginxLog;
        this.saveAccessRecord = saveAccessRecord;
    }

    @Override
    public Flowable<AccessRecord> loadFile(String filename) {
        return readNginxLog.readNginxLog(filename)
                .flatMapSingle(saveAccessRecord::saveAccessRecord);
    }
}
