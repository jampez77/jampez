package com.example.jampez.data.di

import android.app.Application
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.example.jampez.R
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val glideModule = module {
    single { provideRequestOptions() }
    single { provideRequestManager(androidApplication(), get()) }
}

fun provideRequestManager(
    application: Application,
    requestOptions: RequestOptions
) = Glide.with(application).setDefaultRequestOptions(requestOptions)

fun provideRequestOptions() = RequestOptions()
        .placeholder(R.drawable.profile)
        .error(R.drawable.profile)