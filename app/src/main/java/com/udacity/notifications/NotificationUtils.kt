package com.udacity.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

// Notification ID.
private val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, status: String) {
    // Create the content intent for the notification, which launches the details activity
    val contentIntent = Intent(applicationContext,DetailActivity::class.java)
    contentIntent.putExtra("CurrentStatus",status)
    contentIntent.putExtra("Message",messageBody)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    // Build the notification
    val builder = NotificationCompat
        .Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_id)
        )
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_launcher_foreground,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    notify(NOTIFICATION_ID, builder.build())
}

//Cancel the notifications
fun NotificationManager.cancelNotifications() {
    cancelAll()
}

//Create the Channel
fun createChannel(channelId: String, channelName: String, application: Application) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setShowBadge(false)
        }
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        val notificationManager = application.getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }
}