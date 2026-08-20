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
}
