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

data class SettingsUiState(
    val isLoading: Boolean = true,
    val isDarkMode: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val autoUpdateProfiles: Boolean = true,
    val language: String = "en"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // Inject repositories here
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load settings from DataStore
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save dark mode setting
            _uiState.update { it.copy(isDarkMode = enabled) }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save notifications setting
            _uiState.update { it.copy(notificationsEnabled = enabled) }
        }
    }

    fun setAutoUpdateProfiles(enabled: Boolean) {
        viewModelScope.launch {
            // TODO: Save auto update setting
            _uiState.update { it.copy(autoUpdateProfiles = enabled) }
        }
    }
}
