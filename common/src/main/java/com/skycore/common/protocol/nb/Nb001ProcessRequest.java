package com.skycore.common.protocol.nb;

/**
 * JK-NB-001 指令信息处理入站消息。
 * instrType: 1=载荷指令包, 2=仿真数据
 */
public class Nb001ProcessRequest {

    private String transId;
    private int instrType;
    private String payloadId;
    private String targetName;
    private Integer opCode;
    private String rawDigest;
    private String protoVer;
    private Double[] paramList;
    private Double[] simData;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public int getInstrType() {
        return instrType;
    }

    public void setInstrType(int instrType) {
        this.instrType = instrType;
    }

    public String getPayloadId() {
        return payloadId;
    }

    public void setPayloadId(String payloadId) {
        this.payloadId = payloadId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public Integer getOpCode() {
        return opCode;
    }

    public void setOpCode(Integer opCode) {
        this.opCode = opCode;
    }

    public String getRawDigest() {
        return rawDigest;
    }

    public void setRawDigest(String rawDigest) {
        this.rawDigest = rawDigest;
    }

    public String getProtoVer() {
        return protoVer;
    }

    public void setProtoVer(String protoVer) {
        this.protoVer = protoVer;
    }

    public Double[] getParamList() {
        return paramList;
    }

    public void setParamList(Double[] paramList) {
        this.paramList = paramList;
    }

    public Double[] getSimData() {
        return simData;
    }

    public void setSimData(Double[] simData) {
        this.simData = simData;
    }
}
