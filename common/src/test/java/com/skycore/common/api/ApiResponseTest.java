package com.skycore.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApiResponseTest {

    @Test
    void okWrapsData() {
        ApiResponse<String> resp = ApiResponse.ok("hello");
        assertEquals(0, resp.getCode());
        assertEquals("success", resp.getMsg());
        assertEquals("hello", resp.getData());
    }

    @Test
    void failUsesErrorCode() {
        ApiResponse<Void> resp = ApiResponse.fail(ErrorCode.BAD_REQUEST);
        assertEquals(400, resp.getCode());
        assertEquals("bad request", resp.getMsg());
        assertNull(resp.getData());
    }
}
