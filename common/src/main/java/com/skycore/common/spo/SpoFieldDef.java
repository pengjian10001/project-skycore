package com.skycore.common.spo;

/**
 * SPO 字段定义（对齐 SPO.xlsx：开始位置 / 长度 / 转换公式）。
 * startByte 为 1-based 字节下标；lengthBits 为位长。
 */
public class SpoFieldDef {

    private final String code;
    private final String name;
    private final int startByte;
    private final int lengthBits;
    private final String formula;
    private final String unit;

    public SpoFieldDef(String code, String name, int startByte, int lengthBits, String formula, String unit) {
        this.code = code;
        this.name = name;
        this.startByte = startByte;
        this.lengthBits = lengthBits;
        this.formula = formula;
        this.unit = unit;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getStartByte() {
        return startByte;
    }

    public int getLengthBits() {
        return lengthBits;
    }

    public String getFormula() {
        return formula;
    }

    public String getUnit() {
        return unit;
    }
}
