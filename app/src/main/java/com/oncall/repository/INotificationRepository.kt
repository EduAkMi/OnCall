package com.oncall.repository

import kotlinx.coroutines.flow.SharedFlow

interface INotificationRepository {
    val isRunningFlow: SharedFlow<Boolean>
    suspend fun getIsRunning(): Boolean
    suspend fun setIsRunning(isRunning: Boolean)
}
