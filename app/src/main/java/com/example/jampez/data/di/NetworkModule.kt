package com.example.jampez.data.di

import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.utils.constants.HOST
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { provideOkHttpClient() }
    single { providesRetrofit(get()) }
}

fun provideOkHttpClient() = OkHttpClient.Builder()
    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

fun providesRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
    .baseUrl(HOST)
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient)
    .build()
    .create(DummyJsonApi::class.java)
