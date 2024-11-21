package com.example.todolist.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addTaskBtn = findViewById(R.id.btnAddTask);
        Button viewTasksBtn = findViewById(R.id.btnViewTasks);
        Button filterTasksBtn = findViewById(R.id.btnFilterTasks);

        // Menggunakan explicit Intent dengan package name lengkap
        addTaskBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.todolist.activities.AddTaskActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        viewTasksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.todolist.activities.TaskListActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        filterTasksBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.todolist.activities.TaskFilterActivity.class);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}