package com.example.group05_flower_api_app

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.group05_flower_api_app.ui.home.HomeActivity
import com.example.group05_flower_api_app.ui.login.LoginActivity
import com.example.group05_flower_api_app.ui.upload.UploadActivity
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
        onView(withText("Bypass OAuth (Developer Mode)")).perform(click())

        // Assert that the intent to open HomeActivity was successfully triggered
        intended(hasComponent(HomeActivity::class.java.name))
    }

    @Test
    fun testHomeDisplaysFlowerSearch() {
        // Launch HomeActivity and verify the API-backed search control is present.
        ActivityScenario.launch(HomeActivity::class.java).use {
            onView(withHint("Search flowers by name")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun testUploadNavigatesToUploadActivity() {
        // Launch HomeActivity for testing upload navigation
        ActivityScenario.launch(HomeActivity::class.java).use {
            onView(withContentDescription("Upload Flower")).perform(click())

            // Assert that the intent to open UploadActivity was successfully triggered
            intended(hasComponent(UploadActivity::class.java.name))
        }
    }
}
