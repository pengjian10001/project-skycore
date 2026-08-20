# 分期交付计划

> 权威分期说明。模块命名不加 `module-` 前缀。配套：[requirements_analysis.md](requirements_analysis.md)、[todo.md](todo.md)、[多模块Maven项目目录结构规范.md](多模块Maven项目目录结构规范.md)。


## 模块命名（去掉 `module-`）

| 目录 / artifactId | 包名 | 类型 | 一句话 |
|-------------------|------|------|--------|
| `common` | `com.skycore.common` | jar | 公共 DTO、错误码、协议模型 |
| `open` | `com.skycore.open` | jar | 外联业务：JK-WB、JK-NB-001~004 |
| `boot-open` | `com.skycore.boot.open` | 可执行 | 外联独立进程 |
| `console-monitor` | `com.skycore.console.monitor` | jar | 实时监控 + JK-NB-005 |
| `console-task` | `com.skycore.console.task` | jar | 任务管理 |
| `console-stats` | `com.skycore.console.stats` | jar | 数据统计 |
| `console-query` | `com.skycore.console.query` | jar | 数据查询 |
| `console-storage` | `com.skycore.console.storage` | jar | 本地存储 |
| `console-log` | `com.skycore.console.log` | jar | 日志查询 |
| `console-admin` | `com.skycore.console.admin` | jar | 系统管理 |
| `boot-console` | `com.skycore.boot.console` | 可执行 | 管控 Web 组装进程 |

依赖：

```text
boot-open    → open            → common
boot-console → console-*（已完成的） → common
```

`open` 与各 `console-*` 互不依赖。父 POM 只登记**当期及以前已开期**的模块。

旧名对照：`module-service-external` → `open`；`module-boot-external` → `boot-open`；`module-service-web` → 拆为 `console-*`；`module-boot-web` → `boot-console`。

---

## 总览：每期定位

```mermaid
flowchart LR
  P0[common最小集] --> P1[第1期 open外联]
  P1 --> P2[第2期 monitor]
  P2 --> P3[第3期 task]
  P3 --> P4[第4期 stats]
  P4 --> P5[第5期 query]
  P5 --> P6[第6期 storage]
  P6 --> P7[第7期 log]
  P7 --> P8[第8期 admin]
```

| 期次 | 定位 | 主模块 | 部署单元 |
|------|------|--------|----------|
| 准备 | 公共契约可被依赖 | `common` | 无独立进程 |
| **第 1 期** | **与外部系统交互的接口服务** | `open` + `boot-open` | 仅 `boot-open` |
| 第 2 期 | 内部页：实时监控 | `console-monitor` + 首次 `boot-console` | `boot-console`（+ 已有 `boot-open`） |
| 第 3 期 | 内部页：任务管理 | `console-task` | 同上，扩展 boot-console 依赖 |
| 第 4 期 | 内部页：数据统计 | `console-stats` | 同上 |
| 第 5 期 | 内部页：数据查询 | `console-query` | 同上 |
| 第 6 期 | 内部页：本地存储 | `console-storage` | 同上 |
| 第 7 期 | 内部页：日志查询 | `console-log` | 同上 |
| 第 8 期 | 内部页：系统管理 | `console-admin` | 同上 |

原则：**一期只新开一个业务 jar**（第 1 期例外：`open` + 其 boot；第 2 期额外引入 `boot-console` 空组装壳）。

---

## 准备：`common`

**定位**：跨期共享契约，不单独部署。

**要做的事**

- `ApiResponse`、`ErrorCode`
- 第 1 期所需 WB / NB-001~004 DTO（字段按 SRS 示例，权威字典后续再接 SPO）
- 各期需要时再增量加 DTO，避免一次做完所有 console 模型

**部署**：不部署；打 jar 被依赖。

**测试**

```bash
./mvnw -pl common test
```

- DTO 校验、错误码枚举等轻量单测

---

## 第 1 期：外联开放接口（本计划执行范围）

**定位**：面向载荷单机、仿真平台、大屏的独立服务；不含操作员页面。

**要做的事**

| 项 | 内容 |
|----|------|
| 接口 | JK-WB-001~005（HTTP 先落地；TCP 以 Port + mock/骨架） |
| 流水线 | JK-NB-001 指令处理、NB-002 TCP、NB-003 日志写、NB-004 存储写（首版内存 Port） |
| 编排 | Orchestrator：WB 入站 → NB → 出站 / 落库落日志 |
| 文档 | `docs/api_design.md`（仅外联）、`boot-open/docs/route-config.md` |
| 工程 | 父 POM、`mvnw`、`README`、`scripts/dev-start-open.sh` |
| 同步 | 规范 / `requirements_analysis` / `todo` 改为新命名；迁走旧目录 |

**不做**：任何 `console-*`、JK-NB-005、鉴权、七个原型页、真实 DB、完整 SPO 导入。

**部署**

| 项 | 约定 |
|----|------|
| 制品 | `boot-open` 可执行 jar |
| 端口 | HTTP 如 `8081`；TCP 端口配置化 |
| 启动 | `./scripts/dev-start-open.sh` 或 `./mvnw -pl boot-open spring-boot:run` |
| 进程 | **只起外联**；无 `boot-console` |

**测试**

```bash
# 单元：契约 + 外联业务
./mvnw -pl common,open test

# 集成：真实 Spring 上下文 + MockMvc/TestRestTemplate
./mvnw -pl boot-open test
```

| 层级 | 覆盖 |
|------|------|
| `open` 单测 | NB-001 解析/打包分支；NB-003/004 写入可读回；WB 校验失败错误码；出站 Client mock |
| `boot-open` 集成测 | WB-001→NB→NB-003/004；WB-003→WB-004；WB-005 返回 `code/msg/data` |

