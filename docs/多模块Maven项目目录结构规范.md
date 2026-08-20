# Maven 多模块项目目录结构规范

本文是人读约定。Agent 执行 `.cursor/rules/maven-multi-module.mdc`（`alwaysApply: true`）。改目录或依赖约定时，**两处必须一起改**。

分期开发见 [phased_delivery.md](phased_delivery.md)。

> 核心原则
>
> 1. `.cursor/` 只在项目根；子模块禁止再创建 `.cursor/`
> 2. 根 `docs/`：跨模块、全局文档；子模块 `docs/`：仅本模块细节
> 3. 业务代码只写在对应子模块 `src/main`；测试只写在 `src/test`
> 4. 模块目录名 / `artifactId` **不加** `module-` 前缀（如 `common`、`open`、`boot-open`）
> 5. 依赖 DAG：`boot-open` → `open` → `common`；`boot-console` → `console-*` → `common`。禁止环依赖
> 6. 两个独立部署单元：`boot-open`（外联）、`boot-console`（管控）。父 POM 只登记已开期模块
> 7. 一期原则上只新开一个业务 jar（第 1 期为 `open`+`boot-open`；第 2 期起每次一个 `console-*`）

```text
项目根/
├─ .cursor/
├─ docs/
│  ├─ original/
│  ├─ requirements.md
│  ├─ requirements_analysis.md
│  ├─ phased_delivery.md          【分期定位、部署、测试】
│  ├─ tech_design.md
│  ├─ api_design.md
│  ├─ todo.md
│  └─ 多模块Maven项目目录结构规范.md
├─ scripts/
│  └─ dev-start-open.sh           【第 1 期】
├─ pom.xml
├─ README.md
├─ common/                        【jar｜公共】
├─ open/                          【jar｜外联业务｜第 1 期】
├─ boot-open/                     【可执行｜外联部署｜第 1 期】
├─ console-monitor/               【jar｜第 2 期起按次引入，未开期不建】
├─ console-task/                  …
├─ console-stats/
├─ console-query/
├─ console-storage/
├─ console-log/
├─ console-admin/
└─ boot-console/                  【可执行｜管控组装｜第 2 期起】
```

每个子模块统一结构：`pom.xml`、`README.md`、`docs/`、`src/main`、`src/test`、可选 `scripts/`。

传统 `packaging=war` 仅在明确需要 JSP/`WEB-INF` 时使用。文件名与模块名只用 ASCII 连字符 `-`。

## 文档分层规则

1. 根 `docs/`：跨 2+ 模块、整体架构、分期计划、全局接口与 todo。
2. 子模块 `xxx/docs/`：仅本模块设计、私有表、内部接口、模块 todo。
3. 模块 `README.md`：简介、边界、对外能力、启动；不写长篇设计。
4. 实现 UI 时用 `@` 引用 `docs/original/` 下具体页面。

## 模块依赖（DAG）

```text
boot-open    → open            → common
boot-console → console-*（已完成） → common
```

- `open` 与任意 `console-*` **互不依赖**；`console-*` 之间互不依赖。
- 跨进程协作：共享库表 / 消息 / 约定接口。
- 包名：`com.skycore.common`、`com.skycore.open`、`com.skycore.boot.open`、`com.skycore.console.monitor`、`com.skycore.boot.console` 等。
- 每个 `boot-*`：该单元内唯一 `main`、唯一 `@SpringBootApplication`、配置 `spring-boot-maven-plugin`。业务 jar 禁止放启动类。

## Maven 标准源码目录

- `src/main/java`、`src/main/resources`
- `src/test/java`、`src/test/resources`
- 禁止模块根 `tests/`；不要改 `testSourceDirectory`

## `.cursor/rules/maven-multi-module.mdc`

以仓库中该文件为准（命名无 `module-` 前缀、双 boot、分期登记 modules）。

## 父 POM 最小模板（第 1 期）

```xml
<groupId>com.skycore</groupId>
<artifactId>skycore-parent</artifactId>
<version>1.0.0</version>
<packaging>pom</packaging>
<modules>
    <module>common</module>
    <module>open</module>
    <module>boot-open</module>
</modules>
```

第 2 期起再向 `<modules>` 增加 `console-monitor`、`boot-console` 等。子模块依赖兄弟模块时不写 `<version>`。

## 可启动模块

- `boot-open` 依赖 `open`，独立端口（如 8081）
- `boot-console` 依赖已完成的 `console-*`，独立端口（如 8080）

仅 boot 模块配置 `spring-boot-maven-plugin`。

## 旧名对照

| 旧 | 新 |
|----|-----|
| `module-common` | `common` |
| `module-service-external` | `open` |
| `module-boot-external` | `boot-open` |
| `module-service-web` | 拆为 `console-*` |
| `module-boot-web` | `boot-console` |

## gitignore / .cursorignore

以仓库根文件为准。办公文档默认忽略，仅 `docs/original/` 允许归档。
