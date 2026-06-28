package com.sagernet.singbox.ui.tools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sagernet.singbox.viewmodel.ToolsViewModel
import com.sagernet.singbox.theme.IOSColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(
    viewModel: ToolsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tools",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Endpoints Section
            if (uiState.tailscaleEndpoints.isNotEmpty()) {
                item {
                    SectionHeader("Endpoints")
                }
                items(uiState.tailscaleEndpoints) { endpoint ->
                    NavigationItem(
                        icon = Icons.Default.DeviceHub,
                        title = if (uiState.tailscaleEndpoints.size == 1)
                            "Tailscale"
                        else
                            "Tailscale: ${endpoint.endpointTag}",
                        onClick = { /* TODO: Open endpoint details */ }
                    )
                }
            }

            // Services Section
            if (uiState.usbipServers.isNotEmpty()) {
                item {
                    SectionHeader("Services")
                }
                items(uiState.usbipServers) { server ->
                    NavigationItem(
                        icon = Icons.Default.Usb,
                        title = if (uiState.usbipServers.size == 1)
                            "USB/IP"
                        else
                            "USB/IP: ${server.serverTag}",
                        onClick = { /* TODO: Open USB/IP details */ }
                    )
                }
            }

            // Network Section
            item {
                SectionHeader("Network")
            }
            item {
                NavigationItem(
                    icon = Icons.Default.NetworkCheck,
                    title = "Network Quality",
                    onClick = { /* TODO: Open network quality test */ }
                )
            }
            item {
                NavigationItem(
                    icon = Icons.Default.SwapHoriz,
                    title = "STUN Test",
                    onClick = { /* TODO: Open STUN test */ }
                )
            }

            // Debug Section (only for local device)
            if (uiState.remoteServer == null) {
                item {
                    SectionHeader("Debug")
                }
                item {
                    NavigationItem(
                        icon = Icons.Default.BugReport,
                        title = "Crash Report",
                        badge = uiState.crashReportCount,
                        onClick = { /* TODO: Open crash reports */ }
                    )
                }
                item {
                    NavigationItem(
                        icon = Icons.Default.Memory,
                        title = "OOM Report",
                        badge = uiState.oomReportCount,
                        onClick = { /* TODO: Open OOM reports */ }
                    )
                }
                item {
                    NavigationItem(
                        icon = Icons.Default.Flag,
                        title = "Taiwan Flag Available",
                        subtitle = viewModel.taiwanFlagAvailable,
                        onClick = { /* TODO: Check flag availability */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = IOSColors.Blue,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    badge: Int = 0,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title and subtitle
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Badge
        if (badge > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = badge.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        // Chevron
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = IOSColors.Gray3,
            modifier = Modifier.size(20.dp)
        )
    }
}
