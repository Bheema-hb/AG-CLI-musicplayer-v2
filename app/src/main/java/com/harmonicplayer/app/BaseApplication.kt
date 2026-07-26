package com.harmonicplayer.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.harmonicplayer.app.util.LifecycleSecurityObserver
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BaseApplication : Application() {

    @Inject
    lateinit var lifecycleSecurityObserver: LifecycleSecurityObserver

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleSecurityObserver)
    }
}
