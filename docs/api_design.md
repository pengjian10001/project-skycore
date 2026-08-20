# 外联 API 设计（第 1 期）

> 进程：`boot-open`（默认 `http://localhost:8081`）  
> 业务模块：`open`  
> 联调页：`http://localhost:8081/test.html`

统一响应：`{ "code": 0, "msg": "success", "data": ... }`（对齐 [docs/original/图扑软件-项目接口文档模板.docx](original/图扑软件-项目接口文档模板.docx)）。

## 1. 开发依据材料

| 材料 | 路径 | 在本模块中的用途 |
|------|------|------------------|
| 需求规格说明 SRS | [original/01-需求规格说明-太阳极轨天文台有效载荷集成测试仿真数据管理软件-20260527.docx](original/01-需求规格说明-太阳极轨天文台有效载荷集成测试仿真数据管理软件-20260527.docx) | 定义 JK-WB / JK-NB 接口职责与方向（谁收谁发） |
| **SPO 字典** | [original/SPO.xlsx](original/SPO.xlsx)（129 个 sheet） | **字段级权威**：指令码、指令格式、工程遥测、科学帧、判读/约束规则 |
| **接口.docx** | [original/接口.docx](original/接口.docx) | **链路帧级权威**：MHI→载荷 RS422 串行指令包（`0xEB 0x90` 包头、命令类型、累加和等） |
| 图扑接口模板 | [original/图扑软件-项目接口文档模板.docx](original/图扑软件-项目接口文档模板.docx) | WB-005 及统一 JSON 外壳 `code/msg/data` |

**原则：** SRS 决定「有哪些接口」；**编解码与校验以 SPO.xlsx + 接口.docx 为准**，不以 SRS 中的示例字段（如 `MAG_X`）为最终协议。

> 实现状态：
> - **MHI 试点已落地**：WB-001 支持 `frameType` + `rawHex`，按 SPO 字段表解帧（`MHI_SCI_20000` / `MHI_ENG_13001`）；可选 `validateRs422=true` 走 接口.docx 表 43 校验。
> - 其余载荷 / sheet 仍为协议落地目标；SRS 示例字段（magX 等）仅作兼容联调。

---

## 2. SPO.xlsx sheet 分类（便于对照）

| 类别 | sheet 特征 / 示例 | 用途 |
|------|-------------------|------|
| **全局规则** | `3.参数判读规则`、`4.指令判读规则`、`5.指令使用约束`、`130.指令判读规则` | 参数阈值、指令延时/成对等判读 |
| **全局/平台指令** | `29.指令列表`、`30.CMD_PMU_遥控指令`、`131.CMD_EGSE_其他指令` | PMU/EGSE 指令标识与码字 |
| **载荷指令** | 各载荷下的 `指令列表`、`指令格式表`、`*_指令判读规则`、`防护门/偏振轮设置指令表` 等 | 指令识别、打包/解包、约束校验 |
| **工程遥测** | `工程遥测参数_*`（含 PMU 快/慢遥、1553B、各载荷工程/遥测） | WB-001 入站遥测/工程参数解析 |
| **科学数据** | `科学数据帧头_*`、`科学数据格式*`、`科学数据-图像*`、`图像数据包_*`、`图像信息包*`、`磁通门磁强计科学数据格式`、`在轨定标单元科学数据格式` | WB-001 入站科学数据帧解析 |

完整 sheet 清单共 **129** 个，见仓库归档 `docs/original/SPO.xlsx`。

---

## 3. 接口 ↔ SPO sheet / 接口.docx 映射

### 3.1 JK-WB（外部）

