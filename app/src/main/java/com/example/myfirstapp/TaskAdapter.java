// src/main/java/com/example/myfirstapp/TaskAdapter.java

package com.example.myfirstapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    // ✨ 1. 新增：定义接口 ✨
    public interface OnTaskCompletedListener {
        void onTaskCompleted(Task task);
    }

    private List<Task> tasks = new ArrayList<>();
    private OnTaskCompletedListener listener; // ✨ 2. 新增：接口实例变量 ✨

    // ✨ 3. 新增：设置监听器的方法 ✨
    public void setOnTaskCompletedListener(OnTaskCompletedListener listener) {
        this.listener = listener;
    }

    // 1. ViewHolder 内部类：持有列表项的视图引用
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView taskTitle;
        public CheckBox taskCheckbox;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskCheckbox = itemView.findViewById(R.id.task_checkbox);
        }
    }

    // 2. 创建 ViewHolder 实例
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }



    // 4. 返回数据项的总数
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    // 5. 更新数据的方法
    public void setTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged(); // 通知 RecyclerView 刷新列表
    }

    // ✨ 新增：获取特定位置的任务 ✨
    public Task getTaskAt(int position) {
        return tasks.get(position);
    }

    // 在 onBindViewHolder 中根据任务状态显示删除线
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.taskTitle.setText(currentTask.getTitle());

        // ✨ 核心修改：处理点击事件 ✨

        // 1. 先移除旧的监听器，防止重复触发
        holder.taskCheckbox.setOnCheckedChangeListener(null);

        // 2. 设置 CheckBox 的状态
        holder.taskCheckbox.setChecked(currentTask.isCompleted());

        // 3. 添加新的监听器
        holder.taskCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 只有当用户主动勾选（从未完成到完成）时才触发
            if (isChecked && !currentTask.isCompleted() && listener != null) {
                // 通过接口回调，通知 HomeFragment
                listener.onTaskCompleted(currentTask);
            }
        });

        // 根据任务状态显示删除线 (这段逻辑保持不变)
        if (currentTask.isCompleted()) {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskTitle.setAlpha(0.5f);
        } else {
            holder.taskTitle.setPaintFlags(holder.taskTitle.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            holder.taskTitle.setAlpha(1.0f);
        }
    }
}