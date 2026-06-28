package com.sagernet.singbox.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.sagernet.singbox.service.SingBoxVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VpnRepository @Inject constructor(
    private val context: Context
) {
    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    private val _uploadSpeed = MutableStateFlow(0L)
    val uploadSpeed: StateFlow<Long> = _uploadSpeed.asStateFlow()

    private val _downloadSpeed = MutableStateFlow(0L)
    val downloadSpeed: StateFlow<Long> = _downloadSpeed.asStateFlow()

    private val _connectionsCount = MutableStateFlow(0)
    val connectionsCount: StateFlow<Int> = _connectionsCount.asStateFlow()

    private val _groupsCount = MutableStateFlow(0)
    val groupsCount: StateFlow<Int> = _groupsCount.asStateFlow()

    private var bound = false
    private var service: SingBoxVpnService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            // Service connected
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            // Service disconnected
            bound = false
            service = null
            _vpnState.value = VpnState.DISCONNECTED
        }
    }

    private val serviceCallback = object : SingBoxVpnService.ServiceCallback {
        override fun onSpeedUpdate(upload: Long, download: Long) {
            _uploadSpeed.value = upload
            _downloadSpeed.value = download
        }

        override fun onConnectionUpdate(count: Int) {
            _connectionsCount.value = count
        }

        override fun onGroupUpdate(count: Int) {
            _groupsCount.value = count
        }

        override fun onStatusChanged(isRunning: Boolean) {
            _vpnState.value = if (isRunning) VpnState.CONNECTED else VpnState.DISCONNECTED
        }

        override fun onError(error: String) {
            _vpnState.value = VpnState.DISCONNECTED
        }
    }

    fun startVpn(configPath: String) {
        _vpnState.value = VpnState.CONNECTING
        SingBoxVpnService.startService(context, configPath)
    }

    fun stopVpn() {
        _vpnState.value = VpnState.DISCONNECTING
        SingBoxVpnService.stopService(context)
    }

    fun bindService() {
        val intent = Intent(context, SingBoxVpnService::class.java)
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService() {
        if (bound) {
            context.unbindService(connection)
            bound = false
        }
    }

    fun isRunning(): Boolean {
        return _vpnState.value == VpnState.CONNECTED
    }
}

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    REASSERTING,
    DISCONNECTING
}
