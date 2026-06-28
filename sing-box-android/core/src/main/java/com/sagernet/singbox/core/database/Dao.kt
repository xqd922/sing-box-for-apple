package com.sagernet.singbox.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY `order` ASC")
    fun getAllProfiles(): Flow<List<Profile>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getProfileById(id: Long): Profile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: Profile): Long

    @Update
    suspend fun update(profile: Profile)

    @Delete
    suspend fun delete(profile: Profile)

    @Query("DELETE FROM profiles")
    suspend fun deleteAll()
}

@Dao
interface RemoteServerDao {
    @Query("SELECT * FROM remote_servers ORDER BY name ASC")
    fun getAllServers(): Flow<List<RemoteServer>>

    @Query("SELECT * FROM remote_servers WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultServer(): RemoteServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: RemoteServer): Long

    @Update
    suspend fun update(server: RemoteServer)

    @Delete
    suspend fun delete(server: RemoteServer)
}
