package com.example.jampez

import android.app.Application
import org.koin.core.context.startKoin
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {

        }
    }
}