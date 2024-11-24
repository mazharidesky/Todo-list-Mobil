package com.example.todolist.activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.adapters.TaskAdapter;
import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.models.Task;
import com.google.android.material.tabs.TabLayout;
import java.util.List;

public class TaskFilterActivity extends AppCompatActivity {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private List<Task> taskList;
    private TextView completedCount;
    private TextView pendingCount;
    private Boolean currentFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_filter);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistik ReminderIn");
        }

        initializeViews();
        setupTabLayout();

        // Initial load
        updateStatistics();
        loadTasks(null);
    }

    private void initializeViews() {
        // Initialize views
        completedCount = findViewById(R.id.completedCount);
        pendingCount = findViewById(R.id.pendingCount);
        tasksRecyclerView = findViewById(R.id.filteredTasksRecyclerView);
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize database
        db = new DatabaseHelper(this);
    }

    private void setupTabLayout() {
        TabLayout filterTabs = findViewById(R.id.filterTabs);
        filterTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        currentFilter = null;
                        loadTasks(null);  // All tasks
                        break;
                    case 1:
                        currentFilter = true;
                        loadTasks(true);  // Completed tasks
                        break;
                    case 2:
                        currentFilter = false;
                        loadTasks(false); // Pending tasks
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadTasks(Boolean isCompleted) {
        currentFilter = isCompleted;
        taskList = db.getFilteredTasks(isCompleted);
        setupAdapter();
    }

    private void setupAdapter() {
        taskAdapter = new TaskAdapter(this, taskList, new TaskAdapter.TaskAdapterListener() {
            @Override
            public void onTaskStatusChanged(Task task) {
                db.updateTask(task);
                updateStatistics();

                // Jika dalam mode filter, refresh list sesuai filter
                if (currentFilter != null && task.isCompleted() != currentFilter) {
                    taskList.remove(task);
                    taskAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTaskDeleted(Task task, int position) {
                try {
                    // Hapus dari database
                    db.deleteTask(task.getId());

                    // Hapus dari list jika masih ada
                    if (position < taskList.size()) {
                        taskList.remove(position);
                        taskAdapter.notifyItemRemoved(position);
                        // Notify range changed untuk mencegah crash
                        taskAdapter.notifyItemRangeChanged(position, taskList.size());
                    }

                    // Update statistik
                    updateStatistics();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Jika terjadi error, refresh seluruh list
                    loadTasks(currentFilter);
                }
            }
        });
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    private void updateStatistics() {
        try {
            int completed = db.getTaskCount(true);
            int pending = db.getTaskCount(false);

            // Update statistics numbers
            completedCount.setText(String.valueOf(completed));
            pendingCount.setText(String.valueOf(pending));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatistics();
        loadTasks(currentFilter); // Gunakan filter yang sedang aktif
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}