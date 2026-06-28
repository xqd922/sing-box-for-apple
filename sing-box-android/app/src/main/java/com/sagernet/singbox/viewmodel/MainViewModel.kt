package com.sagernet.singbox.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sagernet.singbox.ExtensionProfile
import com.sagernet.singbox.VpnState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _vpnState = MutableStateFlow(VpnState.DISCONNECTED)
    val vpnState: StateFlow<VpnState> = _vpnState.asStateFlow()

    private val _extensionProfile = MutableStateFlow<ExtensionProfile?>(null)
    val extensionProfile: StateFlow<ExtensionProfile?> = _extensionProfile.asStateFlow()

    private val _groupsCount = MutableStateFlow(0)
    val groupsCount: StateFlow<Int> = _groupsCount.asStateFlow()

    private val _connectionsCount = MutableStateFlow(0)
    val connectionsCount: StateFlow<Int> = _connectionsCount.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            // TODO: Load profile from database
            _extensionProfile.value = ExtensionProfile(
                name = "Default Profile",
                status = VpnState.DISCONNECTED
            )
        }
    }

    fun startVpn() {
        viewModelScope.launch {
            // TODO: Start VPN service
            _vpnState.value = VpnState.CONNECTING
            // Simulate connection
            kotlinx.coroutines.delay(1000)
            _vpnState.value = VpnState.CONNECTED
            _extensionProfile.value = _extensionProfile.value?.copy(status = VpnState.CONNECTED)
        }
    }

    fun stopVpn() {
        viewModelScope.launch {
            // TODO: Stop VPN service
            _vpnState.value = VpnState.DISCONNECTING
            // Simulate disconnection
            kotlinx.coroutines.delay(500)
            _vpnState.value = VpnState.DISCONNECTED
            _extensionProfile.value = _extensionProfile.value?.copy(status = VpnState.DISCONNECTED)
        }
    }

    fun updateGroupsCount(count: Int) {
        _groupsCount.value = count
    }

    fun updateConnectionsCount(count: Int) {
        _connectionsCount.value = count
    }
}
