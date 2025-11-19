# 彻底解决 Android SDK 35 错误

## 问题分析

错误信息显示系统仍在尝试使用 `android-35`，即使我们已经将 `compileSdk` 改为 34。这是因为：
1. Gradle 缓存中仍有 SDK 35 的转换结果
2. 可能安装了 SDK 35，系统会自动检测并使用

## 完整解决方案

### 步骤 1：确认 build.gradle 配置

确保 `app/build.gradle` 中：
- `compileSdk 34`（不是 35 或 36）
- `targetSdk 34`

### 步骤 2：删除 Gradle 转换缓存（关键！）

1. **关闭 Android Studio**

2. **删除以下文件夹**：
   ```
   C:\Users\15591\.gradle\caches\transforms-3\
   ```
   - 这是整个 `transforms-3` 文件夹
   - 这个文件夹包含了所有 SDK 版本的转换缓存

3. **删除项目本地缓存**（如果存在）：
   - 项目根目录下的 `.gradle` 文件夹
   - 项目根目录下的 `build` 文件夹
   - `app\build` 文件夹

### 步骤 3：卸载 Android SDK 35（如果已安装）

1. **打开 Android Studio**
2. **打开 SDK Manager**：
   - `File` → `Settings` → `Appearance & Behavior` → `System Settings` → `Android SDK`
   - 或 `Tools` → `SDK Manager`

3. **在 "SDK Platforms" 标签页**：
   - 找到 "Android 15.0 (API 35)"
   - **取消勾选**（如果已勾选）
   - 点击 "Apply" 卸载

4. **确保只安装 SDK 34**：
   - 勾选 "Android 14.0 (API 34)"
   - 取消勾选其他高版本（35, 36 等）

### 步骤 4：清理并重新构建

1. **清理缓存**：
   - `File` → `Invalidate Caches / Restart...` → `Invalidate and Restart`

2. **清理项目**：
   - `Build` → `Clean Project`

3. **同步项目**：
   - `File` → `Sync Project with Gradle Files`

4. **重新构建**：
   - `Build` → `Rebuild Project`

## 快速操作脚本（可选）

如果你想快速删除缓存，可以在 PowerShell 中运行：

```powershell
# 删除 Gradle 转换缓存
Remove-Item -Path "$env:USERPROFILE\.gradle\caches\transforms-3" -Recurse -Force -ErrorAction SilentlyContinue

# 删除项目构建缓存
Remove-Item -Path ".gradle" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "build" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item -Path "app\build" -Recurse -Force -ErrorAction SilentlyContinue
```

## 验证修复

完成上述步骤后：
1. 重新打开 Android Studio
2. 同步项目
3. 构建项目
4. 检查错误信息中是否还有 `android-35` 或 `android-36`

如果还有，请检查：
- `local.properties` 文件
- 其他模块的 `build.gradle` 文件
- Gradle 配置文件

