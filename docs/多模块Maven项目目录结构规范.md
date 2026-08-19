# Maven 多模块项目目录结构规范

本文是人读约定。Agent 执行 `.cursor/rules/maven-multi-module.mdc`（`alwaysApply: true`）。改目录或依赖约定时，**两处必须一起改**。

> 核心原则
>
> 1. `.cursor/` 只在项目根，团队共享 AI 配置，纳入 git，对所有子模块生效；子模块禁止再创建 `.cursor/`
> 2. 根 `docs/`：跨模块、全局、整体级别的文档
> 3. 每个子模块内部 `docs/`：仅本模块关心的细节；模块入口简介放模块根 `README.md`
> 4. 根 `scripts/`：全局构建、环境、发布脚本；子模块 `scripts/`：模块私有脚本
> 5. 业务代码只写在对应子模块 `src/main`；测试只写在对应模块 `src/test`
> 6. `mcp.json` 禁止写密钥。`.cursor/` 只提交 `rules/`、`agents/`、`skills/`、`mcp.json`；忽略 `cache/`、`sessions/`、`logs/`、`*.local`
> 7. 模块间依赖必须是 DAG：`module-boot` → `module-service-*` → `module-common`。禁止环依赖。父 POM 的 `<parent>` 只做版本与插件管理，不表达业务父子

```text
项目根/
├─ .cursor/                          【团队共享 AI 配置，git 提交，全局生效】
│  ├─ rules/                         mdc 编码约束；本项目见 maven-multi-module.mdc
│  ├─ mcp.json                       项目 MCP 配置（禁止存放密钥）
│  ├─ agents/                        团队自定义 Agent（*.md）
│  └─ skills/                        必须是 skills/<name>/SKILL.md，不要把 SKILL.md 直接放在 skills/ 根下
├─ .cursorignore                     二进制原稿等不进入 Agent 索引（仍可 git 归档）
├─ .gitignore
├─ .mvn/wrapper/                     Maven Wrapper（含 jar，需提交）
├─ mvnw
├─ mvnw.cmd
├─ docs/                             【全局｜跨模块、整体项目文档】
│  ├─ original/                      原始输入：需求/原型归档（pdf、docx、原型 HTML 等）
│  ├─ requirements.md                项目整体业务需求
│  ├─ requirements_analysis.md       AI 输出整体需求分析
│  ├─ tech_design.md                 全局整体技术架构方案
│  ├─ api_design.md                  跨模块公共接口、网关接口设计
│  └─ todo.md                        全局跨模块任务拆解清单
├─ scripts/                          【项目全局脚本：构建、环境、发布】
│  ├─ build-all.sh
│  ├─ clean-all.sh
│  └─ dev-start.sh
├─ pom.xml                           Maven 父 POM：统一版本、dependencyManagement、pluginManagement、modules
├─ README.md                         项目总说明、模块总览、团队使用指引
│
├─ module-common/                    公共基础模块（jar，被其他模块依赖）
│  ├─ pom.xml
│  ├─ README.md                      模块职责、对外能力、快速上手
│  ├─ docs/                          仅本模块相关细节
│  │  ├─ module-design.md
│  │  ├─ module-db-schema.md
│  │  ├─ module-api-internal.md
│  │  └─ module-todo.md
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/
│  │  │  └─ resources/
│  │  └─ test/
│  │     ├─ java/
│  │     └─ resources/
│  └─ scripts/
│     └─ module-build.sh
│
├─ module-service-a/                 业务模块 A（jar，依赖 common）
│  ├─ pom.xml
│  ├─ README.md
│  ├─ docs/
│  │  ├─ module-design.md
│  │  ├─ module-db-schema.md
│  │  └─ module-todo.md
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/
│  │  │  └─ resources/
│  │  └─ test/
│  │     ├─ java/
│  │     └─ resources/
│  └─ scripts/
│
├─ module-service-b/                 业务模块 B（jar，依赖 common；不要依赖 service-a）
│  ├─ pom.xml
│  ├─ README.md
│  ├─ docs/
│  │  ├─ module-design.md
│  │  └─ module-todo.md
│  ├─ src/
│  │  ├─ main/
│  │  │  ├─ java/
│  │  │  └─ resources/
│  │  └─ test/
│  │     ├─ java/
│  │     └─ resources/
│  └─ scripts/
│
└─ module-boot/                      唯一可启动模块（Spring Boot jar）
   ├─ pom.xml                        唯一配置 spring-boot-maven-plugin
   ├─ README.md
   ├─ docs/
   │  ├─ module-design.md
   │  └─ route-config.md
   ├─ src/
   │  ├─ main/
   │  │  ├─ java/                    唯一 @SpringBootApplication 与 main
   │  │  └─ resources/               application.yml 等
   │  └─ test/
   │     ├─ java/
   │     └─ resources/
   └─ scripts/
```

