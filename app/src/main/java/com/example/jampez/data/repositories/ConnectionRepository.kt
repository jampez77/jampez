package com.example.jampez.data.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.utils.constants.NETWORK_CONNECTED
import org.koin.java.KoinJavaComponent.inject

class ConnectionRepository : IConnectionRepository {

    private val prefs: SharedPreferences by inject(
        SharedPreferences::class.java
    )

    override fun setNetworkConnection(isConnected: Boolean) {
        prefs.edit {
            putBoolean(NETWORK_CONNECTED, isConnected)
        }
    }

    override fun isNetworkConnected() = prefs.getBoolean(NETWORK_CONNECTED, true)
}