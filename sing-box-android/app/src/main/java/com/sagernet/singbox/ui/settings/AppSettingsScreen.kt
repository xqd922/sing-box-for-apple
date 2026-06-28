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
import com.sagernet.singbox.viewmodel.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
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
                .padding(padding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                SettingsSection {
                    SettingsToggleItem(
                        icon = Icons.Default.DarkMode,
                        title = "Dark Mode",
                        checked = uiState.isDarkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) }
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        checked = uiState.notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) }
                    )
                    SettingsToggleItem(
                        icon = Icons.Default.Autorenew,
                        title = "Auto Update Profiles",
                        checked = uiState.autoUpdateProfiles,
                        onCheckedChange = { viewModel.setAutoUpdateProfiles(it) }
                    )
                }
            }

            item {
                SettingsSection(title = "Language") {
                    // Language selector
                    SettingsNavigationItem(
                        icon = Icons.Default.Language,
                        title = "Language",
                        onClick = { /* TODO: Language selector */ }
                    )
                }
            }
        }
    }
}
