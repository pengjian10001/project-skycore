package com.skycore.open.nb;

import com.skycore.common.protocol.nb.Nb003LogRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * JK-NB-003 日志写入（内存实现）。
 */
@Service
public class LogWritePort {

    private final CopyOnWriteArrayList<Nb003LogRecord> logs = new CopyOnWriteArrayList<>();

    public Nb003LogRecord write(int level, String module, String eventType, String description,
                                int resultStatus, String relatedId) {
        Nb003LogRecord record = new Nb003LogRecord();
        record.setLogId("LOG_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        record.setTimeStamp(System.currentTimeMillis());
        record.setLogLevel(level);
        record.setModuleName(module);
        record.setEventType(eventType);
        record.setDescription(description);
        record.setResultStatus(resultStatus);
        record.setRelatedId(relatedId);
        logs.add(record);
        return record;
    }

    public List<Nb003LogRecord> listRecent(int limit) {
        int size = logs.size();
        int from = Math.max(0, size - limit);
        return new ArrayList<>(logs.subList(from, size));
    }

    public int size() {
        return logs.size();
    }

    public void clear() {
        logs.clear();
    }
}
