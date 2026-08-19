# Cursor AI 多模块Maven项目目录结构（已调整文档分层）

# Cursor AI 多模块 Maven 项目目录结构（已调整文档分层）

> 核心原则
>
> 1. `.cursor/` 在项目根，团队共享 AI 配置，纳入 git，全局作用所有子模块，子模块不再创建`.cursor`
> 2. **根* `docs/`*：存放跨模块、全局、整体级别的文档**
> 3. **每个子模块内部* `docs/`*：存放仅本模块关心的细节文档**；模块入口简介仍然保留模块根下 `README.md`
> 4. 根 `scripts/`：项目全局构建、环境、发布脚本；子模块 `scripts/`：模块私有脚本
> 5. AI 编码：业务代码只写在对应子模块 `src/`；测试放在对应模块测试目录
> 6. MCP 配置禁止写入密钥；`.cursor/**`全部提交版本控制
> 7. 子模块之间单向依赖，形成父子层级依赖。禁止相互依赖。

```Plain Text
项目根/
├─ .cursor/                          ✅【团队共享AI配置，git提交，全局生效】
│  ├─ rules/                         mdc编码约束、Vibe团队规则，全模块共用
│  ├─ mcp.json                       项目MCP配置（禁止存放密钥）
│  ├─ agents/                        团队自定义Agent定义
│  └─ skills/                        团队可复用Skill能力
├─ .gitignore                        
├─ docs/                             ✅【全局｜跨模块、整体项目文档】
│  ├─ original/                      原始输入：docx/pdf需求原始文件（归档）
│  ├─ requirements.md               项目整体业务需求
│  ├─ requirements_analysis.md      AI输出整体需求分析
│  ├─ tech_design.md                 全局整体技术架构方案
│  ├─ api_design.md                  跨模块公共接口、网关接口设计
│  └─ todo.md                        全局跨模块任务拆解清单
├─ scripts/                          ✅【项目全局脚本：构建、环境、发布脚本】
│  ├─ build‑all.sh
│  ├─ clean‑all.sh
│  └─ dev‑start.sh
├─ pom.xml                           ✅ Maven父POM，统一版本、依赖管理、modules列表
├─ README.md                         ✅【项目总说明，Cursor团队使用指引、模块总览】
│
├─ module‑common/                    子模块：公共基础模块（示例）
│  ├─ pom.xml                        继承父pom
│  ├─ README.md                      ✅【模块入口简介：模块职责、对外暴露能力、快速上手】
│  ├─ docs/                          ✅【模块私有｜仅本模块相关细节文档】
│  │  ├─ module‑design.md            模块内部详细设计
│  │  ├─ module‑db‑schema.md         模块数据库表结构
│  │  ├─ module‑api‑internal.md      模块内部接口
│  │  └─ module‑todo.md              模块内部任务清单
│  ├─ src/                           模块业务源码，Cursor AI仅在此产出本模块代码
│  │  ├─ main/java
│  │  └─ main/resources
│  ├─ tests/                         模块独立单元/集成测试
│  │  └─ java
│  └─ scripts/                       ✅【模块私有脚本：单元构建、mock、初始化数据】
│     └─ module‑build.sh
│
├─ module‑service‑a/                 业务模块A（示例）
│  ├─ pom.xml
│  ├─ README.md
│  ├─ docs/
│  │  ├─ module‑design.md
│  │  ├─ module‑db‑schema.md
│  │  └─ module‑todo.md
│  ├─ src/
│  │  ├─ main/java
│  │  └─ main/resources
│  ├─ tests/
│  └─ scripts/
│
├─ module‑service‑b/                 业务模块B（示例）
│  ├─ pom.xml
│  ├─ README.md
│  ├─ docs/
│  │  ├─ module‑design.md
│  │  └─ module‑todo.md
│  ├─ src/
│  │  ├─ main/java
│  │  └─ main/resources
│  ├─ tests/
│  └─ scripts/
│
└─ module‑gateway/                   网关模块（示例）
   ├─ pom.xml
   ├─ README.md
   ├─ docs/
   │  ├─ module‑design.md
   │  └─ route‑config.md
   ├─ src/
   │  ├─ main/java
   │  └─ main/resources
   ├─ tests/
   └─ scripts/
```



## 文档分层规则（写入cursor/rules/maven‑multi‑module‑rulesmdc）

> 文档归属判定标准，给 Cursor AI 执行

1. **放到根 ****`docs/`**
  - 涉及 2 个及以上模块；整体架构；全局业务需求；跨模块接口；项目级归档原始资料；全局任务。
2. **放到子模块 ****`xxx/docs/`**
  - 只和当前这一个模块有关；模块内部实现细节；模块私有表；模块内部接口；模块自己的待办。
3. **模块 ****`README.md`**：只写模块简介、边界、对外能力、快速启动，不写长篇详细设计。
4. 禁止子模块内新建 `.cursor` 文件夹，全部复用根目录 AI 规则配置。



## 补充说明：Maven 测试目录二选一

- 方案 A（当前结构，AI 友好）：模块顶层 `tests/`，需要在 pom 配置 `testSourceDirectory`；适合 Cursor 重度 AI 开发。
- 方案 B（原生 Maven 标准）：移除模块 `tests/`，统一使用 `src/test/java`，修改 cursor rules 识别 `src/test`。



### `.cursor/rules/maven‑multi‑module‑rules.mdc` 完整内容

