# Better Shoulder Surfing：Fabric 26.2 集成方案

## 基线与分支

- 上游：`https://github.com/Exopandora/ShoulderSurfing.git`，本地 remote 名为 `upstream`。
- 工作分支：`integration/fabric-26.2`，从上游标签 `26.2-5.1.1`（`217b7a4a`）创建。该标签已经使用 Minecraft 26.2、Fabric Loader 0.19.3、Fabric API 0.152.1+26.2 和 Java 25。
- `master` 跟踪 `upstream/master`，用于观察上游更新。后续在工作分支上按需合并或挑选上游提交；跨 Minecraft 版本的更新先审查 API 变更，不能直接把 26.3 构建配置覆盖到 26.2 分支。
- 现有 GitHub fork：`https://github.com/050701wmy-lang/ShoulderSurfing`，本地 remote 名为 `origin`。工作分支推送到 `origin/integration/fabric-26.2`。

## 合并原则

以 Shoulder Surfing 为唯一视角与相机状态所有者。其他模组的功能按用户可见行为迁入，而非同时加载四套原有相机 mixin。独立功能通过 Fabric 客户端入口与上游事件 API 实现；确需改变相机渲染路径时，只在已有 Shoulder Surfing mixin 中增加受控分支。

所有新增能力先在 Fabric 26.2 实现。`common` 中只放可跨加载器复用的状态与算法；Fabric 专属按键、事件及 Wynncraft 检测留在 `fabric`。非 Fabric 构建不因首期工作改变。

| 来源 | 目标功能 | 接入位置 | 冲突处理 |
| --- | --- | --- | --- |
| Shoulder Surfing | 肩视角、自由视角、偏移、准星、人物透明、现有配置 | 保留上游实现 | 作为状态及配置基底 |
| Camera Utils | 动态缩放、相机固定、距离滚轮、两个相机预设、电影模式 | 新的独立 Fabric 输入及相机控制层 | 缩放只改变最终 FOV；距离/预设写入 Shoulder Surfing 偏移；固定相机与玩家朝向分离；电影模式使用同一平滑链 |
| Nimble | 平滑视角过渡、骑乘及鞘翅自动第三人称、前视、鞘翅倾斜 | 统一视角状态机及渲染插值 | 只计算一个最终目标视角；避免 Nimble 原有 `Options`/`Camera` mixin 与基底重复 |
| Don't Surf Through Cutscenes | 过场和死亡时临时第一人称并恢复 | `ComputeTemporaryFirstPersonStateEvent` 加少量生命周期状态 | 不覆盖用户在过场期间主动选择的新视角 |
| Nimble ReWynnded | Wynncraft 过场识别、骑乘恢复、按住前视 | 统一视角状态机的 Wynncraft 策略 | 与通用骑乘/过场只保留一份状态，不注册第二套 F5 处理器 |

## 视角状态机

用户显式选择的视角记录为 `requestedPerspective`。自动规则只产生临时的 `effectivePerspective`，规则消失后回到用户选择。优先级从高到低：服务器过场/死亡、用户临时按住前视、骑乘或鞘翅自动视角、用户选择。退出世界、换角色或断线时清空临时状态。若玩家在自动视角期间手动切换，则更新 `requestedPerspective`，不在退出自动状态时恢复旧值。

相机计算顺序：选择有效视角 → 选用预设偏移及距离 → 肩视角碰撞修正 → 平滑插值 → 缩放 FOV → 渲染。固定相机模式仅覆盖相机位置，不跳过碰撞与射线逻辑的安全检查。缩放、距离与预设不能各自覆盖最后的相机位置。

## 实施顺序与验收

1. **26.2 基线**：从 `26.2-5.1.1` 建分支；运行 `:fabric:build` 与 `:fabric:runClient`；确认在 Fabric 26.2 下已有肩视角、自由视角、准星可用。
2. **自动视角**：实现过场/死亡及骑乘恢复，再加入按住前视与鞘翅切换。验证第一人称、肩视角、原版后/前第三人称各自可恢复；切换世界不会继承旧状态。
3. **相机工具**：先做缩放、滚轮距离和两个偏移预设；再做固定相机与电影平滑。每项都使用独立可关闭的配置，检查和原有快捷键是否冲突。
4. **转场与鞘翅**：在统一相机链上加入插值和倾斜，检查低帧率、碰撞、透视及准星行为。
5. **发布前验证**：同一个 jar 中只声明一个模组 ID；Fabric 26.2 客户端进入单人和 Wynncraft，测试过场、死亡、骑乘、鞘翅、缩放与视角按键；保留原始项目署名与适用的许可证声明。

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
- `:fabric:build` 已进入 Fabric 项目配置，但依赖解析时 `api.modrinth.com` 的 TLS 握手中断，未到 Java 编译阶段。`common` 和 `fabric` 的 Cobblemon 编译依赖都使用这个 Maven 服务；在网络恢复前不能宣称 jar 已通过构建。
- 尚未执行游戏客户端验证，五个来源的新增功能也尚未实现。此分支目前是可同步上游的 MC 26.2 基底与实施方案。
