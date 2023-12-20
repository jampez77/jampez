package com.example.jampez.features.login


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jampez.data.repositories.ConnectionRepository
import com.example.jampez.data.repositories.UserRepository
import com.example.jampez.utils.extensions.isEquals
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class LoginViewModel : ViewModel() {

    private val userRepository: UserRepository by inject(UserRepository::class.java)
    private val connectionRepository: ConnectionRepository by inject(ConnectionRepository::class.java)
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
    private val _signInButtonState = MutableLiveData<Boolean>()
    val signInButtonState: LiveData<Boolean> = _signInButtonState

    fun setSignInButtonState(isPressed: Boolean) {
        _signInButtonState.postValue(isPressed)
    }
    fun signIn() {
        _signInButtonState.postValue(true)
    }
    private val _userId = MutableLiveData<Long?>()
    val userId: LiveData<Long?> = _userId

    fun isNetworkConnected() = connectionRepository.isNetworkConnected()

    fun getCurrentUserId(): Long? {
        return userRepository.getUser()?.id
    }

    fun fetchUser(emailInput: String, passwordInput: String) {
        if (isNetworkConnected()) {
            viewModelScope.launch(IO) {
                val userId = userRepository.fetchUsers(emailInput, passwordInput)
                _userId.postValue(userId)
            }
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