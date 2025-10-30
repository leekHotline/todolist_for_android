// src/main/java/com/example/myfirstapp/ui/home/HomeFragment.java

package com.example.myfirstapp.ui.home;

import android.animation.Animator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView; // ✨ 导入 Lottie
import com.example.myfirstapp.R;
import com.example.myfirstapp.Task; // ✨ 导入 Task
import com.example.myfirstapp.TaskAdapter;
import com.example.myfirstapp.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;
    private TaskAdapter taskAdapter;
    private LottieAnimationView lottieAnimationView; // ✨ Lottie 视图
    private MediaPlayer mediaPlayer; // ✨ 音效播放器
    private MediaPlayer trophySoundPlayer; // ✨ 新增：奖杯音效播放器 ✨

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // 初始化 Lottie 视图和音效播放器
        lottieAnimationView = binding.lottieAnimationView;
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.task_completed);
        // ✨ 新增：初始化奖杯音效播放器 ✨
        trophySoundPlayer = MediaPlayer.create(getContext(), R.raw.chime); // 确保音效文件名是 chime

        setupRecyclerView();
        setupFab();
        observeViewModel();

        return root;
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = binding.recyclerViewTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taskAdapter = new TaskAdapter();
        recyclerView.setAdapter(taskAdapter);

        // ✨ 新增：为 Adapter 设置我们自定义的点击监听器 ✨
        taskAdapter.setOnTaskCompletedListener(task -> {
            // 当 CheckBox 被点击时，调用和滑动一样的完成流程
            completeTask(task);
        });

        // ✨ 核心：添加滑动功能 ✨
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // 我们不需要拖动排序
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Task task = taskAdapter.getTaskAt(position); // 需要在 Adapter 中添加这个方法

                // 触发完成流程
                completeTask(task);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setupFab() {
        FloatingActionButton fab = binding.fabAddTask;
        fab.setOnClickListener(view -> showAddTaskDialog());
    }

    // 在 HomeFragment.java 中

    private void observeViewModel() {
        homeViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskAdapter.setTasks(tasks);
        });

        // ✨ 新增：观察奖杯动画触发事件 ✨
        homeViewModel.getTriggerTrophyAnimation().observe(getViewLifecycleOwner(), shouldTrigger -> {
            if (shouldTrigger) {
                playTrophyAnimation();
                // 通知 ViewModel 事件已处理完毕
                homeViewModel.doneTriggeringTrophyAnimation();
            }
        });
    }

    // 在 HomeFragment.java 中

    // ✨ 新增：播放奖杯动画和音效的方法 ✨
    private void playTrophyAnimation() {
        // 1. 播放音效
        if (trophySoundPlayer != null) {
            trophySoundPlayer.start();
        }

        // 2. 播放 Lottie 动画
        lottieAnimationView.setAnimation("trophy.json"); // 确保动画文件名是 trophy.json
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();

        // 3. 动画结束后自动隐藏
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimationView.setVisibility(View.GONE);
                lottieAnimationView.removeAllAnimatorListeners();
            }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    // ✨ 核心：完成任务的完整流程 ✨
    private void completeTask(Task task) {
        // 1. 播放音效
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        // 2. 播放 Lottie 动画
        lottieAnimationView.setAnimation("confetti.json"); // 确保你的动画文件名是 confetti.json
        lottieAnimationView.setVisibility(View.VISIBLE);
        lottieAnimationView.playAnimation();

        // 3. 更新 ViewModel 中的数据
        homeViewModel.completeTask(task);

        // 4. 动画结束后隐藏视图并真正删除任务
        lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                lottieAnimationView.setVisibility(View.GONE);
                // 延迟一小段时间再删除，避免列表刷新过快显得突兀
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    homeViewModel.removeTask(task);
                }, 200);
                lottieAnimationView.removeAllAnimatorListeners(); // 清理监听器，防止内存泄漏
            }
            // 其他回调方法可以为空
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
    }

    // 在 HomeFragment.java 中

    private void showAddTaskDialog() {
        // 1. 创建 AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // 2. 加载我们的自定义布局
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_task, null);

        // 3. 获取布局中的视图
        final EditText input = dialogView.findViewById(R.id.input_task_title);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);

        // 4. 将自定义视图设置给对话框
        builder.setView(dialogView);

        // 5. 创建并显示对话框
        final AlertDialog dialog = builder.create();

        // 关键：设置对话框背景为透明，否则圆角会被白色直角背景覆盖
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 6. 为按钮设置点击事件
        btnAdd.setOnClickListener(v -> {
            String taskTitle = input.getText().toString();
            if (!taskTitle.isEmpty()) {
                homeViewModel.addTask(taskTitle);
                dialog.dismiss(); // 关闭对话框
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss()); // 关闭对话框

        dialog.show();
    }


    // 在 HomeFragment.java 中

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        // 释放资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // ✨ 新增：释放奖杯音效播放器 ✨
        if (trophySoundPlayer != null) {
            trophySoundPlayer.release();
            trophySoundPlayer = null;
        }
    }
}