package com.example.jampez.features.login

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import com.example.jampez.MainActivity
import com.example.jampez.R
import com.example.jampez.databinding.FragmentLoginBinding
import com.example.jampez.features.base.BaseFragment
import com.example.jampez.utils.ConnectionLiveData
import com.example.jampez.utils.constants.USER_ID
import com.example.jampez.utils.extensions.isEmailValid
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val loginViewModel: LoginViewModel by viewModel()
    private lateinit var navController: NavController
    private val networkConnection: ConnectionLiveData by inject()
    private val snackbar: Snackbar by inject { parametersOf(requireActivity()) }

    private val observers by lazy {
        networkConnection.observe(viewLifecycleOwner) { isConnected ->
            loginViewModel.networkConnected = isConnected
        }

        loginViewModel.emailErrorState.observe(viewLifecycleOwner) { showError ->
            binding.emailLayout.error = if (showError) {
                getString(R.string.invalid_email)
            } else {
                null
            }
        }

        loginViewModel.passwordErrorState.observe(viewLifecycleOwner) { showError ->
            binding.passwordLayout.error = if (showError) {
                getString(R.string.invalid_password)
            } else {
                null
            }
        }

        loginViewModel.signInButtonState.observe(viewLifecycleOwner) { isPressed ->
            if (isPressed) {
                signIn()
            }
        }

        loginViewModel.userIdState.observe(viewLifecycleOwner) { userId ->
            if (userId != null) {
                val mainActivity = (activity as MainActivity)
                mainActivity.stopLoadingAnimation()
                val bundle = Bundle()
                bundle.putLong(USER_ID, userId)
                navController.navigate(R.id.action_loginFragment_to_todoFragment, bundle)
            } else {
                loginViewModel.setSignInButtonState(false)

                val mainActivity = (activity as MainActivity)
                mainActivity.hideLoadingTransition()

                snackbar.setText(
                    if (loginViewModel.networkConnected) {
                        getString(R.string.user_not_found)
                    } else {
                        getString(R.string.no_network_connection)
                    }
                )
                snackbar.show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController(requireActivity(), R.id.nav_host_fragment)

        binding.apply {
            viewModel = loginViewModel
        }
        observers
    }

    private fun signIn() {

        val email = binding.email.text.toString()
        val password = binding.password.text.toString()

        if (validateCredentials(email, password)) {
            val mainActivity = (activity as MainActivity)
            mainActivity.startLoadingAnimation()
            mainActivity.showLoadingTransition {
                if (loginViewModel.networkConnected) {
                    lifecycleScope.launch {
                        loginViewModel.fetchUser(email, password)
                    }
                } else {
                    val user = loginViewModel.getUser()
                    val userId = if (user != null && TextUtils.equals(user.email, email) && TextUtils.equals(user.password, password)) {
                        user.id
                    } else {
                        null
                    }
                    loginViewModel.setSignedInState(userId)
                }

            }
        }
    }

    private fun validateCredentials(emailInput: String, passwordInput: String) : Boolean {
        var canProceed: Boolean
        binding.apply {

            val emailValid = emailInput.isEmailValid()
            loginViewModel.setEmailErrorState(!emailValid)

            val passwordValid = !TextUtils.isEmpty(passwordInput)
            loginViewModel.setPasswordErrorState(!passwordValid)

            canProceed = emailValid && passwordValid
        }
        return canProceed
    }
}