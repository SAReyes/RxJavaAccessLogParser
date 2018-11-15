package org.example.dataprovider.logger;

import org.junit.Before;
import org.junit.Test;

public class DefaultLoggerDataProviderTest {

    private DefaultLoggerDataProvider sut;

    @Before
    public void setUp() {
        sut = new DefaultLoggerDataProvider();
    }

    @Test
    public void logger_works() {
        sut.processBannedIp("ignored", "real message");
    }
}
