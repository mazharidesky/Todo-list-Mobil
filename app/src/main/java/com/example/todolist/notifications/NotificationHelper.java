package com.example.todolist.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import com.example.todolist.models.Task;

public class NotificationHelper {
    private Context context;
    public static final String CHANNEL_ID = "TodoListChannel";
    private static final String CHANNEL_NAME = "Todo List Notifications";

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for Todo List task reminders");
            channel.enableLights(true);
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public void scheduleNotification(Task task) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdf.parse(task.getDate() + " " + task.getTime());

            if (date == null) return;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("taskId", task.getId());
            intent.putExtra("taskTitle", task.getTitle());
            intent.putExtra("taskDescription", task.getDescription());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    task.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            calendar.getTimeInMillis(),
                            pendingIntent
                    );
                    Log.d("NotificationHelper", "Notification scheduled for: " + sdf.format(calendar.getTime()));
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        pendingIntent
                );
                Log.d("NotificationHelper", "Notification scheduled for: " + sdf.format(calendar.getTime()));
            }
        } catch (ParseException e) {
            Log.e("NotificationHelper", "Error scheduling notification", e);
        }
    }
}