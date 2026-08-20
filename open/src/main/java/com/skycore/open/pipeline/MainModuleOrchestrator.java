package com.skycore.open.pipeline;

import com.skycore.common.protocol.nb.Nb001ProcessRequest;
import com.skycore.common.protocol.nb.Nb001ProcessResult;
import com.skycore.common.protocol.nb.Nb004StoreRecord;
import com.skycore.common.protocol.wb.Wb001PayloadDataRequest;
import com.skycore.common.protocol.wb.Wb002ParsedPayloadData;
import com.skycore.common.protocol.wb.Wb003SimCommandRequest;
import com.skycore.common.protocol.wb.Wb004ForwardCommand;
import com.skycore.open.nb.InstructionProcessService;
import com.skycore.open.nb.LogWritePort;
import com.skycore.open.nb.PayloadFrameDecodeService;
import com.skycore.open.nb.StoreWritePort;
import com.skycore.open.nb.TcpCommunicationPort;
import com.skycore.open.outbound.OutboundClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 主模块编排：WB 入站 → NB-001 → 日志/存储 → 出站 / TCP。
 */
@Service
public class MainModuleOrchestrator {

    private final InstructionProcessService instructionProcessService;
    private final PayloadFrameDecodeService payloadFrameDecodeService;
    private final TcpCommunicationPort tcpCommunicationPort;
    private final LogWritePort logWritePort;
    private final StoreWritePort storeWritePort;
    private final OutboundClient outboundClient;

    public MainModuleOrchestrator(InstructionProcessService instructionProcessService,
                                  PayloadFrameDecodeService payloadFrameDecodeService,
                                  TcpCommunicationPort tcpCommunicationPort,
                                  LogWritePort logWritePort,
                                  StoreWritePort storeWritePort,
                                  OutboundClient outboundClient) {
        this.instructionProcessService = instructionProcessService;
        this.payloadFrameDecodeService = payloadFrameDecodeService;
        this.tcpCommunicationPort = tcpCommunicationPort;
        this.logWritePort = logWritePort;
        this.storeWritePort = storeWritePort;
        this.outboundClient = outboundClient;
    }

    public Map<String, Object> ingestPayloadData(Wb001PayloadDataRequest request) {
        long start = System.currentTimeMillis();
        PayloadFrameDecodeService.DecodeResult decoded = payloadFrameDecodeService.decode(request);

        Nb001ProcessRequest nbReq = new Nb001ProcessRequest();
        nbReq.setInstrType(InstructionProcessService.TYPE_PAYLOAD_CMD);
        nbReq.setPayloadId(request.getPayloadId());
        nbReq.setOpCode(request.getStatus());
        if (decoded.isSpoDecoded()) {
            nbReq.setRawDigest(decoded.getSummary());
        } else {
            nbReq.setRawDigest(request.resolveRawHex());
        }

        Nb001ProcessResult processed = instructionProcessService.process(nbReq);

        Object storeBody = decoded.isSpoDecoded() ? decoded.getFields() : processed.getReadableCommand();
        Nb004StoreRecord stored = storeWritePort.store(
                "TASK-DEFAULT",
                request.getPayloadId(),
                1,
                String.valueOf(storeBody));

        Wb002ParsedPayloadData outbound = new Wb002ParsedPayloadData();
        outbound.setTransId(processed.getTransId());
        outbound.setPayloadId(request.getPayloadId());
        outbound.setParseTime(System.currentTimeMillis());
        outbound.setValidFlag(1);
        if (decoded.isSpoDecoded()) {
            outbound.setSpoFields(decoded.getFields());
            outbound.setFrameType(decoded.getFrameType());
            outbound.setSpoSheet(decoded.getSpoSheet());
        } else {
            Double magTotal = null;
            if (request.getMagX() != null && request.getMagY() != null && request.getMagZ() != null) {
                magTotal = Math.sqrt(
                        request.getMagX() * request.getMagX()
                                + request.getMagY() * request.getMagY()
                                + request.getMagZ() * request.getMagZ());
            }
            outbound.setMagTotal(magTotal);
        }
        outboundClient.sendToSimPlatform(outbound);

        try {
            tcpCommunicationPort.send("sim-platform", processed.getReadableCommand());
        } catch (IllegalStateException ex) {
            logWritePort.write(3, "TCP", "SEND_FAIL", ex.getMessage(), 1, processed.getTransId());
            throw ex;
        }

        logWritePort.write(1, "Orchestrator", "WB001",
                "ingest payload " + request.getPayloadId()
                        + (decoded.isSpoDecoded() ? " spo=" + decoded.getFrameType() : ""),
                0, processed.getTransId());

        Map<String, Object> result = new HashMap<>();
        result.put("transId", processed.getTransId());
        result.put("recordId", stored.getRecordId());
        result.put("forwarded", outbound);
        result.put("durationMs", System.currentTimeMillis() - start);
        if (decoded.isSpoDecoded()) {
            result.put("frameType", decoded.getFrameType());
            result.put("spoSheet", decoded.getSpoSheet());
            result.put("spoFields", decoded.getFields());
            result.put("frameBytes", decoded.getFrameBytes());
            result.put("satTime", decoded.getSatTime() != null ? decoded.getSatTime() : request.getSatTime());
        }
        return result;
    }

