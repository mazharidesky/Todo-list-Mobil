package com.example.todolist.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.todolist.R;
import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.models.Task;
import com.example.todolist.notifications.NotificationHelper;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText taskInput;
    private TextInputEditText taskDescription;
    private TextInputEditText dateInput;
    private TextInputEditText timeInput;
    private DatabaseHelper db;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        taskInput = findViewById(R.id.taskInput);
        taskDescription = findViewById(R.id.taskDescription);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        Button saveButton = findViewById(R.id.saveButton);

        db = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add New Task");
        }
    }

    private void setupListeners() {
        dateInput.setOnClickListener(v -> showDatePicker());
        timeInput.setOnClickListener(v -> showTimePicker());
        findViewById(R.id.saveButton).setOnClickListener(v -> saveTask());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format(Locale.getDefault(), "%d-%02d-%02d",
                            year, month + 1, dayOfMonth);
                    dateInput.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d",
                            hourOfDay, minute);
                    timeInput.setText(time);
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void saveTask() {
        String title = taskInput.getText().toString().trim();
        String description = taskDescription.getText().toString().trim();
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (title.isEmpty()) {
            taskInput.setError("Please enter task title");
            return;
        }

        if (date.isEmpty()) {
            dateInput.setError("Please select date");
            return;
        }

        if (time.isEmpty()) {
            timeInput.setError("Please select time");
            return;
        }

        Task task = new Task(0, title);
        task.setDescription(description);
        task.setDate(date);
        task.setTime(time);

        long id = db.addTask(task);
        if (id > 0) {
            task.setId((int) id);
            notificationHelper.scheduleNotification(task);
            Toast.makeText(this, "Task added and reminder set!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to add task", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}