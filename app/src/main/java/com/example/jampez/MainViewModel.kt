package com.example.jampez

import androidx.lifecycle.ViewModel
import com.example.jampez.data.models.User
import com.example.jampez.data.repositories.UserRepository

class MainViewModel(private val userRepository: UserRepository) : ViewModel()  {

    fun getUser() : User? {
        return userRepository.getUser()
    }
}