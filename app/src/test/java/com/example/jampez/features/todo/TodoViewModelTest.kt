package com.example.jampez.features.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.jampez.TestApplication
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.ITodoRepository
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.data.models.User
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import retrofit2.Call

class TodoViewModelTest : KoinTest {

    private val todoViewModel by inject<TodoViewModel>()
    private val mockUser = User(1, "firstName", "email", "passwords", "image")
    private val mockTodo = ToDo(
        1,
        "todo",
        true,
        1
    )
    private val mockTodos = listOf(mockTodo)

    private val application: TestApplication = mockk()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testModule = module(createdAtStart = true) {
        single<IUserRepository> { mockk() }
        single<ITodoRepository> { mockk() }
        single<IConnectionRepository> { mockk() }
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(application)
        modules(
            listOf(
                viewModelModule,
                testModule
            )
        )
    }

    @Before
    fun setup() {
        val todoRepository: ITodoRepository = get()
        coEvery { todoRepository.getAllTodos() } returns mockTodos
    }

    @Test
    fun `test signOut`() {
        val mockObserver: Observer<Boolean> = mockk(relaxed = true)
        todoViewModel.signOutButtonState.observeForever(mockObserver)

        todoViewModel.signOut()

        verify { mockObserver.onChanged(true) }
    }

    @Test
    fun `test getUser`() {
        val userRepository: IUserRepository = get()
        coEvery { userRepository.getUser() } returns mockUser

        val user = runBlocking {
            todoViewModel.getUser()
        }

        assertEquals(user, mockUser)
    }

    @Test
    fun `test allTodosLiveData`() {
        val todoRepository: ITodoRepository = get()
        coEvery { todoRepository.getAllTodosLiveData() } returns MutableLiveData(mockTodos)

        val allTodosLiveData = todoViewModel.allTodosLiveData()

        val observer = Observer<List<ToDo>> { todos ->
            assertEquals(todos, todos)
        }

        allTodosLiveData.observeForever(observer)

        allTodosLiveData.removeObserver(observer)
    }

    @Test
    fun `test allToDos`() {
        val todoRepository: ITodoRepository = get()
        coEvery { todoRepository.getAllTodos() } returns mockTodos

        todoViewModel.initTodos()

        assertEquals(todoViewModel.allTodos, mockTodos)
    }

    @Test
    fun `test isNetworkConnected`() {
        val connectionRepository: IConnectionRepository = get()
        coEvery { connectionRepository.isNetworkConnected() } returns false
        assertEquals(todoViewModel.isNetworkConnected(), false)
    }

    @Test
    fun `test UpdateTodo`() {
        val todoRepository: ITodoRepository = get()

        coEvery { todoRepository.updateTodo(mockTodo) } returns Unit

        todoViewModel.updateTodo(mockTodo)

        coEvery { todoRepository.updateTodo(mockTodo) }
    }

    @Test
    fun `test fetchTodos - success`() = runBlocking {
        val todoRepository: ITodoRepository = get()

        val mockCall = mockk<Call<FetchTodos>>()
        val mockResource = ApiResponse(mockCall).apply {
            body = FetchTodos(mockTodos)
            success = true
            errorMessage = null
        }

        coEvery { todoRepository.fetchTodos() } returns mockResource
        coEvery { todoRepository.insertAllTodos(any()) } returns Unit // Mock insertAllTodos

        todoViewModel.fetchTodos(mockUser.id)

        assertEquals(true, todoViewModel.loading.value)

        delay(1000)

        assertEquals(false, todoViewModel.loading.value)
        assertEquals(null, todoViewModel.errorMessage.value)
        assertEquals(mockTodos, todoViewModel.allTodos)
    }

    @Test
    fun `test fetchTodos - error`() = runBlocking {
        val todoRepository: ITodoRepository = get()

        val responseErrorMessage = "ERROR"

        val mockCall = mockk<Call<FetchTodos>>()
        val mockResource = ApiResponse(mockCall).apply {
            body = null
            success = false
            errorMessage = responseErrorMessage
        }

        coEvery { todoRepository.fetchTodos() } returns mockResource

        todoViewModel.fetchTodos(mockUser.id)

        assertEquals(true, todoViewModel.loading.value)

        delay(1000)

        assertEquals(false, todoViewModel.loading.value)
        assertEquals(responseErrorMessage, todoViewModel.errorMessage.value)
    }
}