| 接口 | HTTP / 形态 | 主责材料 | 使用的 SPO sheet | 使用的 接口.docx |
|------|-------------|----------|-----------------|------------------|
| **WB-001** 收载荷打包数据 | `POST /api/open/wb/001/payload-data` | SRS WB-001 + **SPO 工程遥测/科学数据** | **全部「工程遥测参数_*」**（约 sheet 004–025、030–032、044–046、064–065、091–092、098–099、105–106、113–115、121–127 等）；**全部「科学数据帧头_* / 科学数据格式* / 图像数据包_* / 图像信息包*」**（约 026、033–040、047–052、056–058、066–076、081–087、093–094、100–101、107–108、112、116–117 等）。按 `payloadId`/APID/帧类型选对应 sheet 解帧 | 入站若为 RS422 原始流：参照包头 `0xEB90`、载荷标识、累加和规则做帧同步与校验（与 SPO 字段表配合） |
| **WB-002** 发仿真平台（解析后） | 出站 `OutboundClient.sendToSimPlatform`（无独立 URL） | SRS WB-002 | **同 WB-001**：输出为「按 SPO 字段解出后的结构化量」；不新增 sheet，而是消费 WB-001 解析结果 | 一般不再走 RS422 原始帧，而是业务结构化包 |
| **WB-003** 收仿真指令 | `POST /api/open/wb/003/sim-command` | SRS WB-003 + **SPO 指令表** | **全局**：`29.指令列表`、`30.CMD_PMU_遥控指令`、`131.CMD_EGSE_其他指令`；**各载荷**：全部 `指令列表`、`指令格式表`（如 041–044 MHI、053–055 MEI、060–064 FIS、077–081 VISCOR、088–092 VLACOR、095–099 XIT、102–106 SWIMS、109–113 SWIRPA、118–122 MAG 等）；**规则**：`3/4/5.*判读/约束`、`31.PMU _指令判读规则`、各 `*_指令判读规则`、`130.指令判读规则` | 入站 JSON 中的指令码需能映射到 SPO「指令标识/指令码字」；真正下发载荷前再套 接口.docx 帧格式 |
| **WB-004** 转发载荷指令 | 出站 `OutboundClient.forwardToPayload` | SRS WB-004 + **接口.docx 帧** + SPO 指令格式 | **同 WB-003 的指令相关 sheet**（用「指令格式表」填有效数据区；用判读规则做发送前校验） | **主用**：串行指令格式（表 43：`EB 90` + 载荷标识 + 命令类型 + 有效数据 + 累加和）；表 44 命令类型（校时 `0x13`、工参轮询 `0x25`、指向 `0x3B`、平台状态 `0x63`、注入 `0x87`、预关机 `0x94`、复位 `0x1A` 等） |
| **WB-005** 视图/大屏 | `GET /api/open/wb/005/dashboard` | SRS WB-005 + 图扑模板 | **间接使用**：展示已入库的解析结果字段名/单位来自 SPO（工程遥测/科学/指令统计）；本接口自身不做解帧，不绑定单一 sheet | 不使用 |

### 3.2 JK-NB（进程内）

| 接口 | 实现类.方法 | 主责材料 | 使用的 SPO sheet | 使用的 接口.docx |
|------|-------------|----------|-----------------|------------------|
| **NB-001** 指令信息处理 | `InstructionProcessService.process` | SRS NB-001 / `GN-HT-ZL` | **解析载荷指令包**：各 `指令列表` + `指令格式表` + 对应 `*_指令判读规则`；**打包仿真/下发数据**：同上格式表填有效数据；**参数合法性**：`3.参数判读规则`、`5.指令使用约束` | 当输入/输出为 RS422 字节流时，按 接口.docx 拆包/组包，再映射到 SPO 字段 |
| **NB-002** TCP 通信 | `TcpCommunicationPort.send` | SRS NB-002 / `GN-HT-JK` | **不直接绑 sheet**（传输通道）；载荷内容已是 NB-001 按 SPO/接口.docx 处理好的报文 | 传输的 payload 字节应符合 接口.docx 帧结构（若对端是载荷/MHI） |
| **NB-003** 日志 | `LogWritePort.write` | SRS NB-003 | **不直接绑 sheet**；日志中可引用 SPO 的指令标识/参数编号便于追溯 | 不使用 |
| **NB-004** 存储 | `StoreWritePort.store` | SRS NB-004 | **间接**：落库内容的字段模式应对齐 SPO（工程遥测行、科学帧字段、指令记录）；元数据可用 sheet 名/星上标识做字典版本 | 可选存原始 RS422 帧（按 接口.docx）+ 解析后 SPO 字段 |

### 3.3 按载荷速查（指令侧 sheet）

| 载荷/单元 | SPO 中典型 sheet（指令） | 主要服务接口 |
|-----------|--------------------------|--------------|
| PMU / 全局 | `29.指令列表`、`30.CMD_PMU_*`、`31.PMU _指令判读规则`、`131.CMD_EGSE_*` | WB-003/004、NB-001 |
| MHI 磁场速度场 | `43.指令格式表`、`44.指令列表`、`45.MHI_指令判读规则` | 同上 |
| MEI 多波段极紫外 | `55–57` 指令格式/列表/判读 | 同上 |
| FIS 全日面极紫外 | `62–65`（含防护门） | 同上 |
| VISCOR 白光日冕 | `79–82`（含偏振轮/防护门） | 同上 |
| VLACOR 日球层 | `90–92` | 同上 |
| XIT X 射线 | `97–99` | 同上 |
| SWIMS 离子质谱 | `104–106` | 同上 |
| SWIRPA 离子阻滞势 | `111–113` | 同上 |
| MAG 磁强计 | `120–122` | 同上 |

工程遥测 / 科学数据 sheet 按 **WB-001** 的 APID/帧类型选用（见第 2 节分类）。

---

## 4. JK-WB HTTP 一览（当前实现）

