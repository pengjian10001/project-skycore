package com.skycore.open.nb;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * JK-NB-002 TCP 通信端口（第 1 期：内存记录 + 可切换骨架，非真实 socket）。
 */
@Service
public class TcpCommunicationPort {

    public record TcpSendRecord(String target, String payload, long sentAt) {
    }

    private final CopyOnWriteArrayList<TcpSendRecord> outbox = new CopyOnWriteArrayList<>();
    private volatile boolean connected = true;

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public void send(String target, String payload) {
        if (!connected) {
            throw new IllegalStateException("TCP disconnected, target=" + target);
        }
        outbox.add(new TcpSendRecord(target, payload, System.currentTimeMillis()));
    }

    public List<TcpSendRecord> snapshotOutbox() {
        return new ArrayList<>(outbox);
    }

    public void clear() {
        outbox.clear();
    }
}
