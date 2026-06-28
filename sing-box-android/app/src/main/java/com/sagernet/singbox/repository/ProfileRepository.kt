package com.sagernet.singbox.repository

import com.sagernet.singbox.core.database.Profile
import com.sagernet.singbox.core.database.ProfileDao
import com.sagernet.singbox.core.database.ProfileType
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val appContext: android.content.Context
) {
    fun getAllProfiles(): Flow<List<Profile>> {
        return profileDao.getAllProfiles()
    }

    suspend fun getProfileById(id: Long): Profile? {
        return profileDao.getProfileById(id)
    }

    suspend fun insertProfile(profile: Profile): Long {
        return profileDao.insert(profile)
    }

    suspend fun updateProfile(profile: Profile) {
        profileDao.update(profile)
    }

    suspend fun deleteProfile(profile: Profile) {
        profileDao.delete(profile)
    }

    suspend fun importProfile(name: String, content: String, type: ProfileType = ProfileType.LOCAL): Profile {
        // Save configuration file
        val configDir = File(appContext.filesDir, "profiles")
        configDir.mkdirs()

        val fileName = "${System.currentTimeMillis()}.json"
        val configFile = File(configDir, fileName)
        configFile.writeText(content)

        // Create profile record
        val profile = Profile(
            name = name,
            type = type,
            path = configFile.absolutePath
        )

        val id = insertProfile(profile)
        return profile.copy(id = id)
    }

    suspend fun importFromUrl(name: String, url: String): Profile {
        // TODO: Download configuration from URL
        val content = downloadConfig(url)
        return importProfile(name, content, ProfileType.REMOTE)
    }

    private suspend fun downloadConfig(url: String): String {
        // TODO: Implement HTTP download
        // This should use OkHttp or similar
        throw NotImplementedError("HTTP download not implemented yet")
    }

    fun getProfilePath(profile: Profile): File {
        return File(profile.path)
    }

    fun readProfileContent(profile: Profile): String {
        return File(profile.path).readText()
    }

    suspend fun updateProfileContent(profile: Profile, content: String) {
        File(profile.path).writeText(content)
        profile.lastUpdated = java.util.Date()
        updateProfile(profile)
    }
}
