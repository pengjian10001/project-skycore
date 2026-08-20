# skycore — 仿真数据管理软件

> Cursor AI + Maven 多模块。模块目录名**不加** `module-` 前缀。

## 当前开期

**第 1 期已实现**：外联 `common` + `open` + `boot-open`  
联调页：启动后打开 [http://localhost:8081/test.html](http://localhost:8081/test.html)

```bash
./scripts/dev-start-open.sh
# 或（注意必须带 -pl boot-open；父 POM 已 skip spring-boot:run）
mvn -pl boot-open -am spring-boot:run
```

测试：`mvn -pl common,open,boot-open -am test`  
分期说明：[docs/phased_delivery.md](docs/phased_delivery.md) · API：[docs/api_design.md](docs/api_design.md)

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
