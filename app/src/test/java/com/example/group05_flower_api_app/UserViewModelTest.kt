package com.example.group05_flower_api_app

import com.example.group05_flower_api_app.data.api.FlowerApiService
import com.example.group05_flower_api_app.data.api.GoogleTokenClaims
import com.example.group05_flower_api_app.data.api.JwtUtils
import com.example.group05_flower_api_app.data.api.TokenManager
import com.example.group05_flower_api_app.data.api.UserProfileResponse
import com.example.group05_flower_api_app.ui.user.UserViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val api: FlowerApiService = mockk()
    private val tokens: TokenManager = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        mockkObject(JwtUtils)
        every { tokens.getAuthToken() } returns "google-token"
        every { JwtUtils.parseGoogleIdToken("google-token") } returns null
        coEvery { api.getCurrentUser("Bearer google-token") } returns Response.success(
            UserProfileResponse(id = "7", name = "google_1234", email = "alice@example.com")
        )
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        unmockkObject(JwtUtils)
    }

    @Test
    fun `profile refresh preserves the name saved at Google sign in`() {
        every { tokens.getUserName() } returns "Alice Smith"
        val vm = UserViewModel(apiService = api, tokenManager = tokens)
        assertEquals("Alice Smith", vm.uiState.value.name)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Alice Smith", vm.uiState.value.name)
        assertEquals("7", vm.uiState.value.userId)
        assertEquals("alice@example.com", vm.uiState.value.email)
        assertFalse(vm.uiState.value.isLoading)
        verify { tokens.saveUserName("Alice Smith") }
        verify(exactly = 0) { tokens.saveUserName("google_1234") }
    }

    @Test
    fun `Google token name repairs previously overwritten cached username`() {
        every { tokens.getUserName() } returns "google_1234"
        every { JwtUtils.parseGoogleIdToken("google-token") } returns GoogleTokenClaims(name = "Alice Smith")
        val vm = UserViewModel(apiService = api, tokenManager = tokens)
        assertEquals("Alice Smith", vm.uiState.value.name)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Alice Smith", vm.uiState.value.name)
        verify { tokens.saveUserName("Alice Smith") }
    }

    @Test
    fun `server name is used when Google provides no display name`() {
        every { tokens.getUserName() } returns " "
        coEvery { api.getCurrentUser(any()) } returns Response.success(UserProfileResponse(name = "Alice"))
        val vm = UserViewModel(apiService = api, tokenManager = tokens)
        dispatcher.scheduler.advanceUntilIdle()
        assertEquals("Alice", vm.uiState.value.name)
    }
}
