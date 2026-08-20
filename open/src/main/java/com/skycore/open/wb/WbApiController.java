package com.skycore.open.wb;

import com.skycore.common.api.ApiResponse;
import com.skycore.common.protocol.wb.Wb001PayloadDataRequest;
import com.skycore.common.protocol.wb.Wb003SimCommandRequest;
import com.skycore.open.nb.LogWritePort;
import com.skycore.open.nb.StoreWritePort;
import com.skycore.open.nb.TcpCommunicationPort;
import com.skycore.open.outbound.OutboundClient;
import com.skycore.open.pipeline.MainModuleOrchestrator;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/open")
public class WbApiController {

    private final MainModuleOrchestrator orchestrator;
    private final LogWritePort logWritePort;
    private final StoreWritePort storeWritePort;
    private final TcpCommunicationPort tcpCommunicationPort;
    private final OutboundClient outboundClient;

    public WbApiController(MainModuleOrchestrator orchestrator,
                           LogWritePort logWritePort,
                           StoreWritePort storeWritePort,
                           TcpCommunicationPort tcpCommunicationPort,
                           OutboundClient outboundClient) {
        this.orchestrator = orchestrator;
        this.logWritePort = logWritePort;
        this.storeWritePort = storeWritePort;
        this.tcpCommunicationPort = tcpCommunicationPort;
        this.outboundClient = outboundClient;
    }

    /** JK-WB-001 */
    @PostMapping("/wb/001/payload-data")
    public ApiResponse<Map<String, Object>> receivePayloadData(@Valid @RequestBody Wb001PayloadDataRequest request) {
        return ApiResponse.ok(orchestrator.ingestPayloadData(request));
    }

    /** JK-WB-003 */
    @PostMapping("/wb/003/sim-command")
    public ApiResponse<Map<String, Object>> receiveSimCommand(@Valid @RequestBody Wb003SimCommandRequest request) {
        return ApiResponse.ok(orchestrator.ingestSimCommand(request));
    }

    /** JK-WB-005 大屏视图（图扑风格） */
    @GetMapping("/wb/005/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        return ApiResponse.ok(orchestrator.dashboardSnapshot());
    }

    /** 联调辅助：查看内存日志/存储 */
    @GetMapping("/debug/state")
    public ApiResponse<Map<String, Object>> debugState(
            @RequestParam(defaultValue = "20") int limit) {
        Map<String, Object> state = new LinkedHashMap<>();
        state.put("logs", logWritePort.listRecent(limit));
        state.put("stores", storeWritePort.listRecent(limit));
        state.put("tcpOutbox", tcpCommunicationPort.snapshotOutbox());
        state.put("tcpConnected", tcpCommunicationPort.isConnected());
        return ApiResponse.ok(state);
    }

    /** 联调辅助：清空内存态 */
    @PostMapping("/debug/reset")
    public ApiResponse<Map<String, Object>> reset() {
        logWritePort.clear();
        storeWritePort.clear();
        tcpCommunicationPort.clear();
        tcpCommunicationPort.setConnected(true);
        outboundClient.clear();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("reset", true);
        return ApiResponse.ok(result);
    }
}
