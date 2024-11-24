package com.example.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.adapters.TaskAdapter;
import com.example.todolist.database.DatabaseHelper;
import com.example.todolist.models.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {
    private RecyclerView tasksRecyclerView;
    private TaskAdapter taskAdapter;
    private DatabaseHelper db;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Set up action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Your Tasks");
        }

        // Initialize views
        tasksRecyclerView = findViewById(R.id.tasksRecyclerView);
        ExtendedFloatingActionButton fabAddTask = findViewById(R.id.fabAddTask);

        // Setup RecyclerView
        tasksRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        tasksRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Scrolling up
                    fabAddTask.shrink();
                } else {
                    // Scrolling down
                    fabAddTask.extend();
                }
            }
        });

        // Initialize database
        db = new DatabaseHelper(this);

        // Setup FAB click listener
        fabAddTask.setOnClickListener(view -> {
            Intent intent = new Intent(TaskListActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        // Load tasks
        loadTasks();
    }

    private void loadTasks() {
        taskList = db.getAllTasks();
        taskAdapter = new TaskAdapter(this, taskList, new TaskAdapter.TaskAdapterListener() {
            @Override
            public void onTaskStatusChanged(Task task) {
                db.updateTask(task);
            }

            @Override
            public void onTaskDeleted(Task task, int position) {
                db.deleteTask(task.getId());
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
            }
        });
        tasksRecyclerView.setAdapter(taskAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks(); // Reload tasks when returning to this activity
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}