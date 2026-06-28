package com.sagernet.singbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TailscaleEndpoint(
    val endpointTag: String,
    val userGroups: List<UserGroup>
)

data class UserGroup(
    val name: String,
    val peers: List<TailscalePeer>
)

data class TailscalePeer(
    val stableID: String,
    val hostName: String,
    val tailscaleIPs: List<String>,
    val sshHostKeys: List<String>,
    val online: Boolean
)

data class USBIPServer(
    val serverTag: String
)

data class ToolsUiState(
    val isLoading: Boolean = true,
    val tailscaleEndpoints: List<TailscaleEndpoint> = emptyList(),
    val usbipServers: List<USBIPServer> = emptyList(),
    val crashReportCount: Int = 0,
    val oomReportCount: Int = 0,
    val remoteServer: Any? = null
)

@HiltViewModel
class ToolsViewModel @Inject constructor(
    // Inject repositories here
) : ViewModel() {

    private val _uiState = MutableStateFlow(ToolsUiState())
    val uiState: StateFlow<ToolsUiState> = _uiState.asStateFlow()

    var taiwanFlagAvailable: String = "Loading..."
        private set

    init {
        loadTools()
    }

    private fun loadTools() {
        viewModelScope.launch {
            // TODO: Load actual data
            _uiState.update {
                it.copy(
                    isLoading = false,
                    crashReportCount = 0,
                    oomReportCount = 0
                )
            }
        }
    }

    fun checkTaiwanFlagAvailability() {
        viewModelScope.launch {
            // TODO: Check Taiwan flag availability
            taiwanFlagAvailable = "true"
        }
    }
}
