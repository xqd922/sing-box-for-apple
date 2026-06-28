package com.sagernet.singbox.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: Check if VPN should auto-start on boot
            // if (shouldAutoStart(context)) {
            //     SingBoxVpnService.startService(context)
            // }
        }
    }
}
