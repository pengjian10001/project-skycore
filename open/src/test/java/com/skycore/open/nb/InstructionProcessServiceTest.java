package com.skycore.open.nb;

import com.skycore.common.protocol.nb.Nb001ProcessRequest;
import com.skycore.common.protocol.nb.Nb001ProcessResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstructionProcessServiceTest {

    private final InstructionProcessService service = new InstructionProcessService();

    @Test
    void parsePayloadCommand() {
        Nb001ProcessRequest req = new Nb001ProcessRequest();
        req.setInstrType(InstructionProcessService.TYPE_PAYLOAD_CMD);
        req.setPayloadId("PL-001");
        req.setOpCode(1);
        req.setRawDigest("AABB");

        Nb001ProcessResult result = service.process(req);
        assertEquals(1, result.getCheckStatus());
        assertEquals(3, result.getProcStage());
        assertTrue(result.getReadableCommand().contains("PL-001"));
        assertNotNull(result.getTransId());
    }

    @Test
    void packSimData() {
        Nb001ProcessRequest req = new Nb001ProcessRequest();
        req.setInstrType(InstructionProcessService.TYPE_SIM_DATA);
        req.setSimData(new Double[]{1.0, 2.0});

        Nb001ProcessResult result = service.process(req);
        assertNotNull(result.getPackedSimHex());
        assertTrue(result.getPackedSimHex().startsWith("AA55"));
    }

    @Test
    void rejectUnknownType() {
        Nb001ProcessRequest req = new Nb001ProcessRequest();
        req.setInstrType(99);
        assertThrows(IllegalArgumentException.class, () -> service.process(req));
    }
}
