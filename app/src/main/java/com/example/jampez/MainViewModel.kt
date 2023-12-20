package com.example.jampez

import androidx.lifecycle.ViewModel
import com.example.jampez.data.repositories.ConnectionRepository
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel()  {

    private val connectionRepository: ConnectionRepository by inject(ConnectionRepository::class.java)

    fun setNetworkConnection(isConnected: Boolean) {
        connectionRepository.setNetworkConnection(isConnected)
    }
}