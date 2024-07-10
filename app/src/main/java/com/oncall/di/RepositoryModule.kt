package com.oncall.di

import com.oncall.repository.INotificationRepository
import com.oncall.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindNotificationRepository(impl: NotificationRepository): INotificationRepository
}
