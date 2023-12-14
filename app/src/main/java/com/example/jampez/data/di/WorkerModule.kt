package com.example.jampez.data.di

import android.content.Context
import androidx.work.WorkManager
import com.example.jampez.utils.RemoveUserFilesWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    single { providesWorkManager(androidContext()) }
    worker { RemoveUserFilesWorker(androidContext(), get()) }
}

fun providesWorkManager(context: Context) = WorkManager.getInstance(context)