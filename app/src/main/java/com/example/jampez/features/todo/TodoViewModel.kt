package com.example.jampez.features.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.api.wrappers.ResourceFactory
import com.example.jampez.data.api.wrappers.Status
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.ITodoRepository
import com.example.jampez.data.interfaces.IUserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class TodoViewModel : ViewModel() {

    private val userRepository: IUserRepository by inject(IUserRepository::class.java)
    private val todoRepository: ITodoRepository by inject(ITodoRepository::class.java)
    private val connectionRepository: IConnectionRepository by inject(IConnectionRepository::class.java)

    private val todosResourceFactory = ResourceFactory<FetchTodos>()

    private val _signOutButtonState = MutableLiveData<Boolean>()
    val signOutButtonState: LiveData<Boolean> = _signOutButtonState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun signOut() = _signOutButtonState.postValue(true)

    fun getUser() = userRepository.getUser()

    var allTodos: List<ToDo>? = null

    fun allTodosLiveData() = todoRepository.getAllTodosLiveData()

    init {
        viewModelScope.launch(IO) {
            initTodos()
        }
    }

    fun initTodos() {
        allTodos = todoRepository.getAllTodos()
    }
    fun isNetworkConnected() = connectionRepository.isNetworkConnected()

    fun updateTodo(toDo: ToDo) {
        viewModelScope.launch(IO) {
            todoRepository.updateTodo(toDo)
        }
    }

    fun fetchTodos(userId: Long) {
        _loading.postValue(true)
        viewModelScope.launch(IO) {
            val usersResponse = todosResourceFactory.getResource(todoRepository.fetchTodos())

            when(usersResponse.status) {
                Status.SUCCESS -> {
                    val todos = usersResponse.data?.todos

                    val userTodos = todos?.filter { it.userId == userId }

                    userTodos?.run {
                        todoRepository.insertAllTodos(this)
                    }

                    _loading.postValue(false)
                }
                Status.ERROR -> {
                    usersResponse.message?.let { errorMessage ->
                        _errorMessage.postValue(errorMessage)
                    }
                    _loading.postValue(false)
                }
                Status.LOADING -> {

                }
            }
        }
    }

}