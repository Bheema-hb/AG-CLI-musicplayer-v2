package com.harmonicplayer.app.di

import android.content.Context
import com.harmonicplayer.app.util.AudioVisualizerEngine
import com.harmonicplayer.app.util.AudioVisualizerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VisualizerModule {

    @Provides
    @Singleton
    fun provideAudioVisualizerManager(
        @ApplicationContext context: Context
    ): AudioVisualizerManager {
        return AudioVisualizerEngine(context)
    }
}
