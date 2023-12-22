package com.example.jampez.features.login

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import com.example.jampez.R

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginFragmentTest : KoinTest {

    @Test
    fun testEventFragment() {

        launchFragmentInContainer<LoginFragment>(
            themeResId = R.style.Theme_Jampez
        )

        onView(withId(R.id.scrollView)).check(matches(isDisplayed()))
    }
}