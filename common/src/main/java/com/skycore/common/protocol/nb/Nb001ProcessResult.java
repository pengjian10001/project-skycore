package com.skycore.common.protocol.nb;

/**
 * JK-NB-001 处理后结果。
 * procStage: 1已接收 2解析中 3已打包 4待下发
 */
public class Nb001ProcessResult {

    private String transId;
    private int instrType;
    private int checkStatus;
    private int procStage;
    private String readableCommand;
    private String packedSimHex;
    private long timeStamp;

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

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getProcStage() {
        return procStage;
    }

    public void setProcStage(int procStage) {
        this.procStage = procStage;
    }

    public String getReadableCommand() {
        return readableCommand;
    }

    public void setReadableCommand(String readableCommand) {
        this.readableCommand = readableCommand;
    }

    public String getPackedSimHex() {
        return packedSimHex;
    }

    public void setPackedSimHex(String packedSimHex) {
        this.packedSimHex = packedSimHex;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
