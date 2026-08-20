package com.skycore.common.protocol.wb;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * JK-WB-001 载荷单机载荷数据入站。
 */
public class Wb001PayloadDataRequest {

    @NotBlank
    private String payloadId;

    @NotNull
    private Long satTime;

    private Integer frameLen;
    private Long frameCnt;
    private Integer status;
    private Double temp;
    private Double magX;
    private Double magY;
    private Double magZ;
    /** 原始打包报文摘要（hex 或 base64） */
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
}
