package com.skycore.common.spo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MHI 试点字典：摘自 SPO.xlsx
 * - 41.科学数据帧头_20000_磁场与速度场成像仪科学数据1
 * - 32.工程遥测参数_13001_磁场与速度场成像仪工程参数（前若干字节对齐字段）
 */
public final class MhiPilotDictionary {

    public static final String FRAME_MHI_SCI_20000 = "MHI_SCI_20000";
    public static final String FRAME_MHI_ENG_13001 = "MHI_ENG_13001";
    public static final String SPO_SHEET_SCI_20000 = "41.科学数据帧头_20000_磁场与速度场成像仪科学数据1";
    public static final String SPO_SHEET_ENG_13001 = "32.工程遥测参数_13001_磁场与速度场成像仪工程参数";

    private static final Map<String, List<SpoFieldDef>> FRAMES = new LinkedHashMap<>();

    static {
        FRAMES.put(FRAME_MHI_SCI_20000, List.of(
                new SpoFieldDef("SCI200001", "包同步码", 1, 16, "FHEX", null),
                new SpoFieldDef("SCI200002", "包标识", 3, 16, "FHEX", null),
                new SpoFieldDef("SCI200003", "包序", 5, 16, "FHEX", null),
                new SpoFieldDef("SCI200004", "包长", 7, 16, "FUDEC1", null),
                new SpoFieldDef("SCI200005", "系统时间码", 9, 48, "FST", null)
        ));
        // 工程参数试点：仅取字节对齐、便于演示的字段（SPO 开始位置）
        FRAMES.put(FRAME_MHI_ENG_13001, List.of(
                new SpoFieldDef("MHIENG003", "包同步码", 13, 16, "FHEX", null),
                new SpoFieldDef("MHIENG004", "包标识", 15, 16, "FHEX", null),
                new SpoFieldDef("MHIENG005", "包序", 17, 16, "FHEX", null),
                new SpoFieldDef("MHIENG006", "包长", 19, 16, "FUDEC1", null),
                new SpoFieldDef("MHIENG008", "短指令计数", 27, 8, "FUDEC1", null),
                new SpoFieldDef("MHIENG009", "注入数据计数", 28, 8, "FUDEC1", null)
        ));
    }

    private MhiPilotDictionary() {
    }

    public static List<SpoFieldDef> fieldsOf(String frameType) {
        List<SpoFieldDef> fields = FRAMES.get(frameType);
        if (fields == null) {
            throw new IllegalArgumentException("unsupported frameType: " + frameType
                    + ", supported=" + FRAMES.keySet());
        }
        return fields;
    }

    public static String spoSheetOf(String frameType) {
        return switch (frameType) {
            case FRAME_MHI_SCI_20000 -> SPO_SHEET_SCI_20000;
            case FRAME_MHI_ENG_13001 -> SPO_SHEET_ENG_13001;
            default -> throw new IllegalArgumentException("unsupported frameType: " + frameType);
        };
    }

    public static Map<String, List<SpoFieldDef>> allFrames() {
        return Collections.unmodifiableMap(FRAMES);
    }
}
