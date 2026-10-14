package com.example.group05_flower_api_app

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.group05_flower_api_app.ui.data.LoginRepository
import com.example.group05_flower_api_app.ui.data.Result
import com.example.group05_flower_api_app.ui.data.model.LoggedInUser
import com.example.group05_flower_api_app.ui.ui.login.LoginViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private val loginRepository: LoginRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loginWithGoogle updates loginResult LiveData with success state when repository succeeds`() {
        // Arrange
        val expectedUser = LoggedInUser("user_id_100", "John Doe")
        coEvery { loginRepository.loginWithGoogle("valid_id_token", "John Doe") } returns Result.Success(expectedUser)

        // Act
        viewModel.loginWithGoogle("valid_id_token", "John Doe")
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        val result = viewModel.loginResult.value
        assertNotNull(result)
        assertEquals("John Doe", result?.success?.displayName)
    }

    @Test
    fun `onGoogleLoginFailed updates loginResult LiveData with error state`() {
        // Act
        viewModel.onGoogleLoginFailed("Sign-in canceled")

        // Assert
        val result = viewModel.loginResult.value
        assertNotNull(result)
        assertEquals(R.string.login_failed, result?.error)
    }
}
