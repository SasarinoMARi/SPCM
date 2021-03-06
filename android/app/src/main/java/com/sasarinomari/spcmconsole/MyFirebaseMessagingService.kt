package com.sasarinomari.spcmconsole

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sasarinomari.spcmconsole.network.APIClient


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val api = object : APIClient(this) {
            override fun error(message: String) { }
        }
        api.updateFcmToken(token) { }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationManager = NotificationManagerCompat.from(
            applicationContext
        )

        val CHANNEL_ID = "general"
        val CHANNEL_NAME = "general"

        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(applicationContext)
        }

        val title = remoteMessage.notification!!.title
        val body = remoteMessage.notification!!.body

        builder.setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)

        val notification: Notification = builder.build()
        notificationManager.notify(1, notification)
    }
}