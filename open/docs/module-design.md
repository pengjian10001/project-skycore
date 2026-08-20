# open 边界

## 放入

- WB-001~005 适配
- NB-001~004 Port/Service 与 Orchestrator
- 流水线入库与运行日志写入

## 不放入

- 操作员页面 API、鉴权
- JK-NB-005（属 `console-monitor`）
- `@SpringBootApplication`

不通过 jar 依赖调用任何 `console-*`。
