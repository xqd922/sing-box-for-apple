package com.sagernet.singbox.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sagernet.singbox.ui.VpnState
import com.sagernet.singbox.viewmodel.DashboardViewModel
import com.sagernet.singbox.theme.IOSColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCardManagement by remember { mutableStateOf(false) }
    var showProfilePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                actions = {
                    // Menu button
                    IconButton(onClick = { showCardManagement = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Others"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.profile == null) {
            // No profile installed
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                InstallProfileButton(
                    onClick = { /* TODO: Install profile */ }
                )
            }
        } else {
            // Active Dashboard
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Status Card (Full width)
                item(span = { GridItemSpan(2) }) {
                    StatusCard(
                        vpnState = uiState.vpnState,
                        uptime = uiState.uptime
                    )
                }

                // Profile Card
                item {
                    ProfileCard(
                        profileName = uiState.profileName,
                        onClick = { showProfilePicker = true }
                    )
                }

                // Connections Card
                item {
                    ConnectionsCard(
                        count = uiState.connectionsCount,
                        onClick = { /* TODO: Show connections */ }
                    )
                }

                // Upload Traffic Card
                item {
                    UploadTrafficCard(
                        speed = uiState.uploadSpeed,
                        total = uiState.uploadTotal,
                        history = uiState.uploadHistory
                    )
                }

                // Download Traffic Card
                item {
                    DownloadTrafficCard(
                        speed = uiState.downloadSpeed,
                        total = uiState.downloadTotal,
                        history = uiState.downloadHistory
                    )
                }

                // Clash Mode Card (if applicable)
                if (uiState.showClashMode) {
                    item(span = { GridItemSpan(2) }) {
                        ClashModeCard(
                            currentMode = uiState.clashMode,
                            onModeChange = { viewModel.setClashMode(it) }
                        )
                    }
                }

                // HTTP Proxy Card
                item(span = { GridItemSpan(2) }) {
                    HTTPProxyCard(
                        enabled = uiState.httpProxyEnabled,
                        port = uiState.httpProxyPort,
                        onToggle = { viewModel.toggleHttpProxy() }
                    )
                }
            }
        }
    }

    // Card Management Sheet
    if (showCardManagement) {
        CardManagementSheet(
            visibleCards = uiState.visibleCards,
            onDismiss = { showCardManagement = false },
            onCardsChanged = { viewModel.updateVisibleCards(it) }
        )
    }

    // Profile Picker Sheet
    if (showProfilePicker) {
        ProfilePickerSheet(
            profiles = uiState.profiles,
            currentProfileId = uiState.profile?.id,
            onDismiss = { showProfilePicker = false },
            onSelect = { viewModel.selectProfile(it) }
        )
    }
}

@Composable
fun StatusCard(
    vpnState: VpnState,
    uptime: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (vpnState) {
                VpnState.CONNECTED -> IOSColors.Green.copy(alpha = 0.12f)
                VpnState.CONNECTING -> IOSColors.Orange.copy(alpha = 0.12f)
                VpnState.DISCONNECTING -> IOSColors.Orange.copy(alpha = 0.12f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status icon
                Icon(
                    imageVector = when (vpnState) {
                        VpnState.CONNECTED -> Icons.Filled.CheckCircle
                        VpnState.CONNECTING -> Icons.Filled.Sync
                        VpnState.DISCONNECTING -> Icons.Filled.Sync
                        else -> Icons.Filled.StopCircle
                    },
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = when (vpnState) {
                        VpnState.CONNECTED -> IOSColors.Green
                        VpnState.CONNECTING -> IOSColors.Orange
                        VpnState.DISCONNECTING -> IOSColors.Orange
                        else -> IOSColors.Red
                    }
                )

                // Status text
                Text(
                    text = when (vpnState) {
                        VpnState.CONNECTED -> "Started"
                        VpnState.CONNECTING -> "Starting"
                        VpnState.DISCONNECTED -> "Stopped"
                        VpnState.DISCONNECTING -> "Stopping"
                        VpnState.REASSERTING -> "Reasserting"
                    },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Uptime (only when connected)
            if (vpnState == VpnState.CONNECTED && !uptime.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uptime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    profileName: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Profile",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = profileName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun ConnectionsCard(
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Connections",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun UploadTrafficCard(
    speed: Long,
    total: Long,
    history: List<Long>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = IOSColors.Blue
                )
                Text(
                    text = formatSpeed(speed),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Traffic chart
            TrafficLineChart(
                data = history,
                color = IOSColors.Blue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ${formatBytes(total)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DownloadTrafficCard(
    speed: Long,
    total: Long,
    history: List<Long>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = IOSColors.Green
                )
                Text(
                    text = formatSpeed(speed),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Traffic chart
            TrafficLineChart(
                data = history,
                color = IOSColors.Green,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Total: ${formatBytes(total)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ClashModeCard(
    currentMode: String,
    onModeChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Clash Mode",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Rule", "Global", "Direct").forEach { mode ->
                    FilterChip(
                        selected = currentMode == mode,
                        onClick = { onModeChange(mode) },
                        label = { Text(mode) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun HTTPProxyCard(
    enabled: Boolean,
    port: Int,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "HTTP Proxy",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (enabled) {
                    Text(
                        text = "Port: $port",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun InstallProfileButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Install Profile")
    }
}

@Composable
fun TrafficLineChart(
    data: List<Long>,
    color: Color,
    modifier: Modifier = Modifier
) {
    // Simple line chart implementation
    // In production, use a proper chart library
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
    ) {
        if (data.isNotEmpty()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw chart lines
                // This is a simplified version
            }
        }
    }
}

// Utility functions
fun formatSpeed(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B/s"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB/s"
        bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB/s"
        else -> "${bytes / (1024 * 1024 * 1024)} GB/s"
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
        else -> "${"%.2f".format(bytes / (1024.0 * 1024.0 * 1024.0))} GB"
    }
}
