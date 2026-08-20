# 全局待办（跨模块）

> 分期权威说明：[phased_delivery.md](phased_delivery.md)  
> 需求分析：[requirements_analysis.md](requirements_analysis.md)

## 文档与命名（已完成）

- [x] 模块命名去掉 `module-` 前缀（`common` / `open` / `boot-open` / `console-*` / `boot-console`）
- [x] 写入 [phased_delivery.md](phased_delivery.md)
- [x] 更新规范、规则、`requirements_analysis.md`
- [x] 迁走旧 `module-*` 目录，建立第 1 期目录骨架

## 第 1 期：外联 `open` + `boot-open`（进行中）

- [ ] 父 POM + `common` / `open` / `boot-open` 可编译（含 Maven Wrapper）
- [ ] `common`：WB/NB-001~004 DTO、ApiResponse、ErrorCode + 单测
- [ ] `open`：NB-001~004 + Orchestrator + WB-001~005 + 单元测试
- [ ] `boot-open`：启动配置 + 集成测试
- [ ] `docs/api_design.md`（外联）+ `boot-open/docs/route-config.md` 回填
- [ ] `scripts/dev-start-open.sh` 可用

**部署**：只起 `boot-open`（如 8081）  
**测试**：`./mvnw -pl common,open test`；`./mvnw -pl boot-open test`

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
