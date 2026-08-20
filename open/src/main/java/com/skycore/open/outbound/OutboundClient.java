package com.skycore.open.outbound;

import com.skycore.common.protocol.wb.Wb002ParsedPayloadData;
import com.skycore.common.protocol.wb.Wb004ForwardCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 出站客户端（第 1 期内存 mock，供单测断言）。
 */
@Component
public class OutboundClient {

    private final CopyOnWriteArrayList<Wb002ParsedPayloadData> toSimPlatform = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<Wb004ForwardCommand> toPayload = new CopyOnWriteArrayList<>();

    public void sendToSimPlatform(Wb002ParsedPayloadData data) {
        toSimPlatform.add(data);
    }

    public void forwardToPayload(Wb004ForwardCommand cmd) {
        toPayload.add(cmd);
    }

    public List<Wb002ParsedPayloadData> snapshotSimPlatform() {
        return new ArrayList<>(toSimPlatform);
    }

    public List<Wb004ForwardCommand> snapshotPayload() {
        return new ArrayList<>(toPayload);
    }

    public void clear() {
        toSimPlatform.clear();
        toPayload.clear();
    }
}
