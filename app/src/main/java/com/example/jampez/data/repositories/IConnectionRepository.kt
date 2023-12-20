package com.example.jampez.data.repositories

interface IConnectionRepository {
    fun setNetworkConnection(isConnected: Boolean)
    fun isNetworkConnected(): Boolean
}