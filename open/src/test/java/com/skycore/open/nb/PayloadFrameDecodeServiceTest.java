package com.skycore.open.nb;

import com.skycore.common.protocol.wb.Wb001PayloadDataRequest;
import com.skycore.common.spo.MhiPilotDictionary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayloadFrameDecodeServiceTest {

    private final PayloadFrameDecodeService service = new PayloadFrameDecodeService();

    @Test
    void decodeSciPilot() {
        Wb001PayloadDataRequest req = new Wb001PayloadDataRequest();
        req.setPayloadId("PL-MHI");
        req.setFrameType(MhiPilotDictionary.FRAME_MHI_SCI_20000);
        req.setRawHex("EB9020000001000A0000000186A0");

        PayloadFrameDecodeService.DecodeResult result = service.decode(req);
        assertTrue(result.isSpoDecoded());
        assertEquals("0xEB90", result.getFields().get("SCI200001"));
        assertEquals(10L, result.getFields().get("SCI200004"));
        assertEquals(100000L, result.getSatTime());
        assertTrue(result.getSummary().contains("SCI200001"));
    }

    @Test
    void legacyWhenNoFrameType() {
        Wb001PayloadDataRequest req = new Wb001PayloadDataRequest();
        req.setPayloadId("PL-MHI");
        req.setRawDigest("AABB");
        assertFalse(service.decode(req).isSpoDecoded());
    }
}
