package com.alperen.spendcraft.di

import com.alperen.spendcraft.core.common.NotificationTester
import com.alperen.spendcraft.notification.NotificationTesterImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    
    @Binds
    abstract fun bindNotificationTester(
        notificationTesterImpl: NotificationTesterImpl
    ): NotificationTester
}

