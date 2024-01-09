package com.example.jampez.data.repositories

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jampez.TestApplication
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.utils.constants.NETWORK_CONNECTED
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.mock.MockProviderRule

class ConnectionRepositoryTest : AutoCloseKoinTest() {

    private val application: TestApplication = mockk()

    // Define a Koin module for testing to provide mocked instances
    private val testModule = module(createdAtStart = true) {
        single<SharedPreferences> { mockk(relaxed = true) }
        single<IConnectionRepository> { ConnectionRepository() }
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
    fun `setNetworkConnection should update the network connection status in SharedPreferences`() {

        val isConnected = true

        val mockPrefs: SharedPreferences = get()
        val connectionRepository: IConnectionRepository = get()

        connectionRepository.setNetworkConnection(isConnected)

        // Verify that SharedPreferences edit function is invoked with the expected values
        verify {
          mockPrefs.edit().putBoolean(NETWORK_CONNECTED, isConnected)
        }
    }

    @Test
    fun `isNetworkConnected should return the correct network connection status`() {
        val mockPrefs: SharedPreferences = get()
        val connectionRepository: IConnectionRepository = get()
        val isConnected = true

        every { mockPrefs.getBoolean(NETWORK_CONNECTED, any()) } returns isConnected
        val result = connectionRepository.isNetworkConnected()

        assertEquals(isConnected, result)
        verify {
            mockPrefs.getBoolean(NETWORK_CONNECTED, true)
        }
    }

}