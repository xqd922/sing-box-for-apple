package com.sagernet.singbox.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class SingBoxVpnService : VpnService() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "sing-box-vpn"

        fun startService(context: Context) {
            val intent = Intent(context, SingBoxVpnService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, SingBoxVpnService::class.java)
            context.stopService(intent)
        }
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isActive = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification("Starting..."))
        startVpn()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        serviceScope.cancel()
    }

    private fun startVpn() {
        if (isActive) return

        serviceScope.launch {
            try {
                isActive = true
                updateNotification("Connecting...")

                // Build VPN interface
                val builder = Builder()
                    .setSession("sing-box")
                    .setMtu(9000)
                    .addAddress("172.19.0.1", 30)
                    .addRoute("0.0.0.0", 0)
                    .addRoute("::", 0)
                    .addDnsServer("1.1.1.1")
                    .addDnsServer("8.8.8.8")
                    .setBlocking(true)

                // Establish VPN
                vpnInterface = builder.establish()

                // TODO: Start sing-box core using Libbox
                // LibboxStart(configContent, vpnInterface!!.fd)

                updateNotification("Connected")
            } catch (e: Exception) {
                e.printStackTrace()
                updateNotification("Error: ${e.message}")
                stopSelf()
            }
        }
    }

    private fun stopVpn() {
        if (!isActive) return

        isActive = false
        vpnInterface?.close()
        vpnInterface = null

        // TODO: Stop sing-box core
        // LibboxStop()
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
            packageManager.getLaunchIntentForPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("sing-box")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification(text))
    }

    override fun onRevoke() {
        super.onRevoke()
        stopVpn()
    }
}
