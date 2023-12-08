package com.example.jampez.data.api

import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.api.responses.FetchUsers
import com.example.jampez.utils.constants.TODOS
import com.example.jampez.utils.constants.USERS
import retrofit2.Call
import retrofit2.http.GET

interface DummyJsonApi {

    @GET(USERS)
    fun fetchUsers() : Call<FetchUsers>

    @GET(TODOS)
    fun fetchTodos() : Call<FetchTodos>
}