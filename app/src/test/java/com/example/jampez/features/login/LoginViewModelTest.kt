package com.example.jampez.features.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.jampez.TestApplication
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.models.User
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.IUserRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.get
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule.Companion.create
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule

class LoginViewModelTest : KoinTest {

    private val loginViewModel by inject<LoginViewModel>()
    private val mockUser = User(1, "firstName", "email", "passwords", "image")

    private val application: TestApplication = mockk()

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    private val testModule = module(createdAtStart = true) {
        single<IConnectionRepository> { mockk() }
        single<IUserRepository> { mockk() }
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val koinTestRule = create {
        androidContext(application)
        modules(listOf(
            viewModelModule,
            testModule
        ))
    }

    @Test
    fun `test setEmailErrorState`() {
        val mockObserver: Observer<Boolean> = mockk(relaxed = true)
        loginViewModel.emailErrorState.observeForever(mockObserver)

        loginViewModel.setEmailErrorState(true)

        assertEquals(true, loginViewModel.emailErrorState.value)
    }

    @Test
    fun `test setPasswordErrorState`() {
        val mockObserver: Observer<Boolean> = mockk(relaxed = true)
        loginViewModel.passwordErrorState.observeForever(mockObserver)

        loginViewModel.setPasswordErrorState(true)

        assertEquals(true, loginViewModel.passwordErrorState.value)
    }

    @Test
    fun `test setSignInButtonState`() {
        val mockObserver: Observer<Boolean> = mockk(relaxed = true)
        loginViewModel.signInButtonState.observeForever(mockObserver)

        loginViewModel.setSignInButtonState(true)

        assertEquals(true, loginViewModel.signInButtonState.value)
    }

    @Test
    fun `test signIn`() {
        val mockObserver: Observer<Long?> = mockk(relaxed = true)
        loginViewModel.userId.observeForever(mockObserver)

        loginViewModel.signIn()

        assertEquals(true, loginViewModel.signInButtonState.value)
    }

    @Test
    fun `test getCurrentUserId WhenUserExists`() {
        val userRepository: IUserRepository = get()

        coEvery { userRepository.getUser() } returns mockUser

        val loginViewModel = LoginViewModel()

        val userId = loginViewModel.getCurrentUserId()

        assertEquals(mockUser.id, userId)
    }

    @Test
    fun `test getCurrentUserId WhenUserDoesNotExist`() {
        val userRepository: IUserRepository = get()

        coEvery { userRepository.getUser() } returns null

        val loginViewModel = LoginViewModel()

        val userId = loginViewModel.getCurrentUserId()

        assertEquals(null, userId)
    }

    @Test
    fun `test FetchUser - online`() {
        val connectionRepository: IConnectionRepository = get()
        val userRepository: IUserRepository = get()
        val loginViewModel = LoginViewModel()

        val emailInput = "test@example.com"
        val passwordInput = "password"

        every { connectionRepository.isNetworkConnected() } returns true

        val mockAuthenticatedUserId = 123L
        coEvery { userRepository.authenticatedUserId(emailInput, passwordInput) } returns mockAuthenticatedUserId

        loginViewModel.authenticateUser(emailInput, passwordInput)

        assertEquals(mockAuthenticatedUserId, loginViewModel.userId.value)
    }

    @Test
    fun `test FetchUser - offline - ValidCredentials`() {
        val connectionRepository: IConnectionRepository = get()
        val userRepository: IUserRepository = get()
        val loginViewModel = LoginViewModel()

        val emailInput = "email"
        val passwordInput = "passwords"

        every { connectionRepository.isNetworkConnected() } returns false

        coEvery { userRepository.getUser() } returns mockUser

        loginViewModel.authenticateUser(emailInput, passwordInput)

        assertEquals(mockUser.id, loginViewModel.userId.value)
    }

    @Test
    fun `test FetchUser - offline - InvalidCredentials`() {
        val connectionRepository: IConnectionRepository = get()
        val userRepository: IUserRepository = get()
        val loginViewModel = LoginViewModel()

        val emailInput = "test@example.com"
        val passwordInput = "invalidPassword"

        every { connectionRepository.isNetworkConnected() } returns false

        coEvery { userRepository.getUser() } returns mockUser

        loginViewModel.authenticateUser(emailInput, passwordInput)

        assertEquals(null, loginViewModel.userId.value)
    }
}