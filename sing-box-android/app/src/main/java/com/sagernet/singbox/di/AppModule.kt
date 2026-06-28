package com.sagernet.singbox.di

import android.content.Context
import com.sagernet.singbox.core.database.AppDatabase
import com.sagernet.singbox.core.database.ProfileDao
import com.sagernet.singbox.core.database.RemoteServerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideProfileDao(database: AppDatabase): ProfileDao {
        return database.profileDao()
    }

    @Provides
    @Singleton
    fun provideRemoteServerDao(database: AppDatabase): RemoteServerDao {
        return database.remoteServerDao()
    }
}
