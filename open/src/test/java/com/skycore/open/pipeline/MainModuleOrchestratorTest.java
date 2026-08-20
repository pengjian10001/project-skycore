package com.skycore.open.pipeline;

import com.skycore.common.protocol.wb.Wb001PayloadDataRequest;
import com.skycore.common.protocol.wb.Wb003SimCommandRequest;
import com.skycore.open.nb.InstructionProcessService;
import com.skycore.open.nb.LogWritePort;
import com.skycore.open.nb.StoreWritePort;
import com.skycore.open.nb.TcpCommunicationPort;
import com.skycore.open.outbound.OutboundClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainModuleOrchestratorTest {

    private OutboundClient outboundClient;
    private LogWritePort logWritePort;
    private StoreWritePort storeWritePort;
    private MainModuleOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        outboundClient = new OutboundClient();
        logWritePort = new LogWritePort();
        storeWritePort = new StoreWritePort();
        TcpCommunicationPort tcp = new TcpCommunicationPort();
        orchestrator = new MainModuleOrchestrator(
                new InstructionProcessService(),
                tcp,
                logWritePort,
                storeWritePort,
                outboundClient);
    }

    @Test
    void wb001Pipeline() {
        Wb001PayloadDataRequest req = new Wb001PayloadDataRequest();
        req.setPayloadId("PL-MHI");
        req.setSatTime(System.currentTimeMillis());
        req.setMagX(1.0);
        req.setMagY(2.0);
        req.setMagZ(2.0);
        req.setRawDigest("DEADBEEF");

        Map<String, Object> result = orchestrator.ingestPayloadData(req);
        assertNotNull(result.get("transId"));
        assertEquals(1, outboundClient.snapshotSimPlatform().size());
        assertEquals(1, storeWritePort.size());
        assertEquals(1, logWritePort.size());
        assertTrue(outboundClient.snapshotSimPlatform().get(0).getMagTotal() > 0);
    }

    @Test
    void wb003Pipeline() {
        Wb003SimCommandRequest req = new Wb003SimCommandRequest();
        req.setCmdTime(System.currentTimeMillis());
        req.setCmdSeq(1L);
        req.setTargetId(3);
        req.setCmdCode(1);
        req.setSampleFreq(10.0);
        req.setExposure(1.5);

        Map<String, Object> result = orchestrator.ingestSimCommand(req);
        assertNotNull(result.get("packedSimHex"));
        assertEquals(1, outboundClient.snapshotPayload().size());
        assertEquals(3, outboundClient.snapshotPayload().get(0).getTargetId());
    }
}