    public Map<String, Object> ingestSimCommand(Wb003SimCommandRequest request) {
        long start = System.currentTimeMillis();
        Nb001ProcessRequest nbReq = new Nb001ProcessRequest();
        nbReq.setInstrType(InstructionProcessService.TYPE_SIM_DATA);
        nbReq.setPayloadId("PL-" + request.getTargetId());
        nbReq.setOpCode(request.getCmdCode());
        nbReq.setSimData(new Double[]{
                request.getSampleFreq() == null ? 0d : request.getSampleFreq(),
                request.getExposure() == null ? 0d : request.getExposure(),
                request.getParaA() == null ? 0d : request.getParaA(),
                request.getParaB() == null ? 0d : request.getParaB()
        });

        Nb001ProcessResult processed = instructionProcessService.process(nbReq);

        Wb004ForwardCommand forward = new Wb004ForwardCommand();
        forward.setTransId(processed.getTransId());
        forward.setPktTime(request.getCmdTime());
        forward.setTargetId(request.getTargetId());
        forward.setOpCode(request.getCmdCode());
        forward.setWorkMode(request.getWorkMode());
        forward.setChannel(0);
        forward.setPktLen(processed.getPackedSimHex() == null ? 0 : processed.getPackedSimHex().length() / 2);
        forward.setIntTime(request.getExposure());
        outboundClient.forwardToPayload(forward);

        storeWritePort.store("TASK-DEFAULT", "PL-" + request.getTargetId(), 2, processed.getPackedSimHex());

        try {
            tcpCommunicationPort.send("payload-" + request.getTargetId(), processed.getPackedSimHex());
        } catch (IllegalStateException ex) {
            logWritePort.write(3, "TCP", "SEND_FAIL", ex.getMessage(), 1, processed.getTransId());
            throw ex;
        }

        logWritePort.write(1, "Orchestrator", "WB003",
                "forward cmd seq=" + request.getCmdSeq(),
                0, processed.getTransId());

        Map<String, Object> result = new HashMap<>();
        result.put("transId", processed.getTransId());
        result.put("forwarded", forward);
        result.put("packedSimHex", processed.getPackedSimHex());
        result.put("durationMs", System.currentTimeMillis() - start);
        return result;
    }

    public Map<String, Object> dashboardSnapshot() {
        Map<String, Object> data = new HashMap<>();
        data.put("logCount", logWritePort.size());
        data.put("storeCount", storeWritePort.size());
        data.put("tcpConnected", tcpCommunicationPort.isConnected());
        data.put("tcpOutbox", tcpCommunicationPort.snapshotOutbox().size());
        data.put("recentLogs", logWritePort.listRecent(10));
        data.put("recentStores", storeWritePort.listRecent(10));
        data.put("simPlatformOut", outboundClient.snapshotSimPlatform().size());
        data.put("payloadOut", outboundClient.snapshotPayload().size());
        return data;
    }
}
