package com.example.todolist.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.R;
import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.models.Task;
import com.google.android.material.textfield.TextInputEditText;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText taskInput;
    private TextInputEditText taskDescription;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize views
        taskInput = findViewById(R.id.taskInput);
        taskDescription = findViewById(R.id.taskDescription);
        Button saveButton = findViewById(R.id.saveButton);

        // Initialize database
        db = new DatabaseHelper(this);

        // Set up back button in action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Task");
        }

        saveButton.setOnClickListener(v -> saveTask());
    }

    private void saveTask() {
        String title = taskInput.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();

        if (!title.isEmpty()) {
            Task task = new Task(0, title);
            task.setDescription(description);

            long id = db.addTask(task);
            if (id > 0) {
                Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
            }
        } else {
            taskInput.setError("Please enter task title");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}