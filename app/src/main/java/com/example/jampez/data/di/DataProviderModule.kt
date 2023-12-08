package com.example.jampez.data.di

import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.data.dao.ToDoDao
import com.example.jampez.data.repositories.TodoRepository
import com.example.jampez.data.repositories.UserRepository
import org.koin.dsl.module

val dataProviderModule = module {
    single { providesUserRepository(get()) }
    single { providesTodoRepository(get(), get()) }
}

fun providesUserRepository(dummyJsonApi: DummyJsonApi) = UserRepository(dummyJsonApi)
fun providesTodoRepository(dummyJsonApi: DummyJsonApi, toDoDao: ToDoDao) = TodoRepository(dummyJsonApi, toDoDao)