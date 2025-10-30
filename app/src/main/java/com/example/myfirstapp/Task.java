// src/main/java/com/example/myfirstapp/Task.java

package com.example.myfirstapp;

public class Task {
    private final long id;
    private final String title;
    private boolean isCompleted;

    public Task(long id, String title) {
        this.id = id;
        this.title = title;
        this.isCompleted = false;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}