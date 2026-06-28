package com.sagernet.singbox.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoreSettingsScreen(navController: NavController) {
    var logLevel by remember { mutableStateOf("info") }
    var memoryLimit by remember { mutableStateOf("256 MB") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Core") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection(title = "General") {
                    SettingsInfoItem(
                        icon = Icons.Default.BugReport,
                        title = "Log Level",
                        value = logLevel
                    )
                    SettingsInfoItem(
                        icon = Icons.Default.Memory,
                        title = "Memory Limit",
                        value = memoryLimit
                    )
                }
            }

            item {
                SettingsSection(title = "Configuration") {
                    SettingsToggleItem(
                        icon = Icons.Default.Update,
                        title = "Auto Update",
                        checked = true,
                        onCheckedChange = { /* TODO */ }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Refresh,
                        title = "Update Now",
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacketTunnelSettingsScreen(navController: NavController) {
    var includeAllNetworks by remember { mutableStateOf(false) }
    var systemProxyEnabled by remember { mutableStateOf(true) }
    var excludeDefaultRoute by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Packet Tunnel") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection(title = "Routing") {
                    SettingsToggleItem(
                        icon = Icons.Default.Public,
                        title = "Include All Networks",
                        checked = includeAllNetworks,
                        onCheckedChange = { includeAllNetworks = it }
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Proxy,
                        title = "System Proxy",
                        checked = systemProxyEnabled,
                        onCheckedChange = { systemProxyEnabled = it }
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Route,
                        title = "Exclude Default Route",
                        checked = excludeDefaultRoute,
                        onCheckedChange = { excludeDefaultRoute = it }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnDemandRulesScreen(navController: NavController) {
    var enabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("On Demand Rules") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection {
                    SettingsToggleItem(
                        icon = Icons.Default.ToggleOn,
                        title = "Enable On Demand",
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
            }

            if (enabled) {
                item {
                    SettingsSection(title = "Rules") {
                        SettingsNavigationItem(
                            icon = Icons.Default.Wifi,
                            title = "Connect On Demand",
                            onClick = { /* TODO */ }
                        )
                        SettingsNavigationItem(
                            icon = Icons.Default.SignalWifiOff,
                            title = "Disconnect On Demand",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileOverrideScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Override") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection(title = "DNS") {
                    SettingsNavigationItem(
                        icon = Icons.Default.Dns,
                        title = "DNS Override",
                        onClick = { /* TODO */ }
                    )
                }
            }

            item {
                SettingsSection(title = "Routing") {
                    SettingsNavigationItem(
                        icon = Icons.Default.Route,
                        title = "Route Override",
                        onClick = { /* TODO */ }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlScreen(navController: NavController) {
    var enabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Remote Control") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection {
                    SettingsToggleItem(
                        icon = Icons.Default.SettingsRemote,
                        title = "Enable Remote Control",
                        checked = enabled,
                        onCheckedChange = { enabled = it }
                    )
                }
            }

            if (enabled) {
                item {
                    SettingsSection(title = "Servers") {
                        SettingsNavigationItem(
                            icon = Icons.Default.Add,
                            title = "Add Server",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SponsorsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sponsors") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection {
                    // TODO: Add sponsors list
                }
            }
        }
    }
}
