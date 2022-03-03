package com.europerailroad.app.features.commons

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.europerailroad.app.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.railroad.railroad.R
import java.util.*


class FirebaseMessagingCloud : FirebaseMessagingService() {

    private val channelId = "com.europerailroad.app"
    private val channelName = "Europe Rail Road"

    private val titleKey = "title_key"
    private val bodyKey = "body_key"

    override fun onMessageReceived(message: RemoteMessage) {
        if (message != null && message.data.isNotEmpty()) {
            makeNotification(message)
        }
    }

    private fun makeNotification(message: RemoteMessage) {

        val title = message.data[titleKey.toLowerCase()]
        val body = message.data[bodyKey.toLowerCase()]

        val intent = Intent(
            this,
            MainActivity::class.java
        )

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            Random().nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)

        notificationBuilder
            .setGroup(channelId)
            .setSmallIcon(R.drawable.ic_notification)
            //.setBadgeIconType(R.drawable.ic_notification)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setContentText(body)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
            )

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val androidChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            androidChannel.enableLights(true)
            androidChannel.enableVibration(true)
            androidChannel.lightColor = Color.GREEN
            androidChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            notificationManager.createNotificationChannel(androidChannel)

        }

        notificationManager.notify((Math.random() * 10).toInt(), notificationBuilder.build())
    }
}