package com.skycore.common.protocol.wb;

/**
 * JK-WB-004 转发至载荷单机的仿真指令包。
 */
public class Wb004ForwardCommand {

    private Long pktTime;
    private Integer pktLen;
    private Integer targetId;
    private Integer opCode;
    private Integer channel;
    private Integer workMode;
    private Double gain;
    private Double intTime;
    private String transId;

    public Long getPktTime() {
        return pktTime;
    }

    public void setPktTime(Long pktTime) {
        this.pktTime = pktTime;
    }

    public Integer getPktLen() {
        return pktLen;
    }

    public void setPktLen(Integer pktLen) {
        this.pktLen = pktLen;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getOpCode() {
        return opCode;
    }

    public void setOpCode(Integer opCode) {
        this.opCode = opCode;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getWorkMode() {
        return workMode;
    }

    public void setWorkMode(Integer workMode) {
        this.workMode = workMode;
    }

    public Double getGain() {
        return gain;
    }

    public void setGain(Double gain) {
        this.gain = gain;
    }

    public Double getIntTime() {
        return intTime;
    }

    public void setIntTime(Double intTime) {
        this.intTime = intTime;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }
}
