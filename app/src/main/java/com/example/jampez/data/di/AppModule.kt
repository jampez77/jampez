package com.example.jampez.data.di

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.example.jampez.utils.ConnectionLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { provideNetworkConnectionListener(androidContext()) }
    single { provideMaterialAlertDialogBuilder(androidContext(), get()) }
    single<AlertDialog>{ activityContext -> provideAlertDialog(activityContext.get(), get()) }
    single<Snackbar> { activityView -> provideSnackbar(activityView.get(), get(), get()) }
}

fun provideNetworkConnectionListener(context: Context) = ConnectionLiveData(context)

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

fun provideSnackbar(view: View, text: String, length: Int) = Snackbar.make(view, text, length)