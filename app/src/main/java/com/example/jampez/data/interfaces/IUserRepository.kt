package com.example.jampez.data.interfaces

import com.example.jampez.data.models.User

interface IUserRepository {
    fun authenticatedUserId(emailInput: String, passwordInput: String): Long?
    fun saveDatabasePassPhrase() : Boolean
    fun saveUser(user: User?) : Boolean
    fun deleteUser() : Boolean
    fun getUser() : User?
}