// src/main/java/com/example/myfirstapp/ui/home/HomeViewModel.java

package com.example.myfirstapp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.myfirstapp.Task;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    // 使用 MutableLiveData 来持有任务列表，这样当数据变化时，UI可以自动更新
    private final MutableLiveData<List<Task>> tasks;
    private long nextTaskId = 0; // 用于生成唯一的 Task ID
    private int tasksCreatedCount = 0; // ✨ 1. 新增：任务创建计数器 ✨

    // ✨ 2. 新增：用于通知触发奖杯动画的 LiveData ✨
    private final MutableLiveData<Boolean> triggerTrophyAnimation = new MutableLiveData<>();

    public HomeViewModel() {
        tasks = new MutableLiveData<>();
        // ✨ 3. 初始化 LiveData ✨
        triggerTrophyAnimation.setValue(false);
        loadTasks(); // 加载一些初始的假数据
    }

    // ✨ 4. 新增：让 Fragment 可以观察这个事件 ✨
    public LiveData<Boolean> getTriggerTrophyAnimation() {
        return triggerTrophyAnimation;
    }

    // ✨ 5. 新增：重置触发器状态，防止重复触发 ✨
    public void doneTriggeringTrophyAnimation() {
        triggerTrophyAnimation.setValue(false);
    }

    private void loadTasks() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        // 添加一些示例任务
        initialTasks.add(new Task(nextTaskId++, "🚀 学习 Jetpack Compose"));
        initialTasks.add(new Task(nextTaskId++, "🎨 设计酷炫的完成动画"));
        initialTasks.add(new Task(nextTaskId++, "🎵 添加史诗级音效"));
        initialTasks.add(new Task(nextTaskId++, "修复那个讨厌的 Bug"));
        initialTasks.add(new Task(nextTaskId++, "晚上 8 点跑步 3 公里"));

        tasks.setValue(initialTasks); // 更新 LiveData 的值

        tasksCreatedCount += 5; //计数 5的倍数
    }

    // 添加新任务的方法
    public void addTask(String title) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks == null) {
            currentTasks = new ArrayList<>();
        }
        // 创建一个新列表，因为直接修改 LiveData 的列表可能不会触发更新
        ArrayList<Task> newTasks = new ArrayList<>(currentTasks);
        newTasks.add(new Task(nextTaskId++, title));
        tasks.setValue(newTasks);
        // ✨ 6. 核心逻辑：每次添加任务时，计数并检查是否触发成就 ✨
        tasksCreatedCount++;
        if (tasksCreatedCount > 0 && tasksCreatedCount % 5 == 0) {
            triggerTrophyAnimation.setValue(true); // 触发事件！
        }
    }

    // ✨✨✨ 解决方案：添加这个缺失的方法 ✨✨✨
    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    // ✨ 新增：完成任务的方法 ✨
    public void completeTask(Task taskToComplete) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks == null) return;

        // 找到对应的任务并更新其状态
        for (Task task : currentTasks) {
            if (task.getId() == taskToComplete.getId()) {
                task.setCompleted(true);
                break;
            }
        }

        // 创建一个新列表来触发 LiveData 更新
        ArrayList<Task> updatedTasks = new ArrayList<>(currentTasks);
        tasks.setValue(updatedTasks);
    }

    // ✨ 新增：删除任务的方法 (完成动画后调用) ✨
    public void removeTask(Task taskToRemove) {
        List<Task> currentTasks = tasks.getValue();
        if (currentTasks == null) return;

        ArrayList<Task> newTasks = new ArrayList<>(currentTasks);
        newTasks.removeIf(task -> task.getId() == taskToRemove.getId());
        tasks.setValue(newTasks);
    }
}