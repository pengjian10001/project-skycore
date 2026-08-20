package com.skycore.common.spo;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SpoFrameParserTest {

    private final SpoFrameParser parser = new SpoFrameParser();

    @Test
    void parseMhiSci20000Golden() {
        // SCI200001..005: sync=EB90 id=2000 seq=0001 len=10 time=100000
        String rawHex = "EB90 2000 0001 000A 0000000186A0";
        Map<String, Object> fields = parser.parseHex(rawHex, MhiPilotDictionary.fieldsOf(MhiPilotDictionary.FRAME_MHI_SCI_20000));
        assertEquals("0xEB90", fields.get("SCI200001"));
        assertEquals("0x2000", fields.get("SCI200002"));
        assertEquals("0x0001", fields.get("SCI200003"));
        assertEquals(10L, fields.get("SCI200004"));
        assertEquals(100000L, fields.get("SCI200005"));
    }

    @Test
    void parseMhiEng13001PilotFields() {
        String rawHex = "000000000000000000000000EB901301000200080000000000000507";
        Map<String, Object> fields = parser.parseHex(rawHex, MhiPilotDictionary.fieldsOf(MhiPilotDictionary.FRAME_MHI_ENG_13001));
        assertEquals("0xEB90", fields.get("MHIENG003"));
        assertEquals("0x1301", fields.get("MHIENG004"));
        assertEquals("0x0002", fields.get("MHIENG005"));
        assertEquals(8L, fields.get("MHIENG006"));
        assertEquals(5L, fields.get("MHIENG008"));
        assertEquals(7L, fields.get("MHIENG009"));
    }

    @Test
    void frameTooShort() {
        assertThrows(IllegalArgumentException.class,
                () -> parser.parseHex("EB90", MhiPilotDictionary.fieldsOf(MhiPilotDictionary.FRAME_MHI_SCI_20000)));
    }
}
