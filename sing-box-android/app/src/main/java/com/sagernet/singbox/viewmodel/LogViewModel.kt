package com.sagernet.singbox.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sagernet.singbox.ui.logs.LogEntry
import com.sagernet.singbox.ui.logs.LogLevel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogUiState(
    val isLoading: Boolean = true,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val isServiceStarted: Boolean = false,
    val isPaused: Boolean = false,
    val isSearching: Boolean = false,
    val searchText: String = "",
    val selectedLogLevel: LogLevel? = null,
    val logs: List<LogEntry> = emptyList(),
    val visibleLogs: List<LogEntry> = emptyList()
)

@HiltViewModel
class LogViewModel @Inject constructor(
    // Inject repositories here
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogUiState())
    val uiState: StateFlow<LogUiState> = _uiState.asStateFlow()

    private val allLogs = mutableListOf<LogEntry>()
    private var nextLogId = 0L

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            // TODO: Connect to log server and load logs
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isServiceStarted = true
                )
            }
        }
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun setSearchText(text: String) {
        _uiState.update { state ->
            state.copy(
                searchText = text,
                visibleLogs = filterLogs(allLogs, text, state.selectedLogLevel)
            )
        }
    }

    fun setLogLevel(level: LogLevel?) {
        _uiState.update { state ->
            state.copy(
                selectedLogLevel = level,
                visibleLogs = filterLogs(allLogs, state.searchText, level)
            )
        }
    }

    fun clearLogs() {
        allLogs.clear()
        _uiState.update { state ->
            state.copy(
                logs = emptyList(),
                visibleLogs = emptyList()
            )
        }
    }

    fun copyToClipboard() {
        // TODO: Implement clipboard copy
    }

    fun saveToFile() {
        // TODO: Implement file save
    }

    fun shareLogs() {
        // TODO: Implement share
    }

    fun addLog(level: LogLevel, message: String) {
        val entry = LogEntry(
            id = nextLogId++,
            level = level,
            message = message
        )
        allLogs.add(entry)

        _uiState.update { state ->
            state.copy(
                logs = allLogs.toList(),
                visibleLogs = filterLogs(allLogs, state.searchText, state.selectedLogLevel)
            )
        }
    }

    private fun filterLogs(
        logs: List<LogEntry>,
        searchText: String,
        logLevel: LogLevel?
    ): List<LogEntry> {
        return logs.filter { entry ->
            val matchesSearch = searchText.isEmpty() ||
                entry.message.contains(searchText, ignoreCase = true)
            val matchesLevel = logLevel == null || entry.level == logLevel
            matchesSearch && matchesLevel
        }
    }
}