---

## 第 2 期：`console-monitor`（实时监控）

**定位**：操作员首页监控能力；承接 JK-NB-005 聚合读；对齐 `首页-浅色版.html`。

**要做的事**

- 首次创建 `boot-console`（依赖 `common` + `console-monitor`）
- NB-005 监控聚合 API；KPI / 监控列表 / 下行流 / 入库状态 / 日志监测读接口
- 读共享存储（第 1 期 open 写入的数据）；无 open jar 依赖

**部署**

| 项 | 约定 |
|----|------|
| 制品 | `boot-console` jar（端口如 `8080`） |
| 联调 | 通常同时运行 `boot-open`（产数）+ `boot-console`（展示） |
| 启动 | `./mvnw -pl boot-console spring-boot:run`（另开终端起 open） |

**测试**

```bash
./mvnw -pl console-monitor test
./mvnw -pl boot-console test   # 仅 monitor 相关 IT
```

- 单测：聚合逻辑、空数据/有数据快照  
- 集成测：HTTP 监控 API；可选对接 open 写入的内存/测试库  

---

## 第 3 期：`console-task`（任务管理）

**定位**：任务全生命周期；对齐 `1-2.1.任务管理.html`（`GN-SJJK-RWGL-*`）。

**要做的事**：任务查询/新建/列表/分布统计 API；`boot-console` POM 增加对 `console-task` 的依赖。

**部署**：只更新并重启 `boot-console`（仍可与 `boot-open` 并存）。不改 open 进程契约则不必动 `boot-open`。

**测试**

```bash
./mvnw -pl console-task test
./mvnw -pl boot-console test   # 增补 task API IT
```

---

## 第 4 期：`console-stats`（数据统计）

**定位**：仿真指令/指令集/载荷明细统计；对齐 `1-2.2.数据统计.html`。

**要做的事**：统计查询与导出相关 API；挂入 `boot-console`。

**部署 / 测试**：同第 3 期模式——只扩 `console-stats` + `boot-console` 依赖与测试。

```bash
./mvnw -pl console-stats test
./mvnw -pl boot-console test
```

---

## 第 5 期：`console-query`（数据查询）

**定位**：综合查询与载荷数据列表；对齐 `1-2.3 数据查询.html`；检索性能目标沿用 SRS（如 ≤100ms，设计期落实索引）。

**部署 / 测试**：`console-query` 单测 + `boot-console` IT。

---

## 第 6 期：`console-storage`（本地存储）

**定位**：容量/趋势/告警/节点与备份状态；对齐 `1-2.4.本地存储.html`。

**部署 / 测试**：`console-storage` 单测 + `boot-console` IT。

---

## 第 7 期：`console-log`（日志查询）

**定位**：预告警、检索、列表、实时日志流（WebSocket）；对齐 `1-3.日志查询.html`。读 open 侧 NB-003 写入的日志。

**部署 / 测试**：`console-log` 单测（含 WS 测可用 `@SpringBootTest` + 客户端）+ `boot-console` IT。

---

## 第 8 期：`console-admin`（系统管理）

**定位**：用户角色、库配置、归档、操作审计；对齐 `1-4.系统管理.html`。

**要做的事**：鉴权与角色矩阵落地（若前几期用了临时放行，本期收口）。

**部署 / 测试**：`console-admin` 单测 + `boot-console` 鉴权相关 IT；全量 `./mvnw test` 回归。

---

## 各期统一：部署与测试检查表

**部署**

| 检查项 | 第 1 期 | 第 2 期及以后 |
|--------|---------|----------------|
| 必起进程 | `boot-open` | `boot-open` + `boot-console` |
| 独立扩缩 | 外联可单独扩 | 管控可单独扩；互不 jar 依赖 |
| 健康检查 | open 的 `/actuator/health`（若启用） | console 同理 |
| 配置 | `boot-open` 专用 yml | `boot-console` 专用 yml；共享库连接只读/读写权限按期约定 |

**测试**

| 检查项 | 约定 |
|--------|------|
| 单测落点 | 业务 jar 的 `src/test`（`open` 或当期 `console-*`） |
| 集成测落点 | 对应 `boot-*` 的 `src/test` |
| 本期命令 | `./mvnw -pl <本期模块>[,boot-xxx] test` |
| 回归 | 里程碑或第 8 期末：`./mvnw test` |
| 禁止 | 模块根目录 `tests/`；在未开期的 console 模块里写代码 |

---

## 第 1 期路径清单

```text
pom.xml                          # modules: common, open, boot-open（已建）
common/                          # 已建骨架
open/
boot-open/
docs/phased_delivery.md          # 本文件
docs/api_design.md               # 待写（外联契约）
docs/todo.md
docs/requirements_analysis.md
docs/多模块Maven项目目录结构规范.md
.cursor/rules/maven-multi-module.mdc
scripts/dev-start-open.sh
README.md
```

旧目录 `module-*` 已删除并迁至上述命名。

---

## 建议执行顺序（仅第 1 期）

1. 规范与分析文档改名（去 `module-`）+ 目录迁徙  
2. 父 POM + `common` / `open` / `boot-open` 可编译  
3. `common` DTO + 单测  
4. `open`：NB-001~004 + Orchestrator + 单测  
5. `open`：WB-001~005 + 单测  
6. `boot-open` 集成测 + `dev-start-open.sh`  
7. 回填 `api_design.md` / `route-config.md` / 分期 `todo.md`  
