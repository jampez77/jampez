package com.example.jampez.data.repositories

import androidx.lifecycle.LiveData
import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.entities.ToDo

interface ITodoRepository {
    suspend fun fetchTodos(): ApiResponse<FetchTodos>
    suspend fun insertAllTodos(todos: List<ToDo>)
    suspend fun updateTodo(todo: ToDo)
    fun deleteTodos(userId: Long)
    fun getAllTodosLiveData() : LiveData<List<ToDo>>
    fun getAllTodos() : List<ToDo>
}