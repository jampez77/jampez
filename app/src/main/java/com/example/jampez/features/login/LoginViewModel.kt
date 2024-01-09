package com.example.jampez.features.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.utils.Event
import com.example.jampez.utils.extensions.isEquals
import org.koin.java.KoinJavaComponent.inject

class LoginViewModel : ViewModel() {

    private val userRepository: IUserRepository by inject(IUserRepository::class.java)
    private val connectionRepository: IConnectionRepository by inject(IConnectionRepository::class.java)
    private val _emailErrorState = MutableLiveData<Boolean>()
    val emailErrorState: LiveData<Boolean> = _emailErrorState

    fun setEmailErrorState(showError: Boolean) {
        _emailErrorState.postValue(showError)
    }

    private val _passwordErrorState = MutableLiveData<Boolean>()
    val passwordErrorState: LiveData<Boolean> = _passwordErrorState

    fun setPasswordErrorState(showError: Boolean) {
        _passwordErrorState.postValue(showError)
    }
    private val _signInButtonState = MutableLiveData<Event<Unit>>()
    val signInButtonState: LiveData<Event<Unit>> = _signInButtonState

    fun signIn() {
        _signInButtonState.postValue(Event(Unit))
    }

    private val _userId = MutableLiveData<Long?>()
    val userId: LiveData<Long?> = _userId

    fun isNetworkConnected() = connectionRepository.isNetworkConnected()

    fun getCurrentUserId(): Long? {
        return userRepository.getUser()?.id
    }

    fun authenticateUser(emailInput: String, passwordInput: String) {
        if (isNetworkConnected()) {
            val userId = userRepository.authenticatedUserId(emailInput, passwordInput)
            _userId.postValue(userId)
        } else {
            val user = userRepository.getUser()
            val userId = if (user != null && user.email.isEquals(emailInput) && user.password.isEquals(passwordInput)) {
                user.id
            } else {
                null
            }
            _userId.postValue(userId)
        }
    }
}