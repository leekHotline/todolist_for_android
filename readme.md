# 🏆 TaskMaster Go - 游戏化任务清单应用

**TaskMaster Go** 不仅仅是一个普通的待办事项列表，它是一个将“完成任务”游戏化的安卓应用。通过集成立即的视觉和听觉奖励，以及里程碑式的成就系统，它旨在将枯燥的任务管理转变为一种有趣、令人上瘾的体验。

<img width="376" height="832" alt="图片" src="https://github.com/user-attachments/assets/a9d7da35-f07b-47de-a3c4-967cc42fd61c" />
<img width="370" height="830" alt="图片" src="https://github.com/user-attachments/assets/704a67fe-bfc7-46f6-93d4-6ed9be7d6fbb" />

<img width="371" height="832" alt="图片" src="https://github.com/user-attachments/assets/a6a9c1ba-f492-4ba6-adf8-17ca7dbe6f5a" />


## ✨ 项目亮点

-   **现代化的UI设计**: 采用 Material Design 3 风格，界面简洁、直观。
-   **丰富的交互动画**:
    -   **完成奖励**: 通过滑动或点击完成任务时，会触发华丽的五彩纸屑（Confetti）Lottie动画，并伴有清脆的音效，提供即时满足感。
    -   **成就系统**: 每当用户完成5个任务时，会触发一个巨大的“奖杯”动画和特殊的提示音，庆祝这一里程碑。
-   **流畅的手势操作**: 支持向右滑动卡片来快速完成任务，操作自然流畅。
-   **自定义美化组件**: 使用自定义布局，打造了美观的圆角“添加任务”对话框，提升了用户体验。
-   **持久化存储**: 基于 **Room** 数据库，所有任务数据都会被本地持久化保存。即使关闭应用或重启手机，任务也不会丢失。
-   **稳健的架构**: 采用 Google 官方推荐的 **MVVM (Model-View-ViewModel)** 架构，结合 `ViewModel`, `LiveData`, 和 `Repository`，实现了数据与UI的解耦，代码结构清晰，易于维护和扩展。

## 🚀 技术栈

-   **语言**: Java
-   **架构**: MVVM (ViewModel, LiveData, Repository)
-   **UI**:
    -   Android Jetpack (Fragment, Navigation Component)
    -   RecyclerView & CardView
    -   Material Design 3
-   **动画**: [Lottie](https://github.com/airbnb/lottie-android) - 用于渲染高质量的 After Effects 动画。
-   **数据持久化**: [Room](https://developer.android.com/training/data-storage/room) - Google 官方的 ORM 数据库，用于管理本地 SQLite 数据。
-   **异步处理**: `ExecutorService` - 用于在后台线程执行数据库操作，避免阻塞主线程。

## 效果演示

| 添加任务 (自定义对话框) | 滑动完成 (动画与音效) | 里程碑成就 (奖杯动画) |
| :----------------------: | :--------------------: | :--------------------: |
| ![添加任务](https://i.imgur.com/your-add-task-gif.gif) | ![完成任务](https://i.imgur.com/your-complete-task-gif.gif) | ![奖杯成就](https://i.imgur.com/your-trophy-gif.gif) |
*(提示: 你可以将应用的录屏GIF上传到图床，然后替换上面的链接)*

## 如何运行

1.  克隆或下载此项目。
2.  使用 Android Studio 打开项目。
3.  等待 Gradle 同步完成。
4.  点击 "Run 'app'" 按钮，在模拟器或真实设备上运行。

---
