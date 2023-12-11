package com.example.jampez.features.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.api.wrappers.ResourceFactory
import com.example.jampez.data.api.wrappers.Status
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.repositories.TodoRepository
import com.example.jampez.data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class TodoViewModel(private val userRepository: UserRepository, private val todoRepository: TodoRepository) : ViewModel() {

    var networkConnected: Boolean = false

    private val todosResourceFactory = ResourceFactory<FetchTodos>()

    private val _signOutButtonState = MutableLiveData<Boolean>()
    val signOutButtonState: LiveData<Boolean> = _signOutButtonState

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun signOut() = _signOutButtonState.postValue(true)

    fun deleteUser(userId: Long) : Boolean {
        todoRepository.deleteTodos(userId)
        return userRepository.deleteUser()
    }

    fun getUser() = userRepository.getUser()

    var allTodos: List<ToDo>? = null

    fun allTodosLiveData() = todoRepository.getAllTodosLiveData()

    init {
        viewModelScope.launch(IO) {
            allTodos = todoRepository.getAllTodos()
        }
    }

    fun updateTodo(toDo: ToDo) {
        viewModelScope.launch(IO) {
            todoRepository.updateTodo(toDo)
        }
    }

    fun fetchTodos(userId: Long) {
        viewModelScope.launch(IO) {
            _loading.postValue(true)
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