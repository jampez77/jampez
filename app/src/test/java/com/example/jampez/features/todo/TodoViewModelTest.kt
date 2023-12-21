package com.example.jampez.features.todo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.jampez.TestApplication
import com.example.jampez.data.di.appModule
import com.example.jampez.data.di.dataProviderModule
import com.example.jampez.data.di.glideModule
import com.example.jampez.data.di.networkModule
import com.example.jampez.data.di.persistedDataModule
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.di.workerModule
import com.example.jampez.data.entities.ToDo
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.ITodoRepository
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.data.models.User
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

class TodoViewModelTest : KoinTest {

    private val todoViewModel by inject<TodoViewModel>()
    private val testUser = User(1, "firstName", "email", "passwords", "image")
    private val todos = listOf(
        ToDo(
            1,
            "todo",
            true,
            1
        )
    )
    private val todosLiveData = MutableLiveData(todos)

    private val application: TestApplication = mockk()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(application)
        modules(
            listOf(
                viewModelModule,
                persistedDataModule,
                networkModule,
                dataProviderModule,
                glideModule,
                appModule,
                workerModule
            )
        )

        declareMock<IUserRepository> {
            every { getUser() } returns testUser
        }

        declareMock<ITodoRepository> {
            every { getAllTodos() } returns todos
            every { getAllTodosLiveData() } returns todosLiveData
        }

        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns false
        }
    }

    @Test
    fun `test signOut`() {
        todoViewModel.signOut()

        assertEquals(todoViewModel.signOutButtonState.value, true)
    }

    @Test
    fun `test getUser`() {
        assertEquals(todoViewModel.getUser(), testUser)
    }

    @Test
    fun `test allTodosLiveData`() {
        assertEquals(todoViewModel.allTodosLiveData().value, todos)
    }

    @Test
    fun `test allToDos`() {
        todoViewModel.initTodos()

        assertEquals(todoViewModel.allTodos, todos)
    }

    @Test
    fun `test isNetworkConnected`() {
        assertEquals(todoViewModel.isNetworkConnected(), false)
    }

    @Test
    fun `test fetchTodos`() = runTest {
        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns true
        }

        declareMock<IUserRepository> {
            every { authenticatedUserId(testUser.email, testUser.password) } returns testUser.id
        }

        todoViewModel.fetchTodos(testUser.id)


        assertEquals(todoViewModel.loading.value, true)
    }
}