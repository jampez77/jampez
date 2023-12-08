package com.example.jampez

import android.app.Application
import com.example.jampez.data.di.appModule
import com.example.jampez.data.di.dataProviderModule
import com.example.jampez.data.di.dbModule
import com.example.jampez.data.di.glideModule
import com.example.jampez.data.di.networkModule
import com.example.jampez.data.di.preferencesModule
import com.example.jampez.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            modules(
                modules = listOf(
                    viewModelModule,
                    dbModule,
                    networkModule,
                    dataProviderModule,
                    preferencesModule,
                    glideModule,
                    appModule
                )
            )
        }
    }
}