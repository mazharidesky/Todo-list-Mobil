package com.example.todolist.activities;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.adapters.TaskAdapter;
import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.models.Task;
import java.util.List;

public class TaskFilterActivity extends AppCompatActivity {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private List<Task> taskList;
    private TextView completedCount;
    private TextView pendingCount;
    private CardView statisticsCard;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_filter);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Task Statistics");
        }

        // Initialize views
        completedCount = findViewById(R.id.completedCount);
        pendingCount = findViewById(R.id.pendingCount);
        statisticsCard = findViewById(R.id.statisticsCard);
        tasksRecyclerView = findViewById(R.id.filteredTasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RadioGroup filterGroup = findViewById(R.id.filterGroup);

        // Initialize database
        db = new DatabaseHelper(this);

        // Set up statistics card
        statisticsCard.setCardElevation(8f);
        statisticsCard.setRadius(16f);

        // Set up radio group listener
        filterGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioAll) {
                loadTasks(null);
            } else if (checkedId == R.id.radioCompleted) {
                loadTasks(true);
            } else if (checkedId == R.id.radioPending) {
                loadTasks(false);
            }
        });

        // Initial load
        updateStatistics();
        loadTasks(null);
    }

    private void loadTasks(Boolean isCompleted) {
        taskList = db.getFilteredTasks(isCompleted);
        taskAdapter = new TaskAdapter(this, taskList, new TaskAdapter.TaskAdapterListener() {
            @Override
            public void onTaskStatusChanged(Task task) {
                db.updateTask(task);
                updateStatistics();
            }

            @Override
            public void onTaskDeleted(Task task, int position) {
                db.deleteTask(task.getId());
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
                updateStatistics();
            }
        });
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void updateStatistics() {
        int completed = db.getTaskCount(true);
        int pending = db.getTaskCount(false);

        // Update statistics text
        completedCount.setText(String.format("Completed Tasks: %d", completed));
        pendingCount.setText(String.format("Pending Tasks: %d", pending));

        // Update colors based on counts
        completedCount.setTextColor(completed > 0 ? Color.rgb(76, 175, 80) : Color.GRAY);
        pendingCount.setTextColor(pending > 0 ? Color.rgb(244, 67, 54) : Color.GRAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
        loadTasks(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}