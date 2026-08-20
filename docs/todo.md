# 全局待办（跨模块）

> 分期权威说明：[phased_delivery.md](phased_delivery.md)  
> 需求分析：[requirements_analysis.md](requirements_analysis.md)

## 文档与命名（已完成）

- [x] 模块命名去掉 `module-` 前缀（`common` / `open` / `boot-open` / `console-*` / `boot-console`）
- [x] 写入 [phased_delivery.md](phased_delivery.md)
- [x] 更新规范、规则、`requirements_analysis.md`
- [x] 迁走旧 `module-*` 目录，建立第 1 期目录骨架

## 第 1 期：外联 `open` + `boot-open`

- [x] 父 POM + `common` / `open` / `boot-open` 骨架
- [x] `common`：WB/NB DTO、ApiResponse、ErrorCode + 单测
- [x] `open`：NB-001~004 + Orchestrator + WB API + 单元测试
- [x] `boot-open`：启动配置 + 集成测试 + 联调页 `/test.html`
- [x] `docs/api_design.md` + `boot-open/docs/route-config.md`
- [ ] Maven Wrapper（可选：本机有 `mvn` 时可生成）

**部署**：`mvn -pl boot-open -am spring-boot:run` → [http://localhost:8081/test.html](http://localhost:8081/test.html)  
**测试**：`mvn -pl common,open,boot-open -am test`

## 第 2～8 期（未开始）

| 期次 | 模块 | 状态 |
|------|------|------|
| 2 | `console-monitor` + 首次 `boot-console`（含 NB-005） | 待开 |
| 3 | `console-task` | 待开 |
| 4 | `console-stats` | 待开 |
| 5 | `console-query` | 待开 |
| 6 | `console-storage` | 待开 |
| 7 | `console-log` | 待开 |
| 8 | `console-admin` | 待开 |

每期部署/测试命令见 [phased_delivery.md](phased_delivery.md)。

## 全局后续

- [ ] `tech_design.md`：双进程拓扑、共享存储
- [ ] 澄清 TCP vs HTTP 主联调路径
- [ ] Windows 验收与 CI 矩阵
