package com.sagernet.singbox.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sagernet.singbox.viewmodel.SettingsViewModel
import com.sagernet.singbox.theme.IOSColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "settings_main"
    ) {
        composable("settings_main") {
            SettingsMainScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("app") {
            AppSettingsScreen(navController = navController)
        }
        composable("core") {
            CoreSettingsScreen(navController = navController)
        }
        composable("packet_tunnel") {
            PacketTunnelSettingsScreen(navController = navController)
        }
        composable("on_demand_rules") {
            OnDemandRulesScreen(navController = navController)
        }
        composable("profile_override") {
            ProfileOverrideScreen(navController = navController)
        }
        composable("remote_control") {
            RemoteControlScreen(navController = navController)
        }
        composable("sponsors") {
            SponsorsScreen(navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsMainScreen(
    navController: NavController,
    viewModel: SettingsViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
            // Main settings section
            item {
                SettingsSection {
                    SettingsNavigationItem(
                        icon = Icons.Default.Apps,
                        title = "App",
                        onClick = { navController.navigate("app") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Inventory2,
                        title = "Core",
                        onClick = { navController.navigate("core") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.AspectRatio,
                        title = "Packet Tunnel",
                        onClick = { navController.navigate("packet_tunnel") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.MenuOpen,
                        title = "On Demand Rules",
                        onClick = { navController.navigate("on_demand_rules") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Dashboard,
                        title = "Profile Override",
                        onClick = { navController.navigate("profile_override") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.SettingsRemote,
                        title = "Remote Control",
                        onClick = { navController.navigate("remote_control") }
                    )
                    SettingsNavigationItem(
                        icon = Icons.Default.Favorite,
                        title = "Sponsors",
                        onClick = { navController.navigate("sponsors") }
                    )
                }
            }

            // About section
            item {
                SettingsSection(title = "About") {
                    // Documentation
                    ClickableSettingsItem(
                        icon = Icons.Default.Description,
                        title = "Documentation",
                        onClick = { /* TODO: Open documentation */ }
                    )

                    // Source Code
                    ClickableSettingsItem(
                        icon = Icons.Default.Code,
                        title = "Source Code",
                        onClick = { /* TODO: Open source code */ }
                    )

                    // Rate on App Store
                    ClickableSettingsItem(
                        icon = Icons.Default.Star,
                        title = "Rate on the Play Store",
                        onClick = { /* TODO: Open Play Store */ }
                    )

                    // Version
                    SettingsInfoItem(
                        icon = Icons.Default.Info,
                        title = "Version",
                        value = "1.0.0"
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String? = null,
    content: @Composable () -> Unit
) {
    Column {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = IOSColors.Blue,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsNavigationItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = IOSColors.Blue
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Chevron
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = IOSColors.Gray3,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ClickableSettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = IOSColors.Blue
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = IOSColors.Blue
        )
    }
}

@Composable
fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = IOSColors.Blue
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Value
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = IOSColors.Blue
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        // Toggle
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
