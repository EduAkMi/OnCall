package com.oncall.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : INotificationRepository {
    private val _isRunningFlow = MutableSharedFlow<Boolean>()
    override val isRunningFlow = _isRunningFlow.asSharedFlow()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        repositoryScope.launch {
            dataStore.data.collect { preferences ->
                _isRunningFlow.emit(preferences[IS_RUNNING_KEY] ?: false)
            }
        }
    }

    override suspend fun getIsRunning(): Boolean {
        return dataStore.data.first()[IS_RUNNING_KEY] ?: false
    }

    override suspend fun setIsRunning(isRunning: Boolean) {
        dataStore.edit { settings ->
            settings[IS_RUNNING_KEY] = isRunning
        }
    }

    companion object {
        private val IS_RUNNING_KEY = booleanPreferencesKey("is_running")
    }
}
