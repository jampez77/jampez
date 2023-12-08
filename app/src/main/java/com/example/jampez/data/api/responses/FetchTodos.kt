package com.example.jampez.data.api.responses

import com.example.jampez.data.entities.ToDo

data class FetchTodos(
    val todos: List<ToDo>
)