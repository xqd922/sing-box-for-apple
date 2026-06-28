package com.sagernet.singbox.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.sagernet.singbox.ui.dashboard.DashboardScreen
import com.sagernet.singbox.ui.logs.LogScreen
import com.sagernet.singbox.ui.settings.SettingsScreen
import com.sagernet.singbox.ui.tools.ToolsScreen
import com.sagernet.singbox.viewmodel.MainViewModel
import com.sagernet.singbox.theme.IOSColors

sealed class NavigationPage(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Dashboard : NavigationPage(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Outlined.Dashboard,
        selectedIcon = Icons.Filled.Dashboard
    )
    object Logs : NavigationPage(
        route = "logs",
        title = "Logs",
        icon = Icons.Outlined.List,
        selectedIcon = Icons.Filled.List
    )
    object Tools : NavigationPage(
        route = "tools",
        title = "Tools",
        icon = Icons.Outlined.Build,
        selectedIcon = Icons.Filled.Build
    )
    object Settings : NavigationPage(
        route = "settings",
        title = "Settings",
        icon = Icons.Outlined.Settings,
        selectedIcon = Icons.Filled.Settings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val vpnState by viewModel.vpnState.collectAsState()
    val extensionProfile by viewModel.extensionProfile.collectAsState()
    val groupsCount by viewModel.groupsCount.collectAsState()
    val connectionsCount by viewModel.connectionsCount.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val pages = listOf(
        NavigationPage.Dashboard,
        NavigationPage.Logs,
        NavigationPage.Tools,
        NavigationPage.Settings
    )

    Scaffold(
        bottomBar = {
            Column {
                // Bottom Accessory Bar (Status Bar Pill or FAB)
                BottomAccessoryBar(
                    vpnState = vpnState,
                    extensionProfile = extensionProfile,
                    groupsCount = groupsCount,
                    connectionsCount = connectionsCount,
                    onStartClick = { viewModel.startVpn() },
                    onStopClick = { viewModel.stopVpn() },
                    onGroupsClick = { /* TODO: Show groups sheet */ },
                    onConnectionsClick = { /* TODO: Show connections sheet */ }
                )

                // Navigation Bar
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    pages.forEach { page ->
                        val selected = currentDestination?.hierarchy?.any { it.route == page.route } == true

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) page.selectedIcon else page.icon,
                                    contentDescription = page.title
                                )
                            },
                            label = { Text(page.title) },
                            selected = selected,
                            onClick = {
                                navController.navigate(page.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = IOSColors.Gray,
                                unselectedTextColor = IOSColors.Gray,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavigationPage.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(NavigationPage.Dashboard.route) {
                DashboardScreen()
            }
            composable(NavigationPage.Logs.route) {
                LogScreen()
            }
            composable(NavigationPage.Tools.route) {
                ToolsScreen()
            }
            composable(NavigationPage.Settings.route) {
                SettingsScreen()
            }
        }
    }
}

@Composable
fun BottomAccessoryBar(
    vpnState: VpnState,
    extensionProfile: ExtensionProfile?,
    groupsCount: Int,
    connectionsCount: Int,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onGroupsClick: () -> Unit,
    onConnectionsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        AnimatedContent(
            targetState = vpnState,
            transitionSpec = {
                slideInVertically { it } + fadeIn() togetherWith
                    slideOutVertically { -it } + fadeOut()
            }
        ) { state ->
            when (state) {
                VpnState.DISCONNECTED -> {
                    // FAB Start Button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        FloatingActionButton(
                            onClick = onStartClick,
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 2.dp,
                                pressedElevation = 4.dp
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Start",
                                modifier = Modifier.size(22.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                else -> {
                    // Status Bar Pill
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Status Text
                            StatusText(
                                vpnState = vpnState,
                                modifier = Modifier.weight(1f)
                            )

                            // Navigation Buttons
                            NavigationButtons(
                                groupsCount = groupsCount,
                                connectionsCount = connectionsCount,
                                onGroupsClick = onGroupsClick,
                                onConnectionsClick = onConnectionsClick
                            )

                            // Divider
                            Divider(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(1.dp),
                                color = MaterialTheme.colorScheme.outline
                            )

                            // Start/Stop Button
                            StartStopButton(
                                vpnState = vpnState,
                                onClick = if (vpnState == VpnState.CONNECTED) onStopClick else onStartClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusText(
    vpnState: VpnState,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (vpnState) {
        VpnState.DISCONNECTED -> "Stopped" to IOSColors.Gray
        VpnState.CONNECTING -> "Starting" to IOSColors.Orange
        VpnState.CONNECTED -> "Started" to IOSColors.Green
        VpnState.REASSERTING -> "Reasserting" to IOSColors.Orange
        VpnState.DISCONNECTING -> "Stopping" to IOSColors.Orange
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier,
        maxLines = 1
    )
}

@Composable
fun NavigationButtons(
    groupsCount: Int,
    connectionsCount: Int,
    onGroupsClick: () -> Unit,
    onConnectionsClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Groups Button
        if (groupsCount > 0) {
            IconButton(
                onClick = onGroupsClick,
                modifier = Modifier.size(32.dp)
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = groupsCount.toString(),
                                fontSize = 10.sp
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Dashboard,
                        contentDescription = "Groups",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Connections Button
        if (connectionsCount > 0) {
            IconButton(
                onClick = onConnectionsClick,
                modifier = Modifier.size(32.dp)
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = connectionsCount.toString(),
                                fontSize = 10.sp
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Connections",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun StartStopButton(
    vpnState: VpnState,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = if (vpnState == VpnState.CONNECTED)
                Icons.Filled.Stop
            else
                Icons.Filled.PlayArrow,
            contentDescription = if (vpnState == VpnState.CONNECTED) "Stop" else "Start",
            modifier = Modifier.size(20.dp),
            tint = if (vpnState == VpnState.CONNECTED)
                MaterialTheme.colorScheme.error
            else
                MaterialTheme.colorScheme.primary
        )
    }
}

enum class VpnState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    REASSERTING,
    DISCONNECTING
}

data class ExtensionProfile(
    val name: String,
    val status: VpnState,
    val uptime: String? = null
)
