```
#  TaskMaster Go - 开发指南

本文档详细记录了 **TaskMaster Go** 应用的构建流程和核心功能的实现思路，遵循“先UI布局 (XML)，后逻辑实现 (Java)”的原则。

## 阶段一：项目初始化与基础布局

### 1. UI 布局 (XML)

-   **`activity_main.xml`**: 作为应用的主窗口，它只包含两个核心组件：
    1.  `NavHostFragment`: 一个容器，用于承载和切换不同的 Fragment (页面)。
    2.  `BottomNavigationView`: 底部的导航栏，用于在“主页”、“仪表盘”等模块间切换。
    *设计思路：保持 Activity 的“干净”，它只负责导航和窗口管理，不包含任何具体的业务UI。*

-   **`fragment_home.xml`**: 这是我们的核心页面——任务列表页。
    1.  `RecyclerView`: 用于高效地显示可滚动的任务列表。
    2.  `FloatingActionButton`: 右下角的“+”号悬浮按钮，用于触发添加任务的操作。
    3.  `LottieAnimationView`: 一个覆盖全屏的动画视图，默认隐藏。它被用来播放“完成任务”和“获得成就”的动画，通过设置 `elevation` 确保它显示在所有其他元素之上。

-   **`item_task.xml`**: 单个任务项的布局。
    1.  使用 `CardView` 作为根布局，创建带有阴影和圆角的卡片效果。
    2.  `CheckBox`: 显示任务的完成状态。
    3.  `TextView`: 显示任务的标题。

-   **`dialog_add_task.xml`**: 自定义“添加任务”对话框的布局。
    1.  使用 `CardView` 创建圆角背景。
    2.  包含 `TextInputEditText` (提供更好的输入体验) 和两个 `Button` (添加/取消)。
    *设计思路：放弃系统默认的 `AlertDialog` 样式，通过自定义布局来提升视觉美感和品牌一致性。*

### 2. 逻辑实现 (Java)

-   **`MainActivity.java`**:
    -   使用 `ViewBinding` 来安全地访问 `activity_main.xml` 中的视图。
    -   设置 `NavController` 和 `BottomNavigationView`，将导航栏的点击事件与 `NavHostFragment` 中的页面切换关联起来。

-   **`Task.java`**:
    -   最初是一个简单的 POJO (Plain Old Java Object)，包含 `id`, `title`, `isCompleted` 字段。
    -   在引入持久化后，被改造为 Room 的 `@Entity`，代表数据库中的一张表。

-   **`TaskAdapter.java`**:
    -   继承自 `RecyclerView.Adapter`，负责将 `Task` 对象列表绑定到 `item_task.xml` 视图上。
    -   **核心交互**: 定义了一个 `OnTaskCompletedListener` 接口。当 `CheckBox` 被点击时，通过此接口将事件回调给 `HomeFragment`，实现了 Adapter 和 Fragment 之间的解耦。

## 阶段二：核心交互与动画音效

### 1. UI 布局 (XML)

-   **资源文件准备**:
    -   **动画**: 将 `confetti.json` 和 `trophy.json` 放入 `app/src/main/assets` 目录。Lottie 通过此目录加载动画。
    -   **音效**: 将 `task_completed.mp3` 和 `chime.wav` 放入 `app/src/main/res/raw` 目录。`MediaPlayer` 通过 `R.raw.filename` 的方式引用这些资源。

### 2. 逻辑实现 (Java)

-   **`HomeFragment.java`**: 作为所有交互的中心。
    -   **滑动完成**:
        -   使用 `ItemTouchHelper.SimpleCallback` 附加到 `RecyclerView` 上。
        -   监听 `onSwiped` 事件，获取被滑动的任务，并调用 `completeTask()` 方法。
    -   **点击完成**:
        -   为 `TaskAdapter` 设置 `OnTaskCompletedListener` 的实现。
        -   在回调中，同样调用 `completeTask()` 方法。
    -   **`completeTask(Task task)` 方法**:
        1.  播放 `task_completed` 音效。
        2.  设置 `lottie_animation_view` 的动画文件为 `confetti.json` 并播放。
        3.  调用 `homeViewModel.completeTask(task)` 来更新任务状态。
        4.  在动画播放结束的回调 (`onAnimationEnd`) 中，调用 `homeViewModel.removeTask(task)`，实现“先庆祝，后消失”的效果。
    -   **`playTrophyAnimation()` 方法**:
        1.  播放 `chime` 音效。
        2.  设置 `lottie_animation_view` 的动画文件为 `trophy.json` 并播放。
        3.  动画结束后自动隐藏。

-   **`HomeViewModel.java`**:
    -   **任务计数**:
        -   在 `addTask()` 方法中，对一个内部计数器 `tasksCreatedCount` 进行累加。
        -   检查 `tasksCreatedCount % 5 == 0` 是否成立。
        -   如果成立，通过一个 `MutableLiveData<Boolean>` 类型的 `triggerTrophyAnimation` 发送一个 `true` 值。
    -   **事件处理**: `HomeFragment` 观察 `triggerTrophyAnimation` 这个 LiveData。一旦收到 `true`，就调用 `playTrophyAnimation()`，并立即通知 ViewModel 将状态重置为 `false`，以防屏幕旋转等配置更改导致动画重复播放。

## 阶段三：数据持久化

### 1. 逻辑实现 (Java)

-   **`Task.java` (改造)**:
    -   添加 `@Entity` 注解，将其声明为数据库实体。
    -   为 `id` 字段添加 `@PrimaryKey(autoGenerate = true)`，让数据库自动管理主键。

-   **`TaskDao.java` (接口)**:
    -   使用 `@Dao` 注解。
    -   定义了 `@Insert`, `@Update`, `@Query` 等注解方法，用于声明数据库操作。
    -   `getAllTasks()` 方法的返回类型为 `LiveData<List<Task>>`。这是 Room 的一个强大功能，它使得当数据库内容发生变化时，能自动通知观察者（UI）。

-   **`TaskDatabase.java` (抽象类)**:
    -   使用 `@Database` 注解，声明包含的实体和数据库版本。
    -   提供一个单例模式的 `getDatabase()` 方法，确保应用中只有一个数据库实例。
    -   创建了一个 `ExecutorService` 线程池，用于在后台执行所有数据库写操作（增、删、改）。

-   **`TaskRepository.java` (仓库类)**:
    -   作为数据层和 ViewModel 之间的中间人。
    -   它持有 `TaskDao` 的实例，并对外暴露简洁的数据操作方法（如 `insert`, `delete`）。
    -   它的 `getAllTasks()` 直接返回从 DAO 获取的 `LiveData`。
    -   所有写操作都在 `databaseWriteExecutor` 线程池中执行。

-   **`HomeViewModel.java` (最终改造)**:
    -   继承自 `AndroidViewModel` 以获取 `Application` 上下文。
    -   不再自己创建和管理任务列表，而是持有 `TaskRepository` 的实例。
    -   `getTasks()` 方法直接返回从 `Repository` 获取的 `LiveData`。
    -   `addTask`, `completeTask`, `removeTask` 等方法，现在只是简单地调用 `Repository` 的对应方法，将数据操作全权委托给数据层。

通过以上步骤，我们构建了一个结构清晰、功能丰富且可扩展的现代化安卓应用。

---
```