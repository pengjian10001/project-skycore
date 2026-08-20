package com.skycore.common.protocol.wb;

import jakarta.validation.constraints.NotNull;

/**
 * JK-WB-003 仿真平台仿真指令入站。
 */
public class Wb003SimCommandRequest {

    @NotNull
    private Long cmdTime;

    @NotNull
    private Long cmdSeq;

    @NotNull
    private Integer targetId;

    @NotNull
    private Integer cmdCode;

    private Integer workMode;
    private Double sampleFreq;
    private Double exposure;
    private Integer faultCode;
    private Double paraA;
    private Double paraB;

    public Long getCmdTime() {
        return cmdTime;
    }

    public void setCmdTime(Long cmdTime) {
        this.cmdTime = cmdTime;
    }

    public Long getCmdSeq() {
        return cmdSeq;
    }

    public void setCmdSeq(Long cmdSeq) {
        this.cmdSeq = cmdSeq;
    }

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public Integer getCmdCode() {
        return cmdCode;
    }

    public void setCmdCode(Integer cmdCode) {
        this.cmdCode = cmdCode;
    }

    public Integer getWorkMode() {
        return workMode;
    }

    public void setWorkMode(Integer workMode) {
        this.workMode = workMode;
    }

    public Double getSampleFreq() {
        return sampleFreq;
    }

    public void setSampleFreq(Double sampleFreq) {
        this.sampleFreq = sampleFreq;
    }

    public Double getExposure() {
        return exposure;
    }

    public void setExposure(Double exposure) {
        this.exposure = exposure;
    }

    public Integer getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(Integer faultCode) {
        this.faultCode = faultCode;
    }

    public Double getParaA() {
        return paraA;
    }

    public void setParaA(Double paraA) {
        this.paraA = paraA;
    }

    public Double getParaB() {
        return paraB;
    }

    public void setParaB(Double paraB) {
        this.paraB = paraB;
    }
}
