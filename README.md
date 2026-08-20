# skycore — 仿真数据管理软件

> Cursor AI + Maven 多模块。模块目录名**不加** `module-` 前缀。

## 当前开期

**第 1 期**：外联开放接口 — `common` + `open` + `boot-open`  
完整分期：[docs/phased_delivery.md](docs/phased_delivery.md)

## 目录

| 路径 | 说明 |
|------|------|
| `docs/` | 需求、分期、规范、原始归档 |
| `common/` | 公共 jar |
| `open/` | 外联业务 jar |
| `boot-open/` | 外联可启动进程 |
| `scripts/` | 全局脚本 |
| `.cursor/` | 团队 AI 规则 |

管控侧 `console-*` / `boot-console` 在第 2 期起按次创建，见分期文档。

## 依赖方向

```text
boot-open → open → common
```

## 文档入口

- [docs/requirements.md](docs/requirements.md)
- [docs/requirements_analysis.md](docs/requirements_analysis.md)
- [docs/phased_delivery.md](docs/phased_delivery.md)
- [docs/todo.md](docs/todo.md)
- [docs/多模块Maven项目目录结构规范.md](docs/多模块Maven项目目录结构规范.md)
