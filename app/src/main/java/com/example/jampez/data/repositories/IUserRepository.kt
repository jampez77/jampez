package com.example.jampez.data.repositories

import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.api.responses.FetchUsers
import com.example.jampez.data.models.User

interface IUserRepository {
    suspend fun fetchUsers(emailInput: String, passwordInput: String): Long?
    fun saveDatabasePassPhrase() : Boolean
    fun saveUser(user: User?) : Boolean
    fun deleteUser() : Boolean
    fun getUser() : User?
}