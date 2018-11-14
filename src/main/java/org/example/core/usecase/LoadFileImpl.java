package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.core.port.ReadNginxLog;
import org.example.core.port.SaveAccessRecord;

public class LoadFileImpl implements LoadFile {

    private ReadNginxLog readNginxLog;
    private SaveAccessRecord saveAccessRecord;

    public LoadFileImpl(ReadNginxLog readNginxLog, SaveAccessRecord saveAccessRecord) {
        this.readNginxLog = readNginxLog;
        this.saveAccessRecord = saveAccessRecord;
    }

    @Override
    public Flowable<Integer> loadFile(String filename) {
        return readNginxLog.readNginxLog(filename)
                .flatMap(saveAccessRecord::saveAccessRecord);
    }
}
