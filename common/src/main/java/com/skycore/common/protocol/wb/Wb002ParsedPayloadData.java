package com.skycore.common.protocol.wb;

/**
 * JK-WB-002 发往仿真平台的解析后载荷数据。
 */
public class Wb002ParsedPayloadData {

    private String payloadId;
    private Long parseTime;
    private Integer validFlag;
    private Double magTotal;
    private Double plasmaVel;
    private Double proDens;
    private String transId;
    /** SPO 解帧字段（试点）；键为星上标识如 SCI200001 */
    private java.util.Map<String, Object> spoFields;
    private String frameType;
    private String spoSheet;

    public String getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(String payloadId) {
        this.payloadId = payloadId;
    }

    public Long getParseTime() {
        return parseTime;
    }

    public void setParseTime(Long parseTime) {
        this.parseTime = parseTime;
    }

    public Integer getValidFlag() {
        return validFlag;
    }

    public void setValidFlag(Integer validFlag) {
        this.validFlag = validFlag;
    }

    public Double getMagTotal() {
        return magTotal;
    }

    public void setMagTotal(Double magTotal) {
        this.magTotal = magTotal;
    }

    public Double getPlasmaVel() {
        return plasmaVel;
    }

    public void setPlasmaVel(Double plasmaVel) {
        this.plasmaVel = plasmaVel;
    }

    public Double getProDens() {
        return proDens;
    }

    public void setProDens(Double proDens) {
        this.proDens = proDens;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public java.util.Map<String, Object> getSpoFields() {
        return spoFields;
    }

    public void setSpoFields(java.util.Map<String, Object> spoFields) {
        this.spoFields = spoFields;
    }

    public String getFrameType() {
        return frameType;
    }

    public void setFrameType(String frameType) {
        this.frameType = frameType;
    }

    public String getSpoSheet() {
        return spoSheet;
    }

    public void setSpoSheet(String spoSheet) {
        this.spoSheet = spoSheet;
    }
}
