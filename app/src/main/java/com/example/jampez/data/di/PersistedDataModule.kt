package com.example.jampez.data.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.jampez.data.AppDatabase
import com.example.jampez.utils.constants.PASSPHRASE
import com.example.jampez.utils.constants.PREFERENCES_FILE_KEY
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val persistedDataModule = module {
    single { provideSettingsPreferences(androidApplication()) }
    single { provideRoomDatabase(androidContext(), get()) }
    single { provideTodoDao(get()) }
}

fun provideSettingsPreferences(app: Application) = EncryptedSharedPreferences.create(
    PREFERENCES_FILE_KEY,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    app.applicationContext,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

fun provideRoomDatabase(context: Context, sharedPreferences: SharedPreferences): AppDatabase {
    val userPassPhrase = sharedPreferences.getString(PASSPHRASE, "")
    val factory = SupportFactory(SQLiteDatabase.getBytes(userPassPhrase?.toCharArray()))
    return Room.databaseBuilder(context, AppDatabase::class.java, "jampez.db").openHelperFactory(factory).build()
}

private fun provideTodoDao(appDatabase: AppDatabase) = appDatabase.todoDao()