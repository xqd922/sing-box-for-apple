package com.sagernet.singbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagernet.singbox.ui.VpnState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = true,
    val vpnState: VpnState = VpnState.DISCONNECTED,
    val profile: ProfileInfo? = null,
    val profileName: String = "",
    val uptime: String? = null,
    val connectionsCount: Int = 0,
    val uploadSpeed: Long = 0,
    val downloadSpeed: Long = 0,
    val uploadTotal: Long = 0,
    val downloadTotal: Long = 0,
    val uploadHistory: List<Long> = emptyList(),
    val downloadHistory: List<Long> = emptyList(),
    val showClashMode: Boolean = false,
    val clashMode: String = "Rule",
    val httpProxyEnabled: Boolean = false,
    val httpProxyPort: Int = 0,
    val profiles: List<ProfileInfo> = emptyList(),
    val visibleCards: Set<String> = setOf(
        "status", "profile", "connections",
        "upload", "download", "clash_mode", "http_proxy"
    )
)

data class ProfileInfo(
    val id: Long,
    val name: String,
    val type: String
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    // Inject repositories here
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            // TODO: Load actual data from repositories
            _uiState.update {
                it.copy(
                    isLoading = false,
                    profile = ProfileInfo(1, "Default Profile", "local"),
                    profileName = "Default Profile",
                    showClashMode = true
                )
            }
        }
    }

    fun setClashMode(mode: String) {
        viewModelScope.launch {
            // TODO: Set clash mode
            _uiState.update { it.copy(clashMode = mode) }
        }
    }

    fun toggleHttpProxy() {
        viewModelScope.launch {
            // TODO: Toggle HTTP proxy
            _uiState.update { it.copy(httpProxyEnabled = !it.httpProxyEnabled) }
        }
    }

    fun selectProfile(profile: ProfileInfo) {
        viewModelScope.launch {
            // TODO: Select profile
            _uiState.update {
                it.copy(
                    profile = profile,
                    profileName = profile.name
                )
            }
        }
    }

    fun updateVisibleCards(cards: Set<String>) {
        _uiState.update { it.copy(visibleCards = cards) }
    }
}
