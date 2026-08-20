# boot-open

> 外联可启动模块。本部署单元唯一 `main` / `@SpringBootApplication`。第 **1** 期部署单元。

## 职责

- 组装 `open`
- 外联进程配置（HTTP/TCP 端口、数据源）
- 独立打包与部署

## 包名

`com.skycore.boot.open`

## 启动（实现后）

```bash
./scripts/dev-start-open.sh
# 或
./mvnw -pl boot-open spring-boot:run
```

建议 HTTP 端口：`8081`。

## 文档

- [docs/module-design.md](docs/module-design.md)
- [docs/module-todo.md](docs/module-todo.md)
- [docs/route-config.md](docs/route-config.md)

## 测试

```bash
./mvnw -pl boot-open test
```
