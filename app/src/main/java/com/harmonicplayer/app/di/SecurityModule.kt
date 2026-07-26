package com.harmonicplayer.app.di

import com.harmonicplayer.app.data.local.SecureStorageManager
import com.harmonicplayer.app.data.local.SecureStorageManagerImpl
import com.harmonicplayer.app.util.PermissionManager
import com.harmonicplayer.app.util.PermissionManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindSecureStorageManager(
        impl: SecureStorageManagerImpl
    ): SecureStorageManager

    @Binds
    @Singleton
    abstract fun bindPermissionManager(
        impl: PermissionManagerImpl
    ): PermissionManager
}
