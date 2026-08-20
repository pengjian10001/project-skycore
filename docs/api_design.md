# 外联 API 设计（第 1 期）

> 进程：`boot-open`（默认 `http://localhost:8081`）  
> 业务模块：`open`  
> 联调页：`http://localhost:8081/test.html`

统一响应：`{ "code": 0, "msg": "success", "data": ... }`（图扑风格兼容）。

## JK-WB

| ID | Method | Path | 说明 |
|----|--------|------|------|
| WB-001 | POST | `/api/open/wb/001/payload-data` | 收载荷打包数据 → 解析 → 存日志 → 转发仿真平台（mock） |
| WB-002 | （出站） | 由编排触发 `OutboundClient` | 第 1 期内存记录，无独立 HTTP |
| WB-003 | POST | `/api/open/wb/003/sim-command` | 收仿真指令 → 打包 → 转发载荷（mock） |
| WB-004 | （出站） | 由编排触发 | 同上 |
| WB-005 | GET | `/api/open/wb/005/dashboard` | 大屏/联调快照 |

### WB-001 请求示例

```json
{
  "payloadId": "PL-MHI-001",
  "satTime": 1710000000000,
  "magX": 12.5,
  "magY": -3.2,
  "magZ": 8.1,
  "status": 1,
  "rawDigest": "EB90AABB"
}
```

### WB-003 请求示例

```json
{
  "cmdTime": 1710000000000,
  "cmdSeq": 1001,
  "targetId": 3,
  "cmdCode": 1,
  "workMode": 1,
  "sampleFreq": 10,
  "exposure": 1.5
}
```

## JK-NB（进程内）

| ID | 实现类 | 说明 |
|----|--------|------|
| NB-001 | `InstructionProcessService` | 指令解析 / 仿真打包 |
| NB-002 | `TcpCommunicationPort` | TCP 出站骨架（内存 outbox） |
| NB-003 | `LogWritePort` | 日志写（内存） |
| NB-004 | `StoreWritePort` | 存储写（内存） |

## 联调辅助

| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/open/debug/state` | 查看日志/存储/TCP outbox |
| POST | `/api/open/debug/reset` | 清空内存态 |

## 错误码

| code | 含义 |
|------|------|
| 0 | 成功 |
| 400 | 参数校验失败 |
| 502 | 转发/TCP 失败 |
| 500 | 处理异常 |
