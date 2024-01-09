package com.example.jampez.data.di

import android.app.Activity
import com.example.jampez.MainActivity
import com.example.jampez.R
import com.example.jampez.utils.ConnectionLiveData
import com.example.jampez.utils.constants.snackbarText
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import org.koin.dsl.module

val appModule = module {
    single<ConnectionLiveData> { activity -> provideNetworkConnectionListener(activity.get()) }
    single<Snackbar> { view -> provideSnackbar(view.get()) }
}

fun provideNetworkConnectionListener(activity: MainActivity) = ConnectionLiveData(activity)

fun provideSnackbar(activity: Activity) =
    Snackbar.make(activity.findViewById(R.id.nav_host_fragment), snackbarText, LENGTH_LONG)