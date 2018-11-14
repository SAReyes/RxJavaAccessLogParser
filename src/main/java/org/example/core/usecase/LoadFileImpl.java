package org.example.core.usecase;

import io.reactivex.Flowable;
import org.example.core.port.ReadAccessLog;
import org.example.core.port.SaveAccessRecord;

public class LoadFileImpl implements LoadFile {

    private ReadAccessLog readAccessLog;
    private SaveAccessRecord saveAccessRecord;

    public LoadFileImpl(ReadAccessLog readAccessLog, SaveAccessRecord saveAccessRecord) {
        this.readAccessLog = readAccessLog;
        this.saveAccessRecord = saveAccessRecord;
    }

    @Override
    public Flowable<Integer> loadFile(String filename) {
        return readAccessLog.readAccessLog(filename)
                .flatMap(saveAccessRecord::saveAccessRecord);
    }
}
