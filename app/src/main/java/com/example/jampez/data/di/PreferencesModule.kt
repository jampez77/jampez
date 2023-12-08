package com.example.jampez.data.di

import android.app.Application
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.jampez.utils.constants.PREFERENCES_FILE_KEY
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val preferencesModule = module {
    single { provideSettingsPreferences(androidApplication()) }
}

private fun provideSettingsPreferences(app: Application) = EncryptedSharedPreferences.create(
    PREFERENCES_FILE_KEY,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    app.applicationContext,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)