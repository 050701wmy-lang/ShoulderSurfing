# Better Shoulder Surfing：Fabric 26.2 集成方案

## 基线与分支

- 上游：`https://github.com/Exopandora/ShoulderSurfing.git`，本地 remote 名为 `upstream`。
- 工作分支：`integration/fabric-26.2`，从上游标签 `26.2-5.1.1`（`217b7a4a`）创建。该标签已经使用 Minecraft 26.2、Fabric Loader 0.19.3、Fabric API 0.152.1+26.2 和 Java 25。
- `master` 跟踪 `upstream/master`，用于观察上游更新。后续在工作分支上按需合并或挑选上游提交；跨 Minecraft 版本的更新先审查 API 变更，不能直接把 26.3 构建配置覆盖到 26.2 分支。
- 现有 GitHub fork：`https://github.com/050701wmy-lang/ShoulderSurfing`，本地 remote 名为 `origin`。工作分支推送到 `origin/integration/fabric-26.2`。

## 合并原则

以 Shoulder Surfing 为唯一视角与相机状态所有者。其余项目只提供下表列出的目标行为，不同时加载各自的相机 mixin。新增逻辑尽量使用现有事件 API；距离调整与过渡需要进入相机链时，集中在一处处理。

所有新增能力先在 Fabric 26.2 实现。`common` 中只放可跨加载器复用的状态与算法；Fabric 专属按键、事件及 Wynncraft 检测留在 `fabric`。非 Fabric 构建不因首期工作改变。

| 来源 | 目标功能 | 接入位置 | 冲突处理 |
| --- | --- | --- | --- |
| Shoulder Surfing | 肩视角及现有相机行为 | 保留上游实现 | 作为唯一视角与相机基底 |
| Camera Utils | Adjustable Third Person Distance：按住修饰键并滚轮调节第三人称相机距离 | Fabric 输入、原有肩视角距离配置及原版第三人称碰撞计算 | 不移植其相机 mixin |
| Nimble | 平滑视角过渡；骑乘和鞘翅时自动第三人称 | 统一自动视角控制器及过渡插值 | 骑乘只保留一套进入、退出和恢复逻辑 |
| Don't Surf Through Cutscenes | 过场、死亡时临时第一人称，结束后恢复 | 统一自动视角控制器；肩视角优先接入现有临时第一人称事件 | 不让逐帧强制视角覆盖用户手动选择 |
| Nimble ReWynnded | Wynncraft 过场识别、骑乘恢复 | 统一自动视角控制器的识别规则 | Wynncraft 的骑乘规则并入通用骑乘逻辑，不另建状态机 |

明确不做：动态 FOV 缩放、相机固定、双相机预设、电影模式、按住前视、鞘翅画面倾斜。

## 视角状态机

用户显式选择的视角记录为 `requestedPerspective`。自动规则只产生临时的 `effectivePerspective`，规则消失后回到用户选择。优先级从高到低：过场/死亡临时第一人称、骑乘或鞘翅自动第三人称、用户选择。退出世界、换角色或断线时清空临时状态。若玩家在自动视角期间手动切换，则更新 `requestedPerspective`，退出自动状态时尊重这一新选择。

Wynncraft 过场识别与通用过场规则共用同一临时第一人称状态。现有两款 Wynncraft 项目主要通过游戏模式进入 `SPECTATOR` 识别过场；它与普通服务器观战模式可能无法仅凭客户端信息区分，因此只在可识别 Wynncraft 的连接中启用这条规则，其他过场触发条件经实测再加入。死亡以本地玩家死亡状态为触发。两种来源同时出现时只进入一次临时状态，全部结束后才恢复。

相机计算顺序：确定有效视角 → 读取统一第三人称距离 → 肩视角使用原有偏移与碰撞修正，原版后/前第三人称在碰撞检测前调整目标距离 → 平滑过渡 → 渲染。按住 `右 Ctrl` 并滚轮调整距离，范围 1–100 格；第一人称不响应。

