package com.example.jampez.data.di

import com.example.jampez.MainViewModel
import com.example.jampez.data.repositories.IUserRepository
import com.example.jampez.data.repositories.TodoRepository
import com.example.jampez.data.repositories.UserRepository
import com.example.jampez.features.login.LoginViewModel
import com.example.jampez.features.todo.TodoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel() }
    viewModel { LoginViewModel() }
    viewModel { TodoViewModel() }
}