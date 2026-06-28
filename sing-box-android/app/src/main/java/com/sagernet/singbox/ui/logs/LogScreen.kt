package com.sagernet.singbox.ui.logs

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sagernet.singbox.viewmodel.LogViewModel
import com.sagernet.singbox.theme.IOSColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(
    viewModel: LogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Logs",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    // Search button
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }

                    // Pause/Resume button
                    IconButton(onClick = { viewModel.togglePause() }) {
                        Icon(
                            imageVector = if (uiState.isPaused)
                                Icons.Default.PlayArrow
                            else
                                Icons.Default.Pause,
                            contentDescription = if (uiState.isPaused) "Resume" else "Pause"
                        )
                    }

                    // Menu button
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Others"
                            )
                        }

                        // Dropdown menu
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            // Log Level submenu
                            SubMenu(
                                label = "Log Level",
                                icon = Icons.Default.Tune,
                                content = {
                                    DropdownMenuItem(
                                        text = { Text("Default") },
                                        onClick = {
                                            viewModel.setLogLevel(null)
                                            showMenu = false
                                        },
                                        leadingIcon = {
                                            if (uiState.selectedLogLevel == null) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    null,
                                                    tint = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    )
                                    LogLevel.values().forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level.name) },
                                            onClick = {
                                                viewModel.setLogLevel(level)
                                                showMenu = false
                                            },
                                            leadingIcon = {
                                                if (uiState.selectedLogLevel == level) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        null,
                                                        tint = MaterialTheme.colorScheme.primary
                                                    )
                                                }
                                            }
                                        )
                                    }
                                }
                            )

                            // Save submenu
                            SubMenu(
                                label = "Save",
                                icon = Icons.Default.Save,
                                content = {
                                    DropdownMenuItem(
                                        text = { Text("To Clipboard") },
                                        onClick = {
                                            viewModel.copyToClipboard()
                                            showMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.ContentCopy, null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("To File") },
                                        onClick = {
                                            viewModel.saveToFile()
                                            showMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.SaveAlt, null)
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Share") },
                                        onClick = {
                                            viewModel.shareLogs()
                                            showMenu = false
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Share, null)
                                        }
                                    )
                                }
                            )

                            Divider()

                            // Clear Logs
                            DropdownMenuItem(
                                text = { Text("Clear Logs") },
                                onClick = {
                                    viewModel.clearLogs()
                                    showMenu = false
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            AnimatedVisibility(
                visible = showSearch,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SearchBar(
                    query = searchText,
                    onQueryChange = {
                        searchText = it
                        viewModel.setSearchText(it)
                    },
                    onSearch = { /* Search */ },
                    active = false,
                    onActiveChange = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search logs") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = {
                                searchText = ""
                                viewModel.setSearchText("")
                            }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = SearchBarDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) { }
            }

            // Log content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                if (uiState.logs.isEmpty()) {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (uiState.isConnecting) {
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Connecting...",
                                    color = Color.White
                                )
                            } else if (!uiState.isServiceStarted) {
                                Text(
                                    text = "Service not started",
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = "Empty logs",
                                    color = Color.White
                                )
                            }
                        }
                    }
                } else {
                    // Log list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = uiState.visibleLogs,
                            key = { it.id }
                        ) { logEntry ->
                            LogEntryItem(
                                logEntry = logEntry,
                                searchText = searchText
                            )
                        }
                    }

                    // Auto scroll to bottom
                    LaunchedEffect(uiState.visibleLogs.size) {
                        if (!uiState.isPaused && uiState.visibleLogs.isNotEmpty()) {
                            listState.animateScrollToItem(uiState.visibleLogs.size - 1)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LogEntryItem(
    logEntry: LogEntry,
    searchText: String
) {
    val textColor = when (logEntry.level) {
        LogLevel.TRACE -> Color(0xFF888888)
        LogLevel.DEBUG -> Color(0xFF888888)
        LogLevel.INFO -> Color(0xFFFFFFFF)
        LogLevel.WARN -> IOSColors.Orange
        LogLevel.ERROR -> IOSColors.Red
        LogLevel.FATAL -> IOSColors.Red
    }

    val backgroundColor = if (searchText.isNotEmpty() &&
        logEntry.message.contains(searchText, ignoreCase = true)) {
        IOSColors.Yellow.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    Text(
        text = logEntry.message,
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            lineHeight = 14.sp
        ),
        color = textColor,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 1.dp, horizontal = 4.dp)
    )
}

@Composable
fun SubMenu(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        DropdownMenuItem(
            text = { Text(label) },
            onClick = { expanded = true },
            leadingIcon = { Icon(icon, null) },
            trailingIcon = { Icon(Icons.Default.ChevronRight, null) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            content()
        }
    }
}

data class LogEntry(
    val id: Long,
    val level: LogLevel,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogLevel {
    TRACE,
    DEBUG,
    INFO,
    WARN,
    ERROR,
    FATAL
}
