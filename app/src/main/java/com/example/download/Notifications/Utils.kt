package com.example.download.Notifications

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.download.MainActivity
import com.example.download.R


// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(file: String,status: String, applicationContext: Context) {

    //Create bundle to pass
    val args = Bundle()
    args.putString("file", file)
    args.putString("status", status)

    // Create the content intent for the notification, which launches
    // this activity
    val contentPendingIntent = NavDeepLinkBuilder(applicationContext)
        .setGraph(R.navigation.main_navigation)
        .setDestination(R.id.detailsFragment)
        .setArguments(args)
        .createPendingIntent()

    // TODO: Step 1.12 create PendingIntent

    // Add style
    val downloadImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.ic_download
    )
//    val bigPicture = NotificationCompat.BigPictureStyle().bigPicture(eggImage).bigLargeIcon(null)
//
//    // Add snooze action
//    val snoozeIntent = Intent(applicationContext, SnoozeReceiver::class.java)
//    val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(
//        applicationContext,
//        REQUEST_CODE,
//        snoozeIntent,
//        FLAGS
//    )

    // Build the notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_channel_id)
    )
        .setSmallIcon(R.drawable.ic_download)
        .setLargeIcon(downloadImage)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(applicationContext
            .getString(R.string.notification_description))
        // Set content intent, what happens when user clicks notification
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(R.drawable.ic_download,applicationContext.getString(R.string.notification_button),contentPendingIntent)
        // Set priority for older devices
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}

// To cancel all notifications
fun NotificationManager.cancelNotifications(){
    cancelAll()
}