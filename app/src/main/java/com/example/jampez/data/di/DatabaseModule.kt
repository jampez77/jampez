package com.example.jampez.data.di

import android.content.Context
import androidx.room.Room
import com.example.jampez.data.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dbModule = module {
    single { provideRoomDatabase(androidContext()) }
    single { provideTodoDao(get()) }
}

fun provideRoomDatabase(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, "jampez.db")
    .fallbackToDestructiveMigration()
    .build()

private fun provideTodoDao(appDatabase: AppDatabase) = appDatabase.todoDao()