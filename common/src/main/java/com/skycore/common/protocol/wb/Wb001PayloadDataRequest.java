package com.skycore.common.protocol.wb;

import jakarta.validation.constraints.NotBlank;

/**
 * JK-WB-001 载荷单机载荷数据入站。
 * <p>
 * 试点路径：提供 {@code frameType} + {@code rawHex}，按 SPO 字段表解帧；
 * 兼容路径：仍可传 SRS 示例字段（magX/Y/Z 等）。
 */
public class Wb001PayloadDataRequest {

    @NotBlank
    private String payloadId;

    /** 星上时标；SPO 解帧时可由 SCI200005 等字段回填，可空 */
    private Long satTime;

    /**
     * 试点帧类型，例如 {@code MHI_SCI_20000}、{@code MHI_ENG_13001}。
     * 与 {@link #rawHex} 同时提供时走 SPO 解帧。
     */
    private String frameType;

    /** 原始帧十六进制（可含空格）；优先于 rawDigest */
    private String rawHex;

    /** 为 true 时先按 接口.docx 表 43 校验 RS422 包头与累加和 */
    private Boolean validateRs422;

    private Integer frameLen;
    private Long frameCnt;
    private Integer status;
    private Double temp;
    private Double magX;
    private Double magY;
    private Double magZ;
    /** 原始打包报文摘要（hex）；兼容旧联调字段 */
    private String rawDigest;

    public String getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(String payloadId) {
        this.payloadId = payloadId;
    }

    public Long getSatTime() {
        return satTime;
    }

    public void setSatTime(Long satTime) {
        this.satTime = satTime;
    }

    public String getFrameType() {
        return frameType;
    }

    public void setFrameType(String frameType) {
        this.frameType = frameType;
    }

    public String getRawHex() {
        return rawHex;
    }

    public void setRawHex(String rawHex) {
        this.rawHex = rawHex;
    }

    public Boolean getValidateRs422() {
        return validateRs422;
    }

    public void setValidateRs422(Boolean validateRs422) {
        this.validateRs422 = validateRs422;
    }

    public Integer getFrameLen() {
        return frameLen;
    }

    public void setFrameLen(Integer frameLen) {
        this.frameLen = frameLen;
    }

    public Long getFrameCnt() {
        return frameCnt;
    }

    public void setFrameCnt(Long frameCnt) {
        this.frameCnt = frameCnt;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getTemp() {
        return temp;
    }

    public void setTemp(Double temp) {
        this.temp = temp;
    }

    public Double getMagX() {
        return magX;
    }

    public void setMagX(Double magX) {
        this.magX = magX;
    }

    public Double getMagY() {
        return magY;
    }

    public void setMagY(Double magY) {
        this.magY = magY;
    }

    public Double getMagZ() {
        return magZ;
    }

    public void setMagZ(Double magZ) {
        this.magZ = magZ;
    }

    public String getRawDigest() {
        return rawDigest;
    }

    public void setRawDigest(String rawDigest) {
        this.rawDigest = rawDigest;
    }

    public boolean spoDecodeRequested() {
        return frameType != null && !frameType.isBlank()
                && resolveRawHex() != null;
    }

    public String resolveRawHex() {
        if (rawHex != null && !rawHex.isBlank()) {
            return rawHex;
        }
        if (rawDigest != null && !rawDigest.isBlank()) {
            return rawDigest;
        }
        return null;
    }
}
