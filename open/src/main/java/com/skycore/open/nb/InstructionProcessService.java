package com.skycore.open.nb;

import com.skycore.common.protocol.nb.Nb001ProcessRequest;
import com.skycore.common.protocol.nb.Nb001ProcessResult;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;

/**
 * JK-NB-001 指令信息处理：载荷指令解析 / 仿真数据打包。
 */
@Service
public class InstructionProcessService {

    public static final int TYPE_PAYLOAD_CMD = 1;
    public static final int TYPE_SIM_DATA = 2;

    public Nb001ProcessResult process(Nb001ProcessRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }
        int type = request.getInstrType();
        if (type != TYPE_PAYLOAD_CMD && type != TYPE_SIM_DATA) {
            throw new IllegalArgumentException("unknown instrType: " + type);
        }

        Nb001ProcessResult result = new Nb001ProcessResult();
        String transId = request.getTransId() == null || request.getTransId().isBlank()
                ? "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12)
                : request.getTransId();
        result.setTransId(transId);
        result.setInstrType(type);
        result.setTimeStamp(System.currentTimeMillis());
        result.setCheckStatus(1);
        result.setProcStage(2);

        if (type == TYPE_PAYLOAD_CMD) {
            String cmd;
            if (request.getRawDigest() != null && request.getRawDigest().startsWith("SPO=")) {
                cmd = request.getRawDigest();
            } else {
                cmd = String.format(Locale.ROOT,
                        "PAYLOAD=%s OP=%s DIGEST=%s",
                        nullToDash(request.getPayloadId()),
                        request.getOpCode() == null ? "-" : request.getOpCode(),
                        nullToDash(request.getRawDigest()));
            }
            result.setReadableCommand(cmd);
            result.setProcStage(3);
        } else {
            Double[] sim = request.getSimData() == null ? new Double[0] : request.getSimData();
            StringBuilder hex = new StringBuilder("AA55");
            hex.append(String.format(Locale.ROOT, "%04X", sim.length));
            for (Double v : sim) {
                int bits = Float.floatToIntBits(v == null ? 0f : v.floatValue());
                hex.append(String.format(Locale.ROOT, "%08X", bits));
            }
            result.setPackedSimHex(hex.toString());
            result.setProcStage(3);
        }
        return result;
    }

    private static String nullToDash(String v) {
        return v == null || v.isBlank() ? "-" : v;
    }
}
