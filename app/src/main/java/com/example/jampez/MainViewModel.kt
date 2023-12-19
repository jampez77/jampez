package com.example.jampez

import androidx.lifecycle.ViewModel
import com.example.jampez.data.models.User
import com.example.jampez.data.repositories.UserRepository
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel()  {

    private val userRepository: UserRepository by inject(UserRepository::class.java)

    fun getUser() : User? {
        return userRepository.getUser()
    }
}