| ID | Method | Path | 说明 |
|----|--------|------|------|
| WB-001 | POST | `/api/open/wb/001/payload-data` | 收载荷数据 → NB-001 → 存/日志 → WB-002 mock |
| WB-002 | （出站） | `OutboundClient.sendToSimPlatform` | 无独立 HTTP |
| WB-003 | POST | `/api/open/wb/003/sim-command` | 收仿真指令 → NB-001 → WB-004 mock |
| WB-004 | （出站） | `OutboundClient.forwardToPayload` | 无独立 HTTP |
| WB-005 | GET | `/api/open/wb/005/dashboard` | 大屏/联调快照 |

### 请求示例（联调；正式字段以 SPO 为准）

#### WB-001 — MHI 科学帧头试点（SPO sheet `41.科学数据帧头_20000_*`）

样例帧（14 字节）：`EB90 | 2000 | 0001 | 000A | 0000000186A0`  
对应字段：同步码 / 包标识 / 包序 / 包长=10 / 系统时间码=100000。

```bash
curl -s -X POST 'http://localhost:8081/api/open/wb/001/payload-data' \
  -H 'Content-Type: application/json' \
  -d '{
    "payloadId": "PL-MHI",
    "frameType": "MHI_SCI_20000",
    "rawHex": "EB90 2000 0001 000A 0000000186A0"
  }' | jq .
```

期望 `data.spoFields` 含：

| 星上标识 | 值 |
|----------|-----|
| SCI200001 | `"0xEB90"` |
| SCI200002 | `"0x2000"` |
| SCI200003 | `"0x0001"` |
| SCI200004 | `10` |
| SCI200005 | `100000` |

#### WB-001 — MHI 工程参数试点（SPO sheet `32.工程遥测参数_13001_*`）

```bash
curl -s -X POST 'http://localhost:8081/api/open/wb/001/payload-data' \
  -H 'Content-Type: application/json' \
  -d '{
    "payloadId": "PL-MHI",
    "frameType": "MHI_ENG_13001",
    "rawHex": "000000000000000000000000EB901301000200080000000000000507"
  }' | jq .
```

#### WB-001 — RS422 校验（接口.docx 表 43）

`validateRs422=true` 时先按包头 `EB90` + 累加和校验，再解 SPO。科学帧头一般**不要**开此开关（帧结构不同于表 43 指令包）。校验逻辑见 `Rs422FrameCodec` 单测样例帧：`EB9001250100010028`。

#### WB-001 — 兼容旧 SRS 示例字段

```bash
curl -s -X POST 'http://localhost:8081/api/open/wb/001/payload-data' \
  -H 'Content-Type: application/json' \
  -d '{
    "payloadId": "PL-IT-001",
    "satTime": 1710000000000,
    "magX": 1.0,
    "magY": 2.0,
    "magZ": 2.0,
    "status": 1,
    "rawDigest": "AABB"
  }' | jq .
```

#### WB-003

```bash
curl -s -X POST 'http://localhost:8081/api/open/wb/003/sim-command' \
  -H 'Content-Type: application/json' \
  -d '{
    "cmdTime": 1710000000000,
    "cmdSeq": 9,
    "targetId": 2,
    "cmdCode": 1,
    "workMode": 1,
    "sampleFreq": 5.0,
    "exposure": 0.5
  }' | jq .
```

支持的试点 `frameType`：`MHI_SCI_20000`、`MHI_ENG_13001`（见 `MhiPilotDictionary`）。

---

## 5. JK-NB 实现类

| ID | 实现类 | 关键方法 |
|----|--------|----------|
| NB-001 | `InstructionProcessService` | `process` |
| NB-002 | `TcpCommunicationPort` | `send` |
| NB-003 | `LogWritePort` | `write` |
| NB-004 | `StoreWritePort` | `store` |

编排：`MainModuleOrchestrator.ingestPayloadData` / `ingestSimCommand` / `dashboardSnapshot`。

---

## 6. 联调辅助

| Method | Path | 说明 |
|--------|------|------|
| GET | `/api/open/debug/state` | 查看日志/存储/TCP outbox |
| POST | `/api/open/debug/reset` | 清空内存态 |

---

## 7. 错误码

| code | 含义 |
|------|------|
| 0 | 成功 |
| 400 | 参数校验失败 |
| 502 | 转发/TCP 失败 |
| 500 | 处理异常 |

---

## 8. 后续落地清单（相对本映射）

1. ~~`common`：导入 SPO 字典模型（MHI 试点）~~ — 已有 `MhiPilotDictionary` / `SpoFrameParser` / `Rs422FrameCodec`。  
2. ~~`open` WB-001：按帧类型解帧~~ — 已支持 `MHI_SCI_20000` / `MHI_ENG_13001`。  
3. 扩展其余载荷 sheet；WB-003/004 按指令格式表组包并强制套 接口.docx。  
4. 从 SPO.xlsx 自动导入字段表（替代硬编码试点字典）。
