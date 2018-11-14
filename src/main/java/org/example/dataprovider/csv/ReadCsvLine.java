package org.example.dataprovider.csv;

import io.reactivex.Single;
import org.example.domain.AccessRecord;

public interface ReadCsvLine {

    Single<AccessRecord> readCsvLine(String csvLine);
}
