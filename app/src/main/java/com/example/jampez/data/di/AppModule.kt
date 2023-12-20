package com.example.jampez.data.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.jampez.MainActivity
import com.example.jampez.R
import com.example.jampez.utils.ConnectionLiveData
import com.example.jampez.utils.constants.snackbarText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<ConnectionLiveData> { activity -> provideNetworkConnectionListener(activity.get()) }
    single { provideMaterialAlertDialogBuilder(androidContext(), get()) }
    single<AlertDialog>{ context -> provideAlertDialog(context.get(), get()) }
    single<Snackbar> { view -> provideSnackbar(view.get()) }
}

fun provideNetworkConnectionListener(activity: MainActivity) = ConnectionLiveData(activity)

fun provideMaterialAlertDialogBuilder(context: Context, style: Int) = MaterialAlertDialogBuilder(context, style)

fun provideAlertDialog(
    context: Context,
    style: Int
): AlertDialog {
    val builder = MaterialAlertDialogBuilder(context, style)
    val alert = builder.create()
    alert.window?.attributes?.windowAnimations = style
    return alert
}

fun provideSnackbar(activity: Activity) =
    Snackbar.make(activity.findViewById(R.id.nav_host_fragment), snackbarText, LENGTH_LONG)