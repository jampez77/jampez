package com.example.jampez.data.interfaces

interface IConnectionRepository {
    fun setNetworkConnection(isConnected: Boolean)
    fun isNetworkConnected(): Boolean
}