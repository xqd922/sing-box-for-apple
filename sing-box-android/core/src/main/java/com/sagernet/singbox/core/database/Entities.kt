package com.sagernet.singbox.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "profiles")
data class Profile(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val type: ProfileType = ProfileType.LOCAL,
    val path: String,
    val remoteUrl: String? = null,
    val autoUpdate: Boolean = false,
    val autoUpdateInterval: Int = 0,
    val lastUpdated: Date? = null,
    val userAgent: String? = null
)

enum class ProfileType {
    LOCAL,
    ICLOUD,
    REMOTE
}

@Entity(tableName = "remote_servers")
data class RemoteServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val port: Int = 1080,
    val secret: String? = null,
    val isDefault: Boolean = false
)
