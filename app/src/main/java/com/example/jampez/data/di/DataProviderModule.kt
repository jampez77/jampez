package com.example.jampez.data.di

import com.example.jampez.data.repositories.IConnectionRepository
import com.example.jampez.data.repositories.ConnectionRepository
import com.example.jampez.data.repositories.ITodoRepository
import com.example.jampez.data.repositories.IUserRepository
import com.example.jampez.data.repositories.TodoRepository
import com.example.jampez.data.repositories.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataProviderModule = module {
    singleOf(::UserRepository) { bind<IUserRepository>() }
    singleOf(::TodoRepository) { bind<ITodoRepository>() }
    singleOf(::ConnectionRepository) { bind<IConnectionRepository>() }
}