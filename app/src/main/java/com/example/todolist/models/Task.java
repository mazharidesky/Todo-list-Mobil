package com.example.todolist.models;

public class Task {
    private int id;
    private String title;
    private String description;
    private boolean isCompleted;
    private String date;
    private String time;

    public Task(int id, String title) {
        this.id = id;
        this.title = title;
        this.description = "";
        this.isCompleted = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    // Tambahkan getter dan setter
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}