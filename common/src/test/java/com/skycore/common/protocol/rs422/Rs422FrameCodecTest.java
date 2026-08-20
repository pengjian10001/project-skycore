package com.skycore.common.protocol.rs422;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Rs422FrameCodecTest {

    @Test
    void buildAndValidate() {
        byte[] frame = Rs422FrameCodec.build(0x01, 0x25, new byte[]{0x00, 0x01});
        // EB 90 | 01 | 25 | 01(N-1) | 00 01 | checksum
        assertArrayEquals(new byte[]{
                (byte) 0xEB, (byte) 0x90, 0x01, 0x25, 0x01, 0x00, 0x01, 0x00, 0x28
        }, frame);
        assertDoesNotThrow(() -> Rs422FrameCodec.validate(frame));
    }

    @Test
    void rejectBadChecksum() {
        byte[] frame = Rs422FrameCodec.build(0x01, 0x25, new byte[]{0x00, 0x01});
        frame[frame.length - 1] ^= 0x01;
        assertThrows(IllegalArgumentException.class, () -> Rs422FrameCodec.validate(frame));
    }

    @Test
    void rejectBadHeader() {
        byte[] frame = Rs422FrameCodec.build(0x01, 0x25, new byte[]{0x00, 0x01});
        frame[0] = 0x00;
        assertThrows(IllegalArgumentException.class, () -> Rs422FrameCodec.validate(frame));
    }
}