```mdc
---
description: Maven多模块项目编码&文档分层约束
globs: ["**/*.java","**/*.xml","**/*.yml","**/*.md"]
---
# Maven多模块项目约束
## 代码
1. 业务代码必须放到对应maven子模块的src/main下面，禁止根目录新建src。
2. 单元测试代码放在所属模块测试目录，不要混入src/main。
3. 修改代码前确认模块边界，禁止随意跨模块写代码。
4. 新增模块：必须新增模块pom，父pom添加module节点，新增模块README.md与模块docs目录。
5. 脚本区分：全局脚本放在根scripts；模块私有脚本放在对应模块scripts。

## 文档分层规则
1. 根docs/：存放**跨模块、全局、项目整体**文档；整体需求、全局架构、跨模块接口、原始归档、全局todo。
2. 子模块/docs/：存放**仅本模块关心**的私有细节：模块内部设计、私有库表、内部接口、模块本地todo。
3. 模块README.md仅保留：模块职责、对外能力、快速上手，不写长篇详细设计。
4. 只改动单个模块的细节，不要污染根docs；如果改动会影响多个模块，更新根docs。

## AI配置
1. 所有AI规则统一使用根目录.cursor，子模块禁止创建.cursor目录。
2. mcp.json禁止填写密钥、密码等敏感信息。
```



### 根 READMEmdREADMEmd 片段（Cursor 团队指引）

```markdown
# 项目名称
> Cursor AI + Maven多模块开发项目

## 目录说明
- `.cursor/`：团队共享AI编码规则、Agent、Skill，已纳入git，全局生效，**不要在子模块新建.cursor**
- `docs/`：【全局文档】跨模块整体需求、架构、跨模块接口、原始归档
- `scripts/`：项目全局构建脚本
- `module‑*/`：各个Maven子模块
    - `README.md`：模块简介、对外能力
    - `docs/`：模块私有细节文档，仅本模块使用
    - `src/`：业务源码
    - `tests/`：测试代码
    - `scripts/`：模块私有脚本

## Cursor开发规范
1. AI生成业务代码，写在对应子模块的src/main下。
2. 文档判断：跨模块放根docs；单模块细节放模块内部docs。
3. 新增模块必须更新父pom的modules列表。
4. 禁止在mcp.json存放密钥、密码等敏感配置。
```



### 父 pomxml 最小模板

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema‑instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven‑4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.xxx</groupId>
    <artifactId>project‑parent</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    <name>项目父POM</name>

    <modules>
        <module>module‑common</module>
        <module>module‑service‑a</module>
        <module>module‑service‑b</module>
        <module>module‑gateway</module>
    </modules>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF‑8</project.build.sourceEncoding>
    </properties>

    <dependencyManagement>
        <!--统一版本管理，子模块引用不写version-->
    </dependencyManagement>
</project>
```



### 子模块 pomxml 最小模板（tests 目录自定义版）

```xml
<?xml version="1.0" encoding="UTF‑8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema‑instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven‑4.0.0.xsd">
    <parent>
        <groupId>com.xxx</groupId>
        <artifactId>project‑parent</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>module‑common</artifactId>
    <name>module‑common</name>

    <!-- 自定义顶层tests目录，适配Cursor AI识别 -->
    <build>
        <testSourceDirectory>${project.basedir}/tests/java</testSourceDirectory>
    </build>

    <dependencies>
        <!--子模块依赖-->
    </dependencies>
</project>
```



## gitignore 完整适配本 Maven  Cursor AI 多模块项目

> 规则说明：
>
> 1. `.cursor/`** 全部保留提交，不忽略**，团队共享 AI 规则、agents、skills；仅忽略 cursor 本地缓存
> 2. Maven 编译产物、IDE 文件、日志、临时文件全部忽略
> 3. 保护密钥：禁止提交密钥，`mcp.json` 提交模板，密钥放环境变量 / `.env`（`.env` 忽略）
> 4. 子模块通用，无需每个模块复制，根目录一份即可

```gitignore
# ===================== Maven 编译输出 =====================
**/target/
**/pom.xml.tag
**/pom.xml.releaseBackup
**/pom.xml.versionsBackup
**/pom.xml.next
**/dependency-reduced-pom.xml
**/buildNumber.properties
**/.mvn/timing.properties
**/.mvn/wrapper/maven-wrapper.jar

# ===================== IDEA / IntelliJ =====================
.idea/
*.iml
*.iws
*.ipr
out/
**/out/
.idea_modules/

# ===================== VSCode / Cursor 本地缓存【重要】 =====================
# ✅ .cursor/rule,agents,skills,mcp.json 要提交！只忽略本地缓存
.cursor/cache/
.cursor/sessions/
.cursor/logs/
.cursor/.temp/**
.cursor/**/*.tmp
.cursor/**/*.local

.vscode/
*.code‑workspace
.DS_Store

# ===================== 系统、临时文件 =====================
.DS_Store
Thumbs.db
*.swp
*.swo
*~

# ===================== 环境变量、密钥、本地配置 =====================
# mcp.json 提交模板，密钥放到 .env，.env绝不提交
.env
.env.local
.env.*.local
**/*.secret
**/*‑local.yml
**/*‑local.yaml
**/*‑local.properties

# ===================== 日志 =====================
**/logs/
**/*.log
**/nohup.out

# ===================== 脚本生成临时产物 =====================
**/*.tmp
**/*.bak
**/dist/
**/build/

# ===================== 归档、导出文件 =====================
*.pdf
*.docx
*.xlsx
!docs/original/*.pdf
!docs/original/*.docx

# ===================== 测试报告 =====================
**/surefire‑reports/
**/jacoco‑report/

# ===================== Docker 相关 =====================
.dockerignore
*.docker

# ===================== 自定义本地脚本输出 =====================
**/scripts/output/
**/scripts/tmp/
```

