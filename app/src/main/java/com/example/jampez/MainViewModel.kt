package com.example.jampez

import androidx.lifecycle.ViewModel
import com.example.jampez.data.interfaces.IConnectionRepository
import org.koin.java.KoinJavaComponent.inject

class MainViewModel : ViewModel()  {

    private val connectionRepository: IConnectionRepository by inject(IConnectionRepository::class.java)

    fun setNetworkConnection(isConnected: Boolean) {
        connectionRepository.setNetworkConnection(isConnected)
    }
}