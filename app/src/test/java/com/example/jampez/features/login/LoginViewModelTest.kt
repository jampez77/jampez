package com.example.jampez.features.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jampez.TestApplication
import com.example.jampez.data.di.appModule
import com.example.jampez.data.di.dataProviderModule
import com.example.jampez.data.di.glideModule
import com.example.jampez.data.di.networkModule
import com.example.jampez.data.di.persistedDataModule
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.di.workerModule
import com.example.jampez.data.models.User
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.IUserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule.Companion.create
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

class LoginViewModelTest : KoinTest {

    private val loginViewModel by inject<LoginViewModel>()
    private val testUser = User(1, "firstName", "email", "passwords", "image")

    private val application: TestApplication = mockk()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @get:Rule
    val koinTestRule = create {
        androidContext(application)
        modules(listOf(
            viewModelModule,
            persistedDataModule,
            networkModule,
            dataProviderModule,
            glideModule,
            appModule,
            workerModule
        ))

        declareMock<IUserRepository> {
            every { getUser() } returns testUser
        }
    }


    @Test
    fun `test setEmailErrorState`() {
        loginViewModel.setEmailErrorState(true)

        assertEquals(loginViewModel.emailErrorState.value, true)
    }

    @Test
    fun `test setPasswordErrorState`() {
        loginViewModel.setPasswordErrorState(true)

        assertEquals(loginViewModel.passwordErrorState.value, true)
    }

    @Test
    fun `test setSignInButtonState`() {
        loginViewModel.setSignInButtonState(false)

        assertEquals(loginViewModel.signInButtonState.value, false)
    }

    @Test
    fun `test signIn`() {
        loginViewModel.signIn()

        assertEquals(loginViewModel.signInButtonState.value, true)
    }

    @Test
    fun `test getCurrentUserId`() {
        assertEquals(loginViewModel.getCurrentUserId(), testUser.id)
    }

    @Test
    fun `test fetchUser - offline`() {
        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns false
        }

        loginViewModel.authenticateUser(testUser.email, testUser.password)

        assertEquals(loginViewModel.userId.value, testUser.id)
    }

    @Test
    fun `test fetchUser - offline - incorrect details`() {
        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns false
        }

        loginViewModel.authenticateUser("not email", "not password")

        assertEquals(loginViewModel.userId.value, null)
    }

    @Test
    fun `test fetchUser - online`() {
        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns true
        }

        declareMock<IUserRepository> {
            every { authenticatedUserId(testUser.email, testUser.password) } returns testUser.id
        }

        loginViewModel.authenticateUser(testUser.email, testUser.password)


        assertEquals(loginViewModel.userId.value, testUser.id)
    }

    @Test
    fun `test fetchUser - online - incorrect details`() {
        declareMock<IConnectionRepository> {
            every { isNetworkConnected() } returns true
        }

        declareMock<IUserRepository> {
            every { authenticatedUserId("not email", "not password") } returns null
        }

        loginViewModel.authenticateUser("not email", "not password")


        assertEquals(loginViewModel.userId.value, null)
    }

}