package com.skycore.open.nb;

import com.skycore.common.protocol.nb.Nb003LogRecord;
import com.skycore.common.protocol.nb.Nb004StoreRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogAndStorePortTest {

    private LogWritePort logWritePort;
    private StoreWritePort storeWritePort;

    @BeforeEach
    void setUp() {
        logWritePort = new LogWritePort();
        storeWritePort = new StoreWritePort();
    }

    @Test
    void logWriteAndReadBack() {
        Nb003LogRecord record = logWritePort.write(1, "TCP", "CONNECT", "ok", 0, "TXN1");
        assertEquals(1, logWritePort.size());
        assertEquals(record.getLogId(), logWritePort.listRecent(1).get(0).getLogId());
    }

    @Test
    void storeWriteAndFind() {
        Nb004StoreRecord record = storeWritePort.store("TASK-1", "PL-1", 1, "content");
        assertTrue(storeWritePort.findById(record.getRecordId()).isPresent());
        assertEquals(1, storeWritePort.size());
    }
}
