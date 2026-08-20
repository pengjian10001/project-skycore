# boot-open 路由/端口

| 用途 | 值 |
|------|-----|
| HTTP | `8081` |
| 联调页 | [http://localhost:8081/test.html](http://localhost:8081/test.html) |

## API

| 路径 | 说明 |
|------|------|
| `POST /api/open/wb/001/payload-data` | WB-001 |
| `POST /api/open/wb/003/sim-command` | WB-003 |
| `GET /api/open/wb/005/dashboard` | WB-005 |
| `GET /api/open/debug/state` | 联调状态 |
| `POST /api/open/debug/reset` | 清空内存 |

详见 [docs/api_design.md](../../docs/api_design.md)。
