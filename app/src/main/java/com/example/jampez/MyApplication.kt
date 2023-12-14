package com.example.jampez

import android.app.Application
import com.example.jampez.data.di.appModule
import com.example.jampez.data.di.dataProviderModule
import com.example.jampez.data.di.persistedDataModule
import com.example.jampez.data.di.glideModule
import com.example.jampez.data.di.workerModule
import com.example.jampez.data.di.networkModule
import com.example.jampez.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@MyApplication)
            workManagerFactory()
            modules(
                modules = listOf(
                    viewModelModule,
                    persistedDataModule,
                    networkModule,
                    dataProviderModule,
                    glideModule,
                    appModule,
                    workerModule
                )
            )
        }
    }
}