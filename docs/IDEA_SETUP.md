# IDEA 项目配置指南

## 问题：Command+点击无法跳转到源码

**原因**：IDEA 不知道 `src` 是源码目录

---

## 🔧 解决方案

### 方法 1：自动配置（推荐）

我已经为你修改了配置文件，现在需要：

1. **关闭 IDEA**（如果正在运行）
2. **重新打开项目**
   ```
   File → Open → 选择 /Users/unravel/fundFlow-manager
   ```
3. **等待 IDEA 索引完成**（右下角会显示进度）
4. **测试跳转**：Command+点击 `PrintUtil` 应该能跳转了

---

### 方法 2：手动配置（如果方法1不行）

#### 步骤 1：标记源码目录

1. 打开 IDEA
2. 在左侧项目树中，**右键点击 `src` 文件夹**
3. 选择 **Mark Directory as → Sources Root（将目录标记为 → 源代码根目录）**
4. `src` 文件夹图标会变成蓝色（表示源码目录）

#### 步骤 2：添加 MySQL 驱动依赖

1. 打开 **File → Project Structure**（或按 `Cmd + ;`）
2. 左侧选择 **Libraries**
3. 点击 **+** → **Java**
4. 选择项目根目录下的 `mysql-connector-java-8.0.33.jar`
5. 点击 **OK**

#### 步骤 3：验证配置

1. 打开 `Step3_UserDaoTest.java`
2. **Command+点击** `PrintUtil`
3. 应该能跳转到 `PrintUtil.java` 源码

---

## 🎯 配置完成后的效果

✅ **Command+点击** 类名 → 跳转到源码  
✅ **自动补全** 正常工作  
✅ **代码提示** 正常显示  
✅ **错误检查** 实时显示  
✅ **重构功能** 可以使用（重命名、提取方法等）

---

## 📁 项目结构说明

```
fundFlow-manager/
├── .idea/                    # IDEA 配置文件（已修复）
│   ├── fundFlow-manager.iml  # 模块配置 ✅
│   ├── misc.xml              # 项目设置
│   └── modules.xml           # 模块列表
│
├── src/                      # 源码目录（标记为 Sources Root）
│   └── moneyTransfer/
│       ├── entity/          # 实体类
│       ├── dao/             # 数据访问层
│       ├── util/            # 工具类 ← PrintUtil 在这里
│       └── Step*.java       # 测试程序
│
├── moneyTransfer/            # 编译输出目录（应该被排除）
│   └── *.class              # 编译后的字节码
│
└── mysql-connector-java-8.0.33.jar  # MySQL 驱动（已添加到依赖）
```

---

## ❓ 常见问题

### Q1: 重新打开 IDEA 后还是不行？
**A:** 尝试以下操作：
1. **File → Invalidate Caches / Restart**
2. 选择 **Invalidate and Restart**
3. 等待 IDEA 重新索引

### Q2: 提示找不到 Java SDK？
**A:** 配置 Java SDK：
1. **File → Project Structure → Project**
2. **Project SDK** 选择你安装的 JDK（建议 JDK 8 或更高）
3. 如果没有，点击 **New → JDK**，选择 JDK 安装路径

### Q3: 代码有红色波浪线错误？
**A:** 检查：
1. 是否添加了 MySQL 驱动依赖
2. 是否将 `src` 标记为 Sources Root
3. 尝试 **Build → Rebuild Project**

### Q4: 为什么会有两个 moneyTransfer 目录？
**A:** 
- `src/moneyTransfer/` = 源代码（你编写的 .java 文件）
- `moneyTransfer/` = 编译输出（javac 生成的 .class 文件）
- 应该将 `moneyTransfer/` 添加到 `.gitignore`（已添加）

---

## 🎓 IDEA 快捷键（macOS）

| 快捷键 | 功能 |
|--------|------|
| `Cmd + 点击` | 跳转到定义 |
| `Cmd + B` | 跳转到定义（同上） |
| `Cmd + [` | 返回上一个位置 |
| `Cmd + ]` | 前进到下一个位置 |
| `Cmd + Shift + F` | 全局搜索 |
| `Cmd + O` | 查找类 |
| `Cmd + Shift + O` | 查找文件 |
| `Cmd + N` | 生成代码（构造函数、getter/setter） |
| `Cmd + /` | 注释/取消注释 |
| `Cmd + Shift + A` | 查找操作 |

---

## ✅ 验证配置是否成功

打开 `Step3_UserDaoTest.java`，尝试：

1. ✅ **Command+点击** `PrintUtil` → 应该跳转到源码
2. ✅ 输入 `PrintUtil.` → 应该显示方法列表
3. ✅ 输入 `new User()` → 应该有自动补全
4. ✅ 编译项目 → 应该没有错误

---

**配置完成后，记得告诉我结果！** 😊

如果还有问题，截图给我看，我帮你排查。
