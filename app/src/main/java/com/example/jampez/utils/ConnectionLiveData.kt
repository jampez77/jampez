package com.example.jampez.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.*
import android.net.NetworkCapabilities.*
import androidx.lifecycle.LiveData

class ConnectionLiveData(private val context: Context) : LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    private lateinit var connectivityManagerCallback: ConnectivityManager.NetworkCallback

    private val networkRequestBuilder: NetworkRequest.Builder = NetworkRequest.Builder()
        .addTransportType(TRANSPORT_CELLULAR)
        .addTransportType(TRANSPORT_WIFI)

    override fun onActive() {
        super.onActive()
        updateConnection()
        connectivityManager.registerNetworkCallback(
            networkRequestBuilder.build(),
            getConnectivityMarshmallowManagerCallback()
        )
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
    }

    private fun updateConnection() {
        var result = false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork
        if(networkCapabilities != null){
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities)

            if(actNw != null){
                result = when {
                    actNw.hasTransport(TRANSPORT_WIFI) -> true
                    actNw.hasTransport(TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            }
        }
        postValue(result)
    }

    private fun getConnectivityMarshmallowManagerCallback(): ConnectivityManager.NetworkCallback {
        connectivityManagerCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities) {

                networkCapabilities.let { capabilities ->

                    if (capabilities.hasCapability(NET_CAPABILITY_INTERNET) &&
                        capabilities.hasCapability(NET_CAPABILITY_VALIDATED)) {
                        postValue(true)
                    }
                }
            }

            override fun onLost(network: Network) {
                postValue(false)
            }
        }
        return connectivityManagerCallback
    }
}