# common

> 公共基础模块（jar）。被 `open`、各 `console-*` 依赖。不加 `module-` 前缀。

## 职责

- SPO / 协议字典模型、枚举与常量（权威源：`docs/original/SPO.xlsx`）
- 跨模块 DTO、错误码、通用工具
- 无业务编排、无 HTTP/TCP 适配、无启动类

## 包名

`com.skycore.common`

## 开期

准备 / 贯穿。第 1 期先落 WB 与 NB-001~004 最小 DTO。

## 文档

- [docs/module-design.md](docs/module-design.md)
- [docs/module-todo.md](docs/module-todo.md)

## 测试

```bash
./mvnw -pl common test
```
