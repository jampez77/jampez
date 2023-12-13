package com.example.jampez.features.login


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jampez.data.models.User
import com.example.jampez.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    var networkConnected: Boolean = false

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

    private val _userIdState = MutableLiveData<Long?>()
    val userIdState: LiveData<Long?> = _userIdState

    fun setSignedInState(signedIn: Long?) {
        _userIdState.postValue(signedIn)
    }

    private fun saveUser(user: User?) : Boolean {
        return userRepository.saveUser(user)
    }

    private fun saveDatabasePassPhrase(passPhrase: String?) : Boolean {
        return userRepository.saveDatabasePassPhrase(passPhrase)
    }

    fun getUser() : User? {
        return userRepository.getUser()
    }

    fun fetchUser(emailInput: String, passwordInput: String) {

        viewModelScope.launch(IO) {
            val usersResponse = userRepository.fetchUsers()

            usersResponse.let { response ->

                if(response.success) {
                    val users = response.body?.users

                    val user = users?.find { it.email == emailInput && it.password == passwordInput }

                    val userId = if (saveDatabasePassPhrase(user?.password) && saveUser(user)) {
                        user?.id
                    } else {
                        null
                    }
                    _userIdState.postValue(userId)
                } else {
                    _userIdState.postValue(null)
                }
            }
        }
    }

}