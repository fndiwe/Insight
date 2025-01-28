package com.franklinndiwe.insight.utils

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.franklinndiwe.insight.MainActivity
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Setting
import java.util.Calendar
import java.util.concurrent.TimeUnit

class DailyQuoteNotification(context: Context, setting: Setting) {
    // Create a unique notification channel ID
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "DailyQuote"
        const val TAG = "DailyQuoteTag"
    }

    private val workManager = WorkManager.getInstance(context)

    private val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, setting.reminderTimeHour)
        set(Calendar.MINUTE, setting.reminderTimeMinute)
        set(Calendar.SECOND, 0)
    }

    // Create a OneTimeWorkRequest to show the notification
    private val notificationWorkRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS).setInitialDelay(
            calculateDelayUntilNextNotification(), TimeUnit.MILLISECONDS
        ).addTag(TAG).build()

    fun scheduleNotification() = workManager.enqueueUniquePeriodicWork(
        TAG, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, notificationWorkRequest
    )

    fun cancelSchedule() = workManager.cancelAllWorkByTag(TAG)

    private val workInfo = workManager.getWorkInfosForUniqueWork(TAG).get()

    val isScheduled = !workInfo.isNullOrEmpty() && workInfo[0].state == WorkInfo.State.ENQUEUED

    // Function to calculate the delay until the next notification
    private fun calculateDelayUntilNextNotification(): Long {
        var delay = calendar.timeInMillis - System.currentTimeMillis()
        if (delay <= 0) {
            delay += AlarmManager.INTERVAL_DAY
        }
        return delay
    }

    // Function to create the notification channel
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val descriptionText = context.getString(R.string.daily_quote)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                NotificationChannel(NOTIFICATION_CHANNEL_ID, descriptionText, importance).apply {
                    description = descriptionText
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, contentText: String) {
        createNotificationChannel(context)
        // Intent to launch when the notification is clicked
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(
            context, NOTIFICATION_CHANNEL_ID
        ).setContentTitle(context.getString(R.string.daily_quote)).setContentText(contentText)
            .setSmallIcon(R.drawable.quote).setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                    if (ActivityCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) notify(
                        1, notificationBuilder.build()
                    )
                }

                else -> notify(
                    1, notificationBuilder.build()
                )
            }
        }
    }
}