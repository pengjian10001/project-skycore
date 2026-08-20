package com.skycore.common.spo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 按 SPO 字段表从原始帧解出工程量。
 * startByte 为 1-based；多字节按高前低后（大端）。
 */
public class SpoFrameParser {

    public Map<String, Object> parse(byte[] frame, List<SpoFieldDef> fields) {
        if (frame == null || frame.length == 0) {
            throw new IllegalArgumentException("frame is empty");
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (SpoFieldDef field : fields) {
            long raw = extractUnsigned(frame, field.getStartByte(), field.getLengthBits());
            out.put(field.getCode(), decode(field, raw));
        }
        return out;
    }

    public Map<String, Object> parseHex(String rawHex, List<SpoFieldDef> fields) {
        return parse(hexToBytes(rawHex), fields);
    }

    public static byte[] hexToBytes(String rawHex) {
        if (rawHex == null || rawHex.isBlank()) {
            throw new IllegalArgumentException("rawHex is blank");
        }
        String hex = rawHex.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("rawHex length must be even");
        }
        if (!hex.matches("[0-9A-F]+")) {
            throw new IllegalArgumentException("rawHex contains non-hex chars");
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    private static Object decode(SpoFieldDef field, long raw) {
        String formula = field.getFormula() == null ? "" : field.getFormula().toUpperCase(Locale.ROOT);
        if (formula.contains("FHEX") || formula.contains("十六进制")) {
            int nibbles = Math.max(2, (field.getLengthBits() + 3) / 4);
            return String.format(Locale.ROOT, "0x%0" + nibbles + "X", raw);
        }
        if (formula.contains("FUDEC") || formula.contains("十进制")) {
            return raw;
        }
        if (formula.contains("FST") || formula.contains("星时")) {
            return raw;
        }
        if (formula.contains("FGT") || formula.contains("地面时")) {
            return raw;
        }
        return raw;
    }

    /**
     * 从 1-based 字节起始位置提取 lengthBits 位无符号整数（大端）。
     */
    static long extractUnsigned(byte[] frame, int startByte1Based, int lengthBits) {
        if (startByte1Based < 1) {
            throw new IllegalArgumentException("startByte must be >= 1");
        }
        if (lengthBits <= 0 || lengthBits > 64) {
            throw new IllegalArgumentException("lengthBits out of range: " + lengthBits);
        }
        int bitOffset = (startByte1Based - 1) * 8;
        int endBit = bitOffset + lengthBits;
        if (endBit > frame.length * 8) {
            throw new IllegalArgumentException("frame too short for field at byte "
                    + startByte1Based + " lenBits=" + lengthBits + " frameBytes=" + frame.length);
        }
        long value = 0;
        for (int i = 0; i < lengthBits; i++) {
            int absBit = bitOffset + i;
            int byteIndex = absBit / 8;
            int bitInByte = 7 - (absBit % 8);
            int bit = (frame[byteIndex] >> bitInByte) & 0x1;
            value = (value << 1) | bit;
        }
        return value;
    }
}
