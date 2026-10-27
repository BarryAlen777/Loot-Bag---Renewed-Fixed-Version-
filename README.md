# Loot Bag - Renewed (Fixed Version) 4.6.0

Minecraft 1.20.1 / Forge（`javafml`，构建于 47.4.0，依赖范围放宽到 `[47,)`）。

这是原版 **Loot Bag Renewed 4.5.1**（作者 Trhod177s）的修复分支，modId 保持 `lootbagmod` 不变，
所以旧存档里已有的战利品包和 `config/lootbagmod-common.toml` 都还能继续用。

- **本分支版本：4.6.0**（对应原版 4.5.1）
- **分支维护者：BarryAlen777**
- **维护仓库：<https://github.com/BarryAlen777/Loot-Bag---Renewed-Fixed-Version->**

## 这个版本修了什么

| 问题 | 表现 |
|---|---|
| 生存模式不消耗包 | `ItemStack.equals` 是引用比较，旧的判断永远为 false，包可以无限开 |
| 事件监听注册两次 | 掉率与宝箱注入概率翻倍（宝箱注入第二次还会抛异常），日志/聊天重复两遍 |
| `use()` 没判断客户端 | 客户端也跑一遍开包，产生幽灵掉落物、背包闪一下 |
| `getFreeSlot() <= 0` | 0 号槽被当成"背包满"，空背包开包东西全撒地上 |
| `addItem` 返回值被忽略 | 背包装不下的余量直接消失 |
| 空列表 / 写错物品 ID | 异常被吞掉，包照样扣，玩家什么也没拿到 |
| 配置热重载 | 列表不重建，运行时切换白名单会"吃包" |
| `allNonHostileMobs` | 玩家、盔甲架也算"非敌对"，PVP 击杀会掉包；村民/傀儡同开关下会被刷（现由 `excludevillagersandgolems` 挡掉） |
| 掠夺等级相乘 | 别的模组给出夸张的掠夺等级时 `chance * looting` 会 int 溢出成负数，掉包直接失效（改为 long 后截断） |
| 空配置时的空挥 | 服务端拒绝开包时客户端只是挥了一下没有任何提示，现在会在动作栏说明原因 |
| 默认黑名单 | `minecraft:spawn_egg`、`minecraft:mob_spawner` 在 1.20.1 不存在，刷怪蛋根本没被挡住 |
| 死资源 | `buried_treasure.json` / `end_city_treasure.json` 没接进代码；`lootbagdrops.json` 无人引用 |
| 创造模式物品栏 | 物品不在任何 Tab 里，只能 `/give` |

## 许可证与署名

原始模组与本分支均以 **GNU LGPL-3.0-or-later** 授权，许可证全文见 [LICENSE](LICENSE)。

本仓库是原版的**修改版**（LGPL-3.0 第 4 条要求注明改动）：相对原版 4.5.1 的全部改动就是上表，
jar 内 `META-INF/README.md` 带着同一张表，不依赖 git 提交记录做说明；
修改由本分支维护者 **BarryAlen777**（<https://github.com/BarryAlen777/Loot-Bag---Renewed-Fixed-Version->）完成，
原始版权与作者署名（Trhod177s）在 `src/main/resources/META-INF/mods.toml` 的 `authors` 中保留。

## 数值与默认行为（和旧版不一样）

事件监听以前注册了两遍，修掉之后每个概率只算一次，回到配置文件本来的量：

- 怪物掉包：默认 **10% / 击杀**（旧版实际约 20.8%），掠夺附魔按等级翻倍；
- 宝箱注入：单个结构 **23.7%**（旧版因为加了两个池，约 41.8%）；
- `allNonHostileMobs=true` 时，村民、流浪商人、铁傀儡、雪傀儡默认**不掉包**
  （新增配置 `excludevillagersandgolems`，想恢复旧行为改成 `false`；
  写在 `mobdropslist` 里点名的生物不受这条限制）；
- 玩家、盔甲架在任何配置下都不会掉包。

## 新增的配置项

`config/lootbagmod-common.toml` 里多了两个配置，旧的配置文件依旧可以读取：

| 键 | 默认 | 作用 |
|---|---|---|
| `excludeadminitems` | `true` | 硬挡刷怪蛋、命令方块、刷怪笼、屏障、基岩等管理用物品，与黑名单无关 |
| `excludevillagersandgolems` | `true` | 上面说的村民/傀儡排除 |


## 安装

把 jar 放进 `.minecraft/mods`。**先删掉任何同名的旧 jar**：modId 相同会导致 Forge 报重复模组冲突。

## 反馈

有问题、想要新配置项，或者发现某个生物不该掉包：到维护仓库提 issue —
<https://github.com/BarryAlen777/Loot-Bag---Renewed-Fixed-Version->（提的时候把崩溃报告
`crash-reports/` 里最新的 `crash-*.txt` 或 `logs/latest.log` 一起附上）。

## 版本历史

| 版本 | 内容 |
|---|---|
| **4.6.0** | 按复审报告修完全部残留项：村民/铁傀儡/雪傀儡/流浪商人不再被 `allNonHostileMobs` 刷包（新增 `excludevillagersandgolems`，默认开启）；掠夺等级相乘不再 int 溢出；配置池为空时开包会在动作栏给出提示而不是空挥；删掉永远走不到的冗余判断；署名与改动说明不再依赖 git 提交记录；标注分支维护者与仓库地址 |
| 4.5.1 | 修掉生存模式不消耗包、事件监听重复注册、客户端重复开包、空背包撒地上、余量丢失、吞异常吃包、配置热重载、默认黑名单写错 ID 等问题 |
| 原版 4.5.1 | Trhod177s 的 Loot Bag |
