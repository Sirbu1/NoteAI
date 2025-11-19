# 智能笔记 (NoteAI)

## 项目简介

NoteAI 是一个智能笔记应用，允许用户在本地创建、搜索、管理笔记内容，并可以借助 AI 自动生成摘要、主题标签或推荐。

## 技术栈

- **开发语言**: Java
- **UI 框架**: Material Design Components (原生 XML 布局)
- **架构模式**: MVVM (Model-View-ViewModel)
- **数据库**: Room (SQLite ORM)
- **异步处理**: Executor + LiveData
- **依赖注入**: 手动依赖注入（通过 Application 类）

## 项目结构

```
app/src/main/java/com/notai/
├── data/                    # 数据层
│   ├── model/              # 数据模型
│   │   ├── Note.java
│   │   ├── Tag.java
│   │   ├── NoteTagCrossRef.java
│   │   └── NoteWithTags.java
│   ├── dao/                # 数据访问对象
│   │   ├── NoteDao.java
│   │   └── TagDao.java
│   ├── database/           # 数据库配置
│   │   ├── NoteDatabase.java
│   │   └── Converters.java
│   └── repository/         # 仓库层
│       ├── NoteRepository.java
│       └── TagRepository.java
├── ui/                      # UI 层
│   ├── MainActivity.java   # 主界面
│   ├── adapter/            # RecyclerView 适配器
│   │   ├── NoteAdapter.java
│   │   ├── TagChipAdapter.java
│   │   └── TagManageAdapter.java
│   ├── note/               # 笔记相关界面
│   │   └── NoteEditActivity.java
│   ├── tag/                # 标签相关界面
│   │   └── TagManageActivity.java
│   └── viewmodel/          # ViewModel 层
│       ├── NoteViewModel.java
│       └── TagViewModel.java
└── NoteAIApplication.java   # Application 类
```

## 功能特性

### 阶段一：基础功能 ✅

1. **笔记管理**
   - ✅ 新增笔记
   - ✅ 编辑笔记
   - ✅ 删除笔记
   - ✅ 批量管理（长按删除）

2. **标签/分类**
   - ✅ 支持多标签
   - ✅ 标签编辑
   - ✅ 标签删除
   - ✅ 笔记与标签关联

3. **数据持久化**
   - ✅ 使用 Room (SQLite ORM) 保存数据
   - ✅ 笔记表、标签表、关联表设计

4. **搜索功能**
   - ✅ 标题搜索
   - ✅ 内容搜索
   - ✅ 标签搜索
   - ✅ 实时搜索

5. **UI/UX**
   - ✅ Material Design 3 设计
   - ✅ 响应式布局
   - ✅ 空状态提示

## 数据库设计

### Notes 表
- `id`: Long (主键，自增)
- `title`: String (标题)
- `content`: String (内容)
- `createdAt`: Long (创建时间)
- `updatedAt`: Long (更新时间)

### Tags 表
- `id`: Long (主键，自增)
- `name`: String (标签名称)
- `color`: String (标签颜色)

### NoteTagCrossRef 表（关联表）
- `noteId`: Long (笔记ID)
- `tagId`: Long (标签ID)
- 复合主键: (noteId, tagId)

## 架构设计

### MVVM 架构

```
UI Layer (Activities/Fragments)
    ↓
ViewModel Layer (NoteViewModel, TagViewModel)
    ↓
Repository Layer (NoteRepository, TagRepository)
    ↓
Data Access Layer (DAO)
    ↓
Database Layer (Room)
```

### 数据流

1. **UI → ViewModel**: 用户操作触发 ViewModel 方法
2. **ViewModel → Repository**: ViewModel 调用 Repository 方法
3. **Repository → DAO**: Repository 调用 DAO 进行数据库操作
4. **Database → Repository**: 返回 Flow/数据
5. **Repository → ViewModel**: 通过 Flow 传递数据
6. **ViewModel → UI**: 通过 StateFlow 更新 UI

## 开发环境要求

- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 17
- Android SDK 24+ (最低支持 Android 7.0)
- Gradle 8.2+

## 构建和运行

### 快速开始

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击运行按钮（▶️）

### 详细运行指南

**初学者请查看：`运行指南.md`** - 包含详细的步骤说明和常见问题解决方案

## 后续开发计划

### 阶段二：功能扩展与交互优化
- [ ] Markdown 渲染支持
- [ ] 全文搜索优化（FTS5）
- [ ] 长文档性能优化

### 阶段三：AI 能力集成
- [ ] AI 摘要生成
- [ ] AI 标签推荐
- [ ] 笔记润色功能
- [ ] 错别字检测与纠错

### 阶段四：性能调优
- [ ] 性能分析报告
- [ ] 内存优化
- [ ] 启动速度优化
- [ ] 日志系统与崩溃捕获

## 许可证

本项目仅用于学习和评估目的。
# NoteAI
