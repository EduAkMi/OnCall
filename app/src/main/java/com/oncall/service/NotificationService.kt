package com.oncall.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.oncall.R
import com.oncall.presentation.MainActivity
import com.oncall.repository.INotificationRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject lateinit var repository: INotificationRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val runService = intent?.getBooleanExtra(RUN_SERVICE_KEY, false) ?: false
        serviceScope.launch {
            when (runService) {
                true -> createNotification()
                false -> stopSelf()
            }
            repository.setIsRunning(runService)
        }
        return START_STICKY
    }

    private fun createNotification() {
        try {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val notification = buildNotification()
            notification.flags = Notification.FLAG_ONGOING_EVENT
            startForeground(NOTIFICATION_ID, notification)
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } catch (e: Exception) {
            Log.i("AAA", e.message.toString())
        }
    }

    private fun buildNotification(): Notification {
        val pendingIntent = createPendingIntent()
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.bees)
            .setContentTitle(getString(R.string.service_title))
            .setContentText(getString(R.string.service_message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        return builder.build()
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val CHANNEL_ID = "RandomChannelId"
        private const val CHANNEL_NAME = "WhoIsReadingThisChannelName"
        private const val NOTIFICATION_ID = 1
        const val RUN_SERVICE_KEY = "run_service"
    }
}