传统 `packaging=war` 仅在明确需要 JSP / `WEB-INF` 时使用，那时才建 `src/main/webapp/`。默认不要建该目录。

文件名、模块目录名、XML 标签内容只使用 ASCII 连字符 `-`（U+002D），不要用非断行连字符 `‑`（U+2011）。

## 文档分层规则

1. 放到根 `docs/`：涉及 2 个及以上模块；整体架构；全局业务需求；跨模块接口；项目级归档；全局任务。
2. 放到子模块 `xxx/docs/`：只和当前这一个模块有关；内部实现；私有表；内部接口；模块待办。
3. 模块 `README.md`：只写简介、边界、对外能力、快速启动，不写长篇详细设计。
4. 禁止子模块内新建 `.cursor/`，全部复用根目录 AI 配置。
5. 实现 UI 时用 `@` 引用 `docs/original/` 下的具体页面，不要把整个 `original/` 塞进上下文。

## 模块依赖（DAG）

父 POM 继承 ≠ 模块间依赖。

- 允许：`module-boot` 依赖各个 `module-service-*`；各 service 依赖 `module-common`。
- 禁止：`service-a` ↔ `service-b` 环依赖；service 依赖 boot；用 `<parent>` 表达业务层级。
- 兄弟模块若都依赖 common，在各自 POM 声明对 common 的依赖即可，不要互相依赖。
- 版本只在父 POM `dependencyManagement` 写一次；子模块依赖兄弟模块时不写 `<version>`。

包名与模块对应，例如 `com.xxx.common`、`com.xxx.service.a`、`com.xxx.boot`。不要把多个模块的类塞进同一个根包。

`module-boot` 只做组装与启动：唯一 `main`、唯一 `@SpringBootApplication`、唯一 `spring-boot-maven-plugin`。库模块与 service 模块禁止放启动类。

## Maven 标准源码目录

子模块统一使用 Maven 约定目录，**不要**在模块根再建 `tests/`，也无需在 POM 里改 `testSourceDirectory`。

- `src/main/java`：业务源码
- `src/main/resources`：主资源（配置、mapper 等）
- `src/main/webapp`：可选。仅 `packaging=war` 时需要。Spring Boot 可执行 jar、纯库模块不要建此目录
- `src/test/java`：单元/集成测试
- `src/test/resources`：测试资源；若暂时没有测试类，git 不会跟踪空目录，有测试时再补 `java/` 即可

## `.cursor/rules/maven-multi-module.mdc`

以仓库中该文件为准。当前内容：

