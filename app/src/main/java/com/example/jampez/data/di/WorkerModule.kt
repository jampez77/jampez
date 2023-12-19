package com.example.jampez.data.di

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.jampez.utils.worker.SignOutUserWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val workerModule = module {
    single { providesWorkManager(androidContext()) }
    factory<OneTimeWorkRequest.Builder>{ builder -> providesOneTimeWorkRequest(builder.get()) }
    worker { SignOutUserWorker(androidContext(), get()) }
}

fun providesOneTimeWorkRequest(builder: OneTimeWorkRequest.Builder) = builder

fun providesWorkManager(context: Context) = WorkManager.getInstance(context)