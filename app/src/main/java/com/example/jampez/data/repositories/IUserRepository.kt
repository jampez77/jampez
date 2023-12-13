package com.example.jampez.data.repositories

import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.api.responses.FetchUsers
import com.example.jampez.data.models.User

interface IUserRepository {
    suspend fun fetchUsers(): ApiResponse<FetchUsers>
    fun saveDatabasePassPhrase(passPhrase: String?) : Boolean
    fun saveUser(user: User?) : Boolean
    fun deleteUser() : Boolean
    fun getUser() : User?
}