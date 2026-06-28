package com.sagernet.singbox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardManagementSheet(
    visibleCards: Set<String>,
    onDismiss: () -> Unit,
    onCardsChanged: (Set<String>) -> Unit
) {
    val cards = listOf(
        "status" to "Status",
        "profile" to "Profile",
        "connections" to "Connections",
        "upload" to "Upload Traffic",
        "download" to "Download Traffic",
        "clash_mode" to "Clash Mode",
        "http_proxy" to "HTTP Proxy"
    )

    var selectedCards by remember { mutableStateOf(visibleCards) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Dashboard Items",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(cards) { (id, title) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = id in selectedCards,
                            onCheckedChange = { checked ->
                                selectedCards = if (checked) {
                                    selectedCards + id
                                } else {
                                    selectedCards - id
                                }
                                onCardsChanged(selectedCards)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePickerSheet(
    profiles: List<ProfileInfo>,
    currentProfileId: Long?,
    onDismiss: () -> Unit,
    onSelect: (ProfileInfo) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Select Profile",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(profiles) { profile ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = profile.id == currentProfileId,
                            onClick = {
                                onSelect(profile)
                                onDismiss()
                            }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

data class ProfileInfo(
    val id: Long,
    val name: String,
    val type: String
)
