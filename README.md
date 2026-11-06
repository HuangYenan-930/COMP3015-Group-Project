# Battle Joker 团队开发仓库

COMP3015 三人项目。目标：V3、Multicast Scoreboard、Saving and Uploading Puzzle。
当前提交是课堂代码的跨平台开发基线，尚未完成上述全部作业功能。

## 第一次打开

1. 安装 **JDK 25**（Mac 选对应 Apple Silicon/Intel 架构，Windows 选对应架构），确保 `java -version` 显示 25。项目也可使用较新 JDK 编译，但团队和 CI 统一使用 25。
2. 用 IntelliJ 打开整个仓库的根目录或根 `pom.xml`，选择加载 Maven 项目。
3. Project SDK 和 Maven Runner 的 JRE 都选 JDK 25。
4. 等待依赖下载。**不需要单独安装 JavaFX SDK，也不要复制其他人的 `.idea`、`.iml` 或 JavaFX SDK 文件夹。** Maven 根据运行电脑下载对应平台的 JavaFX 本地库。
5. Windows、Mac 使用相同源码。首次运行需要联网，仓库自带 Maven Wrapper，无需另装 Maven。

## 构建

所有命令都从仓库根目录执行。

Mac/Linux：

```sh
./mvnw --batch-mode verify
```

Windows PowerShell：

```powershell
.\mvnw.cmd --batch-mode verify
```

构建结果位于各模块 `target/`；FXML 和卡牌 PNG 会自动打包到客户端 classpath。

## 启动

先在一个终端启动服务器，并保持运行。

Mac：

```sh
./mvnw -pl Jokerserver compile exec:java
```

Windows PowerShell：

```powershell
.\mvnw.cmd -pl Jokerserver compile exec:java
```

再开一个终端启动游戏客户端。

Mac：

```sh
./mvnw -pl BattleJoker compile javafx:run
```

Windows PowerShell：

```powershell
.\mvnw.cmd -pl BattleJoker compile javafx:run
```

**不用单独运行 `Client.main()`**，那是发送 Hello 的通信测试入口。
IntelliJ 可在 Maven 工具窗口按模块执行相同 goal；JavaFX 游戏应通过 `javafx:run` 启动，避免 Current File 缺少模块参数。

## 多台电脑连接

默认连接 `127.0.0.1:15556`，只适用于服务器和客户端在同一台电脑。
同一局域网的另一台电脑需使用服务器的局域网 IPv4 地址，例如：

Mac：

```sh
./mvnw -pl BattleJoker -Dserver.host=192.168.1.10 -Dserver.port=15556 compile javafx:run
```

Windows PowerShell（将 -D 参数放入引号，避免 PowerShell 解析问题）：

```powershell
.\mvnw.cmd -pl BattleJoker "-Dserver.host=192.168.1.10" "-Dserver.port=15556" compile javafx:run
```

地址应替换为服务器的实际 IP，两台电脑必须能相互访问，服务器防火墙需允许 Java 的入站连接。
更换服务器监听端口：

```sh
./mvnw -pl Jokerserver -Dserver.port=15557 compile exec:java
```

Windows 用 `.\mvnw.cmd`，并将 `"-Dserver.port=15557"` 放入引号。
当前仍是课堂版的姓名输入框；IP/端口对话框由后续 V1 工作补齐，以上参数方便现在跨电脑联调。

## 数据和资源

- 卡牌图片从 classpath 加载，不再依赖 IDE 工作目录。
- 本地分数数据库首次连接时自动创建目录和 scores 表，数据库不上传 Git。
- 默认数据目录为启动进程工作目录下的 `data/`；Maven goal 的实际工作目录可因启动方式而不同，每个人的运行数据独立保存。
- 当前数据库逻辑仍在客户端，这是课堂基线；V1 负责人后续迁移至服务器。首次运行榜单为空是正常的，不包含其他人的历史成绩。
- 本次适配统一使用 Maven Central 提供的 JavaFX 25.0.1；桌面旧项目自带的 SDK 不再用于本仓库构建。

## 三人协作

每人使用自己的本地 clone。以后从这个仓库开发，避免同时修改桌面旧副本并误以为会自动同步。

1. 开始前更新 main：

```sh
git switch main
git pull --ff-only
```

2. 新建自己的分支，例如 `feature/v1`、`feature/v3`、`feature/scoreboard`：

```sh
git switch -c feature/v1
```

3. 修改、构建通过后提交：

```sh
git add .
git commit -m "Complete player score synchronization"
git push -u origin feature/v1
```

4. 在 GitHub 创建 Pull Request，让队友看过后合并到 main。
5. 接手下一阶段前切回 main 并 pull；不需要再传 ZIP。不要使用 force push 覆盖队友工作。

私有仓库需要由所有者在 GitHub 的 Settings → Collaborators 邀请另外两位成员，受邀者接受后才能 clone/push。

## 自动构建

`.github/workflows/build.yml` 在每次 push、PR 时分别使用 macOS 和 Windows、JDK 25 运行 Maven verify。
构建通过代表编译和资源打包成功，不等于完成图形界面、局域网、组播或 V3 功能验收；这些仍需要多电脑演示测试。

## 模块和后续任务

- `Jokerserver/src/`：服务器、ServerEngine、Player。
- `BattleJoker/src/`：客户端 Java 和 FXML。
- `BattleJoker/images/`：共享卡牌资源。

后续仍需完成：V1 的服务器数据库/本局结算/状态同步和完整结束判断；V3 的房间与回合；独立组播榜单；保存和上传棋盘。
课堂版尚有 TCP 长度处理、断线遍历删除等待完善逻辑。本次仅适配开发和构建，不修改游戏规则或宣称完成作业。
