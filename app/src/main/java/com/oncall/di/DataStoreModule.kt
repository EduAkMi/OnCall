package com.oncall.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val NOTIFICATION_SETTINGS = "notification_settings"

val Context.notificationDataStore: DataStore<Preferences> by preferencesDataStore(
    name = NOTIFICATION_SETTINGS
)

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
    @Singleton
    @Provides
    fun provideNotificationDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return appContext.notificationDataStore
    }
}
