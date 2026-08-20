# open

> 外联业务模块（jar）。依赖 `common`；**不依赖**任何 `console-*`。第 **1** 期主交付。

## 职责

- 外部接口 JK-WB-001~005（含大屏）
- 进程内 JK-NB-001~004（指令处理、TCP、日志写、存储写）
- 后台流水线：`GN-HT-*`
- 载荷/仿真数据与运行日志写入

## 包名

`com.skycore.open`

## 部署

由 `boot-open` 组装，独立进程。见 [docs/phased_delivery.md](../docs/phased_delivery.md) 第 1 期。

## 文档

- [docs/module-design.md](docs/module-design.md)
- [docs/module-todo.md](docs/module-todo.md)

## 测试

```bash
./mvnw -pl open test
```
