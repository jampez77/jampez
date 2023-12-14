package com.example.jampez

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.jampez.databinding.ActivityMainBinding
import com.example.jampez.utils.constants.USER_ID
import com.example.jampez.utils.extensions.isRooted
import com.example.jampez.utils.extensions.startLoadingAnimation
import com.example.jampez.utils.extensions.stopLoadingAnimation
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState ?: Bundle())

        if (isRooted()) {
            finish()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = viewModel.getUser()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (user != null && navController.currentDestination?.id == R.id.loginFragment) {
            val bundle = Bundle()
            bundle.putLong(USER_ID, user.id)
            navController.navigate(R.id.action_loginFragment_to_todoFragment, bundle)
        } else {
            postponeEnterTransition()
        }
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