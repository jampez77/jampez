package com.example.jampez.data.repositories

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jampez.TestApplication
import com.example.jampez.data.api.DummyJsonApi
import com.example.jampez.data.api.responses.FetchUsers
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.data.models.User
import com.example.jampez.utils.CallMock.Companion.mockErrorResponse
import com.example.jampez.utils.CallMock.Companion.mockSuccessResponse
import com.example.jampez.utils.constants.EMAIL
import com.example.jampez.utils.constants.FIRST_NAME
import com.example.jampez.utils.constants.ID
import com.example.jampez.utils.constants.IMAGE
import com.example.jampez.utils.constants.PASSPHRASE
import com.example.jampez.utils.constants.PASSWORD
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.MockProviderRule

class UserRepositoryTest : AutoCloseKoinTest() {

    private val mockUser = User(1, "firstName", "email", "passwords", "image")
    private val mockUsers = listOf(mockUser)

    private val application: TestApplication = mockk()

    // Define a Koin module for testing to provide mocked instances
    private val testModule = module(createdAtStart = true) {
        single<SharedPreferences> { mockk(relaxed = true) }
        single<DummyJsonApi> { mockk() }
        single<IUserRepository> { UserRepository(get()) }
    }

    @get:Rule
    val mockProvider = MockProviderRule.create { clazz ->
        mockkClass(clazz)
    }

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        androidContext(application)
        modules(listOf(
            viewModelModule,
            testModule
        ))
    }

    @Test
    fun `authenticatedUserId should return null on unsuccessful authentication`() {
        val dummyJsonApi: DummyJsonApi = get()
        val mockCallResponse = mockErrorResponse<FetchUsers>(
            500,
            "type",
            "content"
        )

        val userRepository: IUserRepository = get()

        every { dummyJsonApi.fetchUsers() } returns mockCallResponse

        val authenticatedUserId = userRepository.authenticatedUserId(mockUser.email, mockUser.password)

        assertEquals(null, authenticatedUserId)
    }

    @Test
    fun `authenticatedUserId should return null on unsuccessful save to shared preferences`() {
        val dummyJsonApi: DummyJsonApi = get()
        val mockCallResponse = mockSuccessResponse(FetchUsers(listOf()))

        val userRepository: IUserRepository = get()

        every { dummyJsonApi.fetchUsers() } returns mockCallResponse

        val authenticatedUserId = userRepository.authenticatedUserId(mockUser.email, mockUser.password)

        assertNull(authenticatedUserId)
    }

    @Test
    fun `authenticatedUserId should return expected ID on successful authentication`() {
        val mockPrefs: SharedPreferences = get()
        val dummyJsonApi: DummyJsonApi = get()
        val mockCallResponse = mockSuccessResponse(FetchUsers(mockUsers))

        val userRepository: IUserRepository = get()

        every { dummyJsonApi.fetchUsers() } returns mockCallResponse

        every { mockPrefs.edit() } returns mockk()
        every { mockPrefs.contains(any()) } returns false
        every { mockPrefs.edit().putString(any(), any()) } returns mockk()

        val authenticatedUserId = userRepository.authenticatedUserId(mockUser.email, mockUser.password)

        assertEquals(mockUser.id, authenticatedUserId)
        verify { mockPrefs.edit().putString(any(), any()) }
        verify { mockPrefs.edit().putLong(ID, mockUser.id) }
    }

    @Test
    fun `saveDatabasePassPhrase should save passphrase in SharedPreferences`() {
        val mockPrefs: SharedPreferences = get()
        val userRepository: IUserRepository = get()
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)

        every { mockPrefs.edit() } returns editor
        every { mockPrefs.contains(any()) } returns false

        val result = userRepository.saveDatabasePassPhrase()

        assertTrue(result)
        verify { editor.putString(PASSPHRASE, any()) }
    }

    @Test
    fun `saveUser should save user in SharedPreferences`() {
        val mockPrefs: SharedPreferences = get()
        val userRepository: IUserRepository = get()
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)

        every { mockPrefs.edit() } returns editor
        every { mockPrefs.contains(any()) } returns false

        val result = userRepository.saveUser(mockUser)

        assertTrue(result)
        verify { editor.putLong(ID, mockUser.id) }
    }

    @Test
    fun `deleteUser should remove user data from SharedPreferences`() {
        val mockPrefs: SharedPreferences = get()
        val userRepository: IUserRepository = get()
        val editor = mockk<SharedPreferences.Editor>(relaxed = true)

        every { mockPrefs.edit() } returns editor
        every { editor.remove(any()) } returns editor

        val result = userRepository.deleteUser()

        assertTrue(result)
        verify { editor.remove(ID) }
    }

    @Test
    fun `getUser should return expected User object from SharedPreferences`() {
        val userRepository: IUserRepository = get()
        val mockPrefs: SharedPreferences = get()

        every { mockPrefs.contains(any()) } returns true
        every { mockPrefs.getLong(ID, any()) } returns mockUser.id
        every { mockPrefs.getString(FIRST_NAME, any()) } returns mockUser.firstName
        every { mockPrefs.getString(EMAIL, any()) } returns mockUser.email
        every { mockPrefs.getString(PASSWORD, any()) } returns mockUser.password
        every { mockPrefs.getString(IMAGE, any()) } returns mockUser.image

        val actualUser = userRepository.getUser()

        assertEquals(mockUser, actualUser)
    }
}