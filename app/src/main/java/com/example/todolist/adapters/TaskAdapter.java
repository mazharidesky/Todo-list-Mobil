package com.example.todolist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolist.R;
import com.example.todolist.models.Task;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private final List<Task> taskList;
    private final Context context;
    private final TaskAdapterListener listener;

    public interface TaskAdapterListener {
        void onTaskStatusChanged(Task task);
        void onTaskDeleted(Task task, int position);
    }

    public TaskAdapter(Context context, List<Task> taskList, TaskAdapterListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.taskTitle.setText(task.getTitle());
        holder.taskDescription.setText(task.getDescription());
        holder.taskCheckBox.setChecked(task.isCompleted());

        // Change text color based on completion status
        int textColor = task.isCompleted() ?
                context.getResources().getColor(R.color.completed_task) :
                context.getResources().getColor(R.color.pending_task);
        holder.taskTitle.setTextColor(textColor);

        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            if (listener != null) {
                listener.onTaskStatusChanged(task);
            }
        });

        holder.deleteButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDeleted(task, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        TextView taskTitle;
        TextView taskDescription;
        ImageButton deleteButton;

        TaskViewHolder(View itemView) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            taskTitle = itemView.findViewById(R.id.taskTitle);
            taskDescription = itemView.findViewById(R.id.taskDescription);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}