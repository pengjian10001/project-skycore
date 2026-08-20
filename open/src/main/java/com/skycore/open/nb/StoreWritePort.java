package com.skycore.open.nb;

import com.skycore.common.protocol.nb.Nb004StoreRecord;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * JK-NB-004 存储写入（内存实现）。
 */
@Service
public class StoreWritePort {

    private final CopyOnWriteArrayList<Nb004StoreRecord> records = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<String, Nb004StoreRecord> byId = new ConcurrentHashMap<>();

    public Nb004StoreRecord store(String taskId, String payloadName, int dataType, String content) {
        Nb004StoreRecord record = new Nb004StoreRecord();
        record.setRecordId("REC_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10));
        record.setTaskId(taskId);
        record.setPayloadName(payloadName);
        record.setDataType(dataType);
        record.setDataContent(content);
        long now = System.currentTimeMillis();
        record.setGenTime(now);
        record.setStoreTime(now);
        records.add(record);
        byId.put(record.getRecordId(), record);
        return record;
    }

    public Optional<Nb004StoreRecord> findById(String recordId) {
        return Optional.ofNullable(byId.get(recordId));
    }

    public List<Nb004StoreRecord> listRecent(int limit) {
        int size = records.size();
        int from = Math.max(0, size - limit);
        return new ArrayList<>(records.subList(from, size));
    }

    public int size() {
        return records.size();
    }

    public void clear() {
        records.clear();
        byId.clear();
    }
}
