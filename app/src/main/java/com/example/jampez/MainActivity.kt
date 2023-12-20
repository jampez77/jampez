package com.example.jampez

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.jampez.databinding.ActivityMainBinding
import com.example.jampez.utils.ConnectionLiveData
import com.example.jampez.utils.extensions.isRooted
import com.example.jampez.utils.extensions.startLoadingAnimation
import com.example.jampez.utils.extensions.stopLoadingAnimation
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private val networkConnection: ConnectionLiveData by inject { parametersOf(this) }

    private val observers by lazy {
        networkConnection.observe(this) { isConnected ->
            viewModel.setNetworkConnection(isConnected)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState ?: Bundle())

        if (isRooted()) {
            finish()
        }
        postponeEnterTransition()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observers
    }

    fun startLoadingAnimation() {
        binding.sigingIn.startLoadingAnimation()
    }

    fun stopLoadingAnimation() {
        binding.sigingIn.stopLoadingAnimation()
    }

    fun showLoadingTransition(onTransitionFinished: () -> Unit) {
        binding.motionLayout.transitionToEnd {
            onTransitionFinished.invoke()
        }
    }

    fun hideLoadingTransition() {
        binding.motionLayout.transitionToStart()
    }
}