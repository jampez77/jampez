package com.example.jampez.features.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jampez.data.di.dataProviderModule
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.models.User
import com.example.jampez.data.repositories.UserRepository
import io.mockk.every
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule.Companion.create
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule
import org.koin.test.mock.declareMock

class LoginViewModelTest : KoinTest {

    private val loginViewModel by inject<LoginViewModel>()
    private val testUser = User(1, "firstName", "email", "passwords", "image")

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()


    @get:Rule
    val koinTestRule = create {
        modules(listOf(dataProviderModule, viewModelModule))
        declareMock<UserRepository> {
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
    fun `test fetchUser - offline`() {
        loginViewModel.networkConnected = false

        loginViewModel.fetchUser(testUser.email, testUser.password)

        assertEquals(loginViewModel.userId.value, testUser.id)
    }

    @Test
    fun `test fetchUser - offline - incorrect details`() {
        loginViewModel.networkConnected = false

        loginViewModel.fetchUser("not email", "not password")

        assertEquals(loginViewModel.userId.value, null)
    }

}