package com.example.jampez

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import com.example.jampez.data.di.appModule
import com.example.jampez.data.di.viewModelModule
import com.example.jampez.data.interfaces.IConnectionRepository
import com.example.jampez.data.interfaces.IUserRepository
import com.example.jampez.utils.ConnectionLiveData
import com.google.android.material.snackbar.Snackbar
import io.mockk.mockk
import io.mockk.mockkClass
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.KoinTestRule
import org.koin.test.check.checkModules
import org.koin.test.get
import org.koin.test.inject
import org.koin.test.mock.MockProviderRule

class ModulesTest: AutoCloseKoinTest() {

    private val connectionLiveData: ConnectionLiveData by inject()
    private val snackbar: Snackbar by inject()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var activityScenario: ActivityScenario<MainActivity>


    @Test
    fun `test appModule configuration`() {
        val testModule = module {
            single { ConnectionLiveData(getMainActivity()) }
            single { Snackbar.make(getMainActivity().findViewById(R.id.nav_host_fragment), "Test Snackbar", Snackbar.LENGTH_SHORT) }
        }

        startKoin {
            modules(listOf(testModule))
        }

        assertNotNull(connectionLiveData)
        assertNotNull(snackbar)
    }

    private fun getMainActivity(): MainActivity {
        val countingIdlingResource = CountingIdlingResource("activityScenario")
        IdlingRegistry.getInstance().register(countingIdlingResource)

        activityScenario = ActivityScenario.launch(MainActivity::class.java)

        var mainActivity: MainActivity? = null
        activityScenario.onActivity {
            mainActivity = it
            countingIdlingResource.decrement()
        }

        IdlingRegistry.getInstance().unregister(countingIdlingResource)
        return mainActivity ?: throw IllegalStateException("MainActivity not found")
    }
}