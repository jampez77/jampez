package com.example.jampez.data.di

import com.example.jampez.MainViewModel
import com.example.jampez.features.login.LoginViewModel
import com.example.jampez.features.todo.TodoViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::MainViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::TodoViewModel)
}