package com.skycore.common.protocol.rs422;

/**
 * 接口.docx 表 43：MHI RS422 串行指令帧校验。
 * 包头 0xEB 0x90；累加和 = 载荷标识+命令类型+有效数据字节数+有效数据 按字节累加取低 16 位。
 */
public final class Rs422FrameCodec {

    public static final int HEADER_0 = 0xEB;
    public static final int HEADER_1 = 0x90;

    private Rs422FrameCodec() {
    }

    public static void validate(byte[] frame) {
        if (frame == null || frame.length < 7) {
            throw new IllegalArgumentException("RS422 frame too short");
        }
        if ((frame[0] & 0xFF) != HEADER_0 || (frame[1] & 0xFF) != HEADER_1) {
            throw new IllegalArgumentException(String.format(
                    "RS422 header mismatch, expect EB90 got %02X%02X",
                    frame[0] & 0xFF, frame[1] & 0xFF));
        }
        int nMinus1 = frame[4] & 0xFF;
        int n = nMinus1 + 1;
        int expectedLen = 5 + n + 2; // header(2)+id+type+len + N data + checksum(2)
        // bytes: 0,1 header; 2 payloadId; 3 cmdType; 4 N-1; 5..(5+N-1) data; then 2 checksum
        // total = 5 + N + 2
        if (frame.length != expectedLen) {
            throw new IllegalArgumentException("RS422 length mismatch: frame="
                    + frame.length + " expected=" + expectedLen + " N=" + n);
        }
        int sum = 0;
        for (int i = 2; i < 5 + n; i++) {
            sum = (sum + (frame[i] & 0xFF)) & 0xFFFF;
        }
        int checksum = ((frame[frame.length - 2] & 0xFF) << 8) | (frame[frame.length - 1] & 0xFF);
        if (checksum != sum) {
            throw new IllegalArgumentException(String.format(
                    "RS422 checksum mismatch expect=0x%04X actual=0x%04X", sum, checksum));
        }
    }

    /**
     * 组一帧：payloadId, cmdType, dataBytes；自动填 N-1 与累加和。
     */
    public static byte[] build(int payloadId, int cmdType, byte[] data) {
        if (data == null || data.length < 2 || data.length > 218) {
            throw new IllegalArgumentException("RS422 data length must be in [2,218]");
        }
        int n = data.length;
        byte[] frame = new byte[5 + n + 2];
        frame[0] = (byte) HEADER_0;
        frame[1] = (byte) HEADER_1;
        frame[2] = (byte) (payloadId & 0xFF);
        frame[3] = (byte) (cmdType & 0xFF);
        frame[4] = (byte) ((n - 1) & 0xFF);
        System.arraycopy(data, 0, frame, 5, n);
        int sum = 0;
        for (int i = 2; i < 5 + n; i++) {
            sum = (sum + (frame[i] & 0xFF)) & 0xFFFF;
        }
        frame[frame.length - 2] = (byte) ((sum >> 8) & 0xFF);
        frame[frame.length - 1] = (byte) (sum & 0xFF);
        return frame;
    }
}
