package com.skycore.common.api;

public enum ErrorCode {
    OK(0, "success"),
    BAD_REQUEST(400, "bad request"),
    NOT_FOUND(404, "not found"),
    PROCESS_ERROR(500, "process error"),
    FORWARD_ERROR(502, "forward error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
