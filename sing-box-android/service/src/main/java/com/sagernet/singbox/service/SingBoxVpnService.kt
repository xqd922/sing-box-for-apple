package com.sagernet.singbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.sagernet.singbox.MainActivity
import kotlinx.coroutines.*

class SingBoxVpnService : VpnService(), LibboxServiceInterface {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "sing-box-vpn"
        private const val ACTION_START = "com.sagernet.singbox.START"
        private const val ACTION_STOP = "com.sagernet.singbox.STOP"
        private const val EXTRA_CONFIG_PATH = "config_path"

        fun startService(context: Context, configPath: String = "") {
            val intent = Intent(context, SingBoxVpnService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_CONFIG_PATH, configPath)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, SingBoxVpnService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private var libboxWrapper: LibboxWrapper? = null
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isActive = false
    private var uploadSpeed = 0L
    private var downloadSpeed = 0L
    private var connectionsCount = 0
    private var groupsCount = 0

    // Callbacks for UI updates
    private val callbacks = mutableListOf<ServiceCallback>()

    interface ServiceCallback {
        fun onSpeedUpdate(upload: Long, download: Long)
        fun onConnectionUpdate(count: Int)
        fun onGroupUpdate(count: Int)
        fun onStatusChanged(isRunning: Boolean)
        fun onError(error: String)
    }

    fun addCallback(callback: ServiceCallback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: ServiceCallback) {
        callbacks.remove(callback)
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        libboxWrapper = LibboxWrapper(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val configPath = intent.getStringExtra(EXTRA_CONFIG_PATH) ?: ""
                startForeground(NOTIFICATION_ID, createNotification("Starting..."))
                startVpn(configPath)
            }
            ACTION_STOP -> {
                stopVpn()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        serviceScope.cancel()
        callbacks.clear()
    }

    private fun startVpn(configPath: String) {
        if (isActive) return

        serviceScope.launch {
            try {
                isActive = true
                updateNotification("Connecting...")

                // Start sing-box using Libbox
                val success = libboxWrapper?.start(configPath) ?: false

                if (success) {
                    updateNotification("Connected")
                    notifyStatusChanged(true)
                } else {
                    updateNotification("Connection failed")
                    notifyError("Failed to start sing-box")
                    stopSelf()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateNotification("Error: ${e.message}")
                notifyError(e.message ?: "Unknown error")
                stopSelf()
            }
        }
    }

    private fun stopVpn() {
        if (!isActive) return

        isActive = false
        libboxWrapper?.stop()
        notifyStatusChanged(false)
    }

    // LibboxServiceInterface implementation
    override fun logMessage(message: String) {
        // Log to Android log
        android.util.Log.d("sing-box", message)
    }

    override fun notifySpeedUpdate(upload: Long, download: Long) {
        uploadSpeed = upload
        downloadSpeed = download
        callbacks.forEach { it.onSpeedUpdate(upload, download) }
        updateNotificationSpeed(upload, download)
    }

    override fun updateTraffic(upload: Long, download: Long) {
        // Update total traffic statistics
    }

    override fun updateConnections(count: Int) {
        connectionsCount = count
        callbacks.forEach { it.onConnectionUpdate(count) }
    }

    override fun updateGroups(count: Int) {
        groupsCount = count
        callbacks.forEach { it.onGroupUpdate(count) }
    }

    private fun notifyStatusChanged(isRunning: Boolean) {
        callbacks.forEach { it.onStatusChanged(isRunning) }
    }

    private fun notifyError(error: String) {
        callbacks.forEach { it.onError(error) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "sing-box VPN",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "sing-box VPN Service"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, SingBoxVpnService::class.java).apply {
                action = ACTION_STOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("sing-box")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_media_pause, "Stop", stopIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(text))
    }

    private fun updateNotificationSpeed(upload: Long, download: Long) {
        val uploadStr = formatSpeed(upload)
        val downloadStr = formatSpeed(download)
        updateNotification("↑ $uploadStr  ↓ $downloadStr")
    }

    private fun formatSpeed(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B/s"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB/s"
            else -> "${bytes / (1024 * 1024)} MB/s"
        }
    }

    override fun onRevoke() {
        super.onRevoke()
        stopVpn()
    }
}