```mdc
---
description: Maven多模块目录、依赖方向与文档分层。新建模块、改代码、写文档时必须遵守。
alwaysApply: true
---

# Maven 多模块约束

人读约定见 `docs/多模块Maven项目目录结构规范.md`。改目录约定时，必须同时改该文档与本规则。

## 代码落点

- 业务代码只写在对应子模块 `src/main`。禁止在仓库根创建 `src/`。
- 测试只写在对应模块 `src/test`。不要建模块根 `tests/`，不要改 `testSourceDirectory`。
- `src/main/webapp` 仅当该模块 `packaging=war`。Spring Boot 可执行 jar 与库模块禁止建 `webapp/`。
- 改代码前确认模块边界，禁止把 A 模块实现写进 B 模块。
- 包名与模块对应，如 `com.xxx.common`、`com.xxx.service.a`。不要把多个模块的类塞进同一个根包。

## 依赖（DAG，不是 POM parent）

- 允许：`module-boot` → `module-service-*` → `module-common`。
- 禁止：模块环依赖；用 `<parent>` 表达业务父子；在库或 service 模块放 `@SpringBootApplication` 或 `spring-boot-maven-plugin`。
- 全仓库只有一个可启动模块（`module-boot`）：唯一 `main` 与 `@SpringBootApplication`。
- 兄弟模块依赖：版本在父 POM `dependencyManagement` 声明；子模块只写 `groupId` + `artifactId`，不写 `version`。
- 父 POM `packaging=pom`，只做 modules 列表、版本、`dependencyManagement`、`pluginManagement`。

## 新增模块清单

1. 新建模块目录与 `pom.xml`（`<relativePath>../pom.xml</relativePath>`）
2. 父 POM `<modules>` 增加节点
3. 若会被其他模块依赖，在父 POM `dependencyManagement` 登记
4. 写模块 `README.md`（职责、对外能力、启动）
5. 建模块 `docs/`
6. 文件名、模块名、XML 只使用 ASCII 连字符 `-`，不要用 `‑`（U+2011）

## 文档分层

- 根 `docs/`：跨 2+ 模块、整体需求/架构、跨模块 API、原始归档、全局 todo。
- 子模块 `docs/`：仅本模块的设计、私有表、内部接口、模块 todo。
- 模块 `README.md` 保持短入口，不写长篇设计。
- 单模块细节不要写进根 docs；跨模块变更必须更新根 docs。
- 实现 UI 时用 `@` 引用 `docs/original/` 下的具体页面，不要把整个 `original/` 塞进上下文。

## 脚本与 AI 配置

- 根 `scripts/`：全局构建/环境/发布。模块 `scripts/`：仅该模块。
- 只使用根目录 `.cursor/`。禁止在子模块创建 `.cursor/`。
- 提交：`rules/`、`agents/`、`skills/*/SKILL.md`、`mcp.json`。不提交：`cache/`、`sessions/`、`logs/`、`*.local`。
- Skill 必须是 `.cursor/skills/<name>/SKILL.md`，不要把 `SKILL.md` 直接放在 `skills/` 根下。
- `mcp.json` 禁止写密钥；用环境变量或根目录 `.env`（`.env` 不入库）。
```

## 根 README.md 片段

```markdown
# 项目名称
> Cursor AI + Maven 多模块开发项目

## 目录说明
- `.cursor/`：团队共享 AI 规则、Agent、Skill，已纳入 git，全局生效。不要在子模块新建 `.cursor`
- `docs/`：全局文档。跨模块需求、架构、跨模块接口、原始归档
- `scripts/`：项目全局构建脚本
- `module-*/`：各个 Maven 子模块
    - `README.md`：模块简介、对外能力
    - `docs/`：模块私有细节文档
    - `src/`：Maven 标准源码（`main` 业务，`test` 测试）
    - `scripts/`：模块私有脚本
- `module-boot/`：唯一可启动模块

## Cursor 开发规范
1. 业务代码写在对应子模块的 `src/main` 下。
2. 跨模块文档放根 `docs/`；单模块细节放该模块 `docs/`。
3. 新增模块必须更新父 POM 的 `<modules>`，并在 `dependencyManagement` 登记（若会被依赖）。
4. 禁止在 `mcp.json` 存放密钥。
5. 依赖方向：`module-boot` → `module-service-*` → `module-common`，禁止环依赖。
```

## 父 POM 最小模板

