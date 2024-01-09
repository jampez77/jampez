package com.example.jampez.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import com.example.jampez.TestApplication
import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.data.api.responses.FetchTodos
import com.example.jampez.data.api.wrappers.ApiResponse
import com.example.jampez.data.dao.ToDoDao
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.interfaces.ITodoRepository
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.utils.CallMock.Companion.mockSuccessResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.MockProviderRule

class TodoRepositoryTest : AutoCloseKoinTest() {

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

    // Define a Koin module to provide mocked dependencies
    private val testModule = module {
        single<DummyJsonApi> { mockk() }
        single<ToDoDao> { mockk(relaxed = true) }
        single<ITodoRepository> { TodoRepository(get(), get()) }
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(application)
        modules(listOf(
            viewModelModule,
            testModule
        ))
    }

    @Test
    fun `fetchTodos should return ApiResponse from DummyJsonApi`() = runBlocking {

        val todoRepository: ITodoRepository = get()
        val dummyJsonApi: DummyJsonApi = get()

        val mockCallResponse = mockSuccessResponse(FetchTodos(mockTodos))
        coEvery { dummyJsonApi.fetchTodos() } returns mockCallResponse

        val mockResource = ApiResponse(mockCallResponse)

        val result = todoRepository.fetchTodos()

        assertEquals(mockResource.body, result.body)
        assertEquals(mockResource.success, result.success)
    }

    @Test
    fun `insertAllTodos should call insertAll method in ToDoDao`() = runBlocking {
        val todoRepository: ITodoRepository = get()
        val toDoDao: ToDoDao = get()

        todoRepository.insertAllTodos(mockTodos)

        coVerify { toDoDao.insertAll(mockTodos) }
    }

    @Test
    fun `getAllTodos should return allToDos from ToDoDao`() {
        val todoRepository: ITodoRepository = get()
        val toDoDao: ToDoDao = get()

        coEvery { toDoDao.getAll() } returns mockTodos

        assertEquals(todoRepository.getAllTodos(), mockTodos)
    }

    @Test
    fun `getAllTodosLiveData should return LiveData containing list of ToDos from ToDoDao`() {

        val todoRepository: ITodoRepository = get()
        val toDoDao: ToDoDao = get()
        val mockLiveData: LiveData<List<ToDo>> = mockk()

        every { toDoDao.getAllLiveData() } returns mockLiveData
        every { mockLiveData.value } returns mockTodos

        val liveDataResult = todoRepository.getAllTodosLiveData().value
        assertEquals(mockTodos, liveDataResult)
    }

    @Test
    fun `updateTodo should call update method in ToDoDao`() = runBlocking {
        val todoRepository: ITodoRepository = get()
        val toDoDao: ToDoDao = get()

        todoRepository.updateTodo(mockTodo)

        coVerify { toDoDao.update(mockTodo) }
    }

    @Test
    fun `deleteTodos should call emptyTable method in ToDoDao`() {
        val todoRepository: ITodoRepository = get()
        val toDoDao: ToDoDao = get()

        val userId : Long = -1

        todoRepository.deleteTodos(userId)

        coVerify { toDoDao.emptyTable(userId) }
    }

}