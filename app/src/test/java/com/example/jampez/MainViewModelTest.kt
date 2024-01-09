package com.example.jampez

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.interfaces.IConnectionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule

class MainViewModelTest : AutoCloseKoinTest() {

    private val mainViewModel by inject<MainViewModel>()
    private val application: TestApplication = mockk()
    // Define a Koin module for testing to provide mocked instances
    private val testModule = module(createdAtStart = true) {
        single<IConnectionRepository> { mockk() }
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
    fun `test SetNetworkConnection`() {
        // Set up
        val isConnected = true

        val connectionRepository = get<IConnectionRepository>()

        // Mock behavior of connectionRepository's setNetworkConnection method using MockK
        every { connectionRepository.setNetworkConnection(isConnected) } returns Unit

        // Call the method under test
        mainViewModel.setNetworkConnection(isConnected)

        // Verify that setNetworkConnection was called with the correct parameter using MockK
        verify { connectionRepository.setNetworkConnection(isConnected) }
    }
}