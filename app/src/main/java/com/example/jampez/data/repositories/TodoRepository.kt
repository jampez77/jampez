package com.example.jampez.data.repositories

import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.dao.ToDoDao
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.models.User
import com.example.jampez.utils.constants.EMAIL
import com.example.jampez.utils.constants.FIRST_NAME
import com.example.jampez.utils.constants.ID
import com.example.jampez.utils.constants.IMAGE
import com.example.jampez.utils.constants.PASSWORD

class TodoRepository(private val dummyJsonApi: DummyJsonApi, private val toDoDao: ToDoDao) : ITodoRepository {

    override suspend fun fetchTodos(): ApiResponse<FetchTodos> = ApiResponse(dummyJsonApi.fetchTodos())

    override suspend fun insertAllTodos(todos: List<ToDo>) = toDoDao.insertAll(todos)

    override fun getAllTodos() = toDoDao.getAll()

    override fun getAllTodosLiveData() = toDoDao.getAllLiveData()

    override suspend fun updateTodo(todo: ToDo) = toDoDao.update(todo)

    override fun deleteTodos(userId: Long) = toDoDao.emptyTable(userId)

}