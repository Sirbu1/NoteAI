# 安装 Android SDK Platform 34 的详细步骤

## 问题原因
错误 "Could not find compile target android-34" 表示你的电脑上没有安装 Android SDK Platform 34。

## 解决方案：在 Android Studio 中安装 SDK

### 方法一：通过 SDK Manager 安装（推荐）

#### 步骤 1：打开 SDK Manager

1. 在 Android Studio 中，点击菜单：`File` → `Settings`（Windows）或 `Android Studio` → `Preferences`（Mac）
2. 在左侧菜单中，选择：`Appearance & Behavior` → `System Settings` → `Android SDK`

#### 步骤 2：安装 Android SDK Platform 34

1. 在 "SDK Platforms" 标签页中，找到 **"Android 14.0 (API 34)"**
2. **勾选** "Android 14.0 (API 34)" 前面的复选框
3. 如果看到子选项，确保勾选：
   - ✅ Android SDK Platform 34
   - ✅ Sources for Android 34（可选，但推荐）

#### 步骤 3：安装 Build Tools

1. 切换到 "SDK Tools" 标签页
2. 确保勾选：
   - ✅ Android SDK Build-Tools
   - ✅ Android SDK Platform-Tools
   - ✅ Android SDK Command-line Tools

#### 步骤 4：应用更改

1. 点击右下角的 **"Apply"** 按钮
2. 会弹出确认对话框，点击 **"OK"**
3. 等待下载和安装完成（可能需要几分钟到十几分钟，取决于网络速度）

#### 步骤 5：验证安装

1. 安装完成后，在 "SDK Platforms" 标签页中，确认 "Android 14.0 (API 34)" 前面有绿色的勾选标记
2. 点击 **"OK"** 关闭设置窗口

#### 步骤 6：同步项目

1. 点击菜单：`File` → `Sync Project with Gradle Files`
2. 等待同步完成

---

### 方法二：通过 Tools 菜单快速打开

1. 在 Android Studio 顶部菜单栏，点击：`Tools` → `SDK Manager`
2. 按照上面的步骤 2-6 操作

---

## 如果下载很慢或失败

### 使用国内镜像（可选）

如果下载速度很慢，可以配置代理或使用国内镜像：

1. 在 SDK Manager 中，点击 "SDK Update Sites" 标签
2. 可以添加国内镜像源（如阿里云镜像）

或者：

1. 在 `gradle.properties` 中配置代理（如果你有代理的话）

---

## 验证修复

安装完成后：

1. **同步项目**：`File` → `Sync Project with Gradle Files`
2. **构建项目**：`Build` → `Rebuild Project`
3. 如果看到 "BUILD SUCCESSFUL"，说明修复成功！

---

## 常见问题

### Q: 下载失败怎么办？
A: 
- 检查网络连接
- 尝试重新下载
- 如果使用代理，检查代理设置

### Q: 需要安装哪些版本？
A: 
- **必须安装**：Android SDK Platform 34
- **推荐安装**：Android SDK Build-Tools（最新版本）
- **可选**：其他 API 版本（如 33, 32 等）

### Q: 安装需要多长时间？
A: 
- 取决于网络速度
- 通常需要 5-15 分钟
- 首次安装可能需要更长时间

---

## 快速检查清单

- [ ] 打开 SDK Manager
- [ ] 勾选 "Android 14.0 (API 34)"
- [ ] 点击 "Apply" 安装
- [ ] 等待安装完成
- [ ] 同步项目
- [ ] 重新构建项目

完成这些步骤后，错误应该就解决了！

