package com.example.group05_flower_api_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.group05_flower_api_app.ui.ui.home.HomeActivity
import com.example.group05_flower_api_app.ui.ui.login.LoginActivity
import com.example.group05_flower_api_app.ui.ui.result.ResultActivity
import org.hamcrest.CoreMatchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Before
    fun setUp() {
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @get:Rule
    val loginActivityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun testLoginNavigatesToHome() {
        // Click Sign In button on Login screen
        onView(withText(R.string.action_sign_in)).perform(click())

        // Assert that the intent to open HomeActivity was successfully triggered
        intended(hasComponent(HomeActivity::class.java.name))
    }

    @Test
    fun testSearchNavigatesToResult() {
        // Launch HomeActivity for testing search
        ActivityScenario.launch(HomeActivity::class.java).use {
            // Type search query and trigger the keyboard search action (IME action search)
            onView(withHint("Search...")).perform(replaceText("Roses"), closeSoftKeyboard())
            onView(withHint("Search...")).perform(pressImeActionButton())

            // Assert that the intent to open ResultActivity with the search query was successfully triggered
            intended(allOf(
                hasComponent(ResultActivity::class.java.name),
                hasExtra("EXTRA_QUERY", "Roses")
            ))
        }
    }
}