## 实施顺序与验收

1. **26.2 基线**：从 `26.2-5.1.1` 建分支；运行 `:fabric:build` 与 `:fabric:runClient`；确认在 Fabric 26.2 下已有肩视角、自由视角、准星可用。
2. **自动视角**：实现共用的骑乘/鞘翅第三人称及恢复、死亡临时第一人称、Wynncraft 观战模式过场识别。验证第一人称、肩视角、原版后/前第三人称各自可恢复；手动切换与切换世界不会恢复过期视角。
3. **第三人称距离**：加入按住修饰键滚轮调距，验证三种第三人称视角、1–100 格边界、靠墙碰撞、第一人称及按键冲突。
4. **平滑过渡**：在同一相机链中给手动和自动视角切换加入位置插值；检查低帧率、墙体碰撞、人物透明与准星。
5. **发布前验证**：同一个 jar 中只声明一个模组 ID；Fabric 26.2 客户端进入单人和 Wynncraft，测试过场、死亡、骑乘、鞘翅、第三人称距离与视角恢复；保留原始项目署名与适用的许可证声明。

## 源码与许可证

上游 Shoulder Surfing 为 MIT。Nimble 和 Nimble ReWynnded 为 MIT，移植其代码时保留版权与许可声明。Don't Surf Through Cutscenes 为 LGPL-3.0；首期按行为独立实现，不直接复制其源码。Camera Utils 的 `fabric.mod.json` 声明 `All Rights Reserved`，不复制其源码、资源或 UI；只依据公开功能描述独立实现。发布前再次核对所有迁入文件的授权与署名。

## 同步上游

```powershell
git fetch upstream --tags
git switch integration/fabric-26.2
git log --oneline integration/fabric-26.2..upstream/master
```

先查看候选提交是否改动 `api/`、`common/`、`fabric/` 或构建版本。适用于 26.2 的修复可 `git cherry-pick <sha>`；整批合并前需要检查版本目录和 Fabric 元数据，完成构建与客户端验证后再推送。

## 当前验证状态

- 已修正 Gradle 9.5.1 配置阶段的仓库声明冲突：`settings.gradle.kts` 中的插件仓库改用普通 `content` 过滤。
- 已实现第三人称距离调整：按住 `右 Ctrl` 并滚轮调整肩视角及原版后/前第三人称距离，设为 1–100 格。肩视角复用现有距离配置和碰撞处理；原版视角在原生碰撞检测前增加相对于默认 4 格的距离差。旧配置若把距离上限保留在 5 格，可在设置中手动调高；滚轮操作本身仍限制在 100 格。
- 已实现统一自动视角：骑乘或鞘翅飞行自动进入肩视角；死亡或在 `wynncraft.com` 域名的服务器进入观战模式时临时进入第一人称。原因解除后恢复进入前的视角；玩家手动切换优先。Wynncraft 的非观战模式过场目前无法可靠识别，需游戏内实测补充触发条件。
- 已实现 250 毫秒视角位置过渡。它在 Shoulder Surfing 原有相机与碰撞结果之后插值；尚未在复杂墙体场景验证。
- 距离功能改动后 `:fabric:build` 已通过，开发客户端也启动到资源加载完成，日志未出现新增 mixin 报错。构建产物为 `BetterShoulderSurfing-Fabric-26.2-5.1.1-bss.2.jar`。Minecraft 开发身份的 Realms 认证报错与本地模组加载无关。
- 保留 `shouldersurfing` 模组 ID 以复用现有配置与资源；发布项目 ID 已置为 `unset`，避免使用上游 CurseForge/Modrinth 项目。新项目发布前再配置自己的 ID。
- 尚未完成游戏内操作及 Wynncraft 实服验证。`api.modrinth.com` 对 Gradle 的 TLS 握手有时失败；本机 Maven 缓存已有 Cobblemon 的两个仅用于编译的工件，构建脚本可从 `mavenLocal()` 读取。