`Java` / `Spring Boot` 版本按团队实际修改。不要用 `spring-boot-starter-parent` 覆盖本父 POM；用 BOM import。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxx</groupId>
    <artifactId>project-parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <name>项目父 POM</name>

    <modules>
        <module>module-common</module>
        <module>module-service-a</module>
        <module>module-service-b</module>
        <module>module-boot</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.release>${java.version}</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-boot.version>3.4.5</spring-boot.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>module-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>module-service-a</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>${project.groupId}</groupId>
                <artifactId>module-service-b</artifactId>
                <version>${project.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <version>${spring-boot.version}</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
</project>
```

## 子模块 POM 最小模板（库 / service）

子模块继承 `groupId` 与 `version`，不要重复写。依赖兄弟模块时不写 `<version>`。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.xxx</groupId>
        <artifactId>project-parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>module-common</artifactId>
    <name>module-common</name>

    <dependencies>
        <!-- 其他模块依赖 common 时，在本文件不写 version -->
    </dependencies>
</project>
```

service 模块依赖 common 的写法：

```xml
<dependency>
    <groupId>${project.groupId}</groupId>
    <artifactId>module-common</artifactId>
</dependency>
```

## 可启动模块 `module-boot` POM 要点

```xml
<artifactId>module-boot</artifactId>
<dependencies>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>module-service-a</artifactId>
    </dependency>
    <dependency>
        <groupId>${project.groupId}</groupId>
        <artifactId>module-service-b</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

仅此模块配置 `spring-boot-maven-plugin`。需要传统 WAR 时再改 `packaging=war` 并增加 `src/main/webapp/`，不要作为默认。

## gitignore

根目录一份即可，子模块不要复制。规则：

1. 提交 `.cursor/rules`、`agents`、`skills`、`mcp.json`；只忽略 Cursor 本地缓存
2. 忽略 Maven 编译产物、IDE 文件、日志、临时文件
3. `mcp.json` 提交模板；密钥放 `.env`（忽略）。本地覆盖配置用 `*-local.yml` 等
4. 提交 Maven Wrapper（含 `.mvn/wrapper/maven-wrapper.jar`、`mvnw`）
5. 默认忽略 pdf/docx/xlsx；仅 `docs/original/` 及其子目录允许归档
6. 不要忽略 `.dockerignore`

完整内容以仓库根 `.gitignore` 为准。

```text
# ===================== Maven 编译输出 =====================
**/target/
**/pom.xml.tag
**/pom.xml.releaseBackup
**/pom.xml.versionsBackup
**/pom.xml.next
**/dependency-reduced-pom.xml
**/buildNumber.properties
**/.mvn/timing.properties
# maven-wrapper.jar 需要提交，保证无本机 Maven 时可用 mvnw 构建

# ===================== IDEA / IntelliJ =====================
.idea/
*.iml
*.iws
*.ipr
out/
**/out/
.idea_modules/

# ===================== VSCode / Cursor 本地缓存 =====================
# 提交：.cursor/rules、agents、skills、mcp.json
# 忽略：本地缓存与本机覆盖
.cursor/cache/
.cursor/sessions/
.cursor/logs/
.cursor/.temp/
.cursor/**/*.tmp
.cursor/**/*.local

.vscode/
*.code-workspace
.DS_Store

# ===================== 系统、临时文件 =====================
Thumbs.db
*.swp
*.swo
*~

# ===================== 环境变量、密钥、本地配置 =====================
# mcp.json 提交模板；密钥放 .env，绝不提交
.env
.env.local
.env.*.local
**/*.secret
**/*-local.yml
**/*-local.yaml
**/*-local.properties

# ===================== 日志 =====================
**/logs/
**/*.log
**/nohup.out

# ===================== 脚本生成临时产物 =====================
**/*.tmp
**/*.bak
**/dist/

# ===================== 归档、导出文件 =====================
# 默认忽略办公文档；仅 docs/original/ 及其子目录允许归档
*.pdf
*.docx
*.xlsx
!docs/original/**/*.pdf
!docs/original/**/*.docx
!docs/original/**/*.xlsx

# ===================== 测试报告 =====================
**/target/surefire-reports/
**/target/jacoco-report/
**/jacoco-report/

# ===================== 自定义本地脚本输出 =====================
**/scripts/output/
**/scripts/tmp/

```



## .cursorignore

二进制原稿可进 git，但不要进入 Agent 索引。原型 HTML 保留可检索，实现具体页面时再 `@` 引用。完整内容以仓库根 `.cursorignore` 为准。
