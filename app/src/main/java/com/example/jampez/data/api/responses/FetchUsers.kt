package com.example.jampez.data.api.responses

import com.example.jampez.data.models.User


data class FetchUsers(
    val users: List<User>
)