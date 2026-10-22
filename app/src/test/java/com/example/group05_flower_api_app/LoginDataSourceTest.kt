package com.example.group05_flower_api_app

import com.example.group05_flower_api_app.data.LoginDataSource
import com.example.group05_flower_api_app.data.Result
import com.example.group05_flower_api_app.data.api.AuthApiService
import com.example.group05_flower_api_app.data.api.TokenManager
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import retrofit2.Retrofit
import io.mockk.every
import io.mockk.mockkStatic
import retrofit2.converter.gson.GsonConverterFactory

class LoginDataSourceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authApiService: AuthApiService
    private val tokenManager: TokenManager = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(android.util.Log::class)
        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0

        mockWebServer = MockWebServer()
        mockWebServer.start()

        authApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `loginWithGoogle sends ID token to REST API and saves verified Google token`() = runTest {
        // Arrange
        val expectedTokenResponse = """
            {
                "subject": "user_google_777",
                "issuer": "https://accounts.google.com",
                "audience": ["web-client-id"],
                "email": "alice@example.com"
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(expectedTokenResponse)
                .addHeader("Content-Type", "application/json")
        )

        val dataSource = LoginDataSource(
            apiService = authApiService,
            tokenManager = tokenManager
        )

        // Act
        val result = dataSource.loginWithGoogle(
            idToken = "google_id_token_xyz",
            displayName = "Alice Smith"
        )

        // Assert - Verify HTTP Request sent to Spring Boot endpoint
        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/api/auth/google", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
        assertTrue(recordedRequest.body.readUtf8().contains("google_id_token_xyz"))

        // Assert - Verify TokenManager saved verified Google token
        verify { tokenManager.saveAuthToken("google_id_token_xyz") }

        // Assert - Verify Result Success
        assertTrue(result is Result.Success)
        val user = (result as Result.Success).data
        assertEquals("user_google_777", user.userId)
        assertEquals("Alice Smith", user.displayName)
    }

    @Test
    fun `loginWithGoogle rejects API authentication errors`() = runTest {
        // Arrange
        val mockApi = mockk<AuthApiService>()
        coEvery { mockApi.loginWithGoogle(any()) } returns Response.error(401, mockk(relaxed = true))

        val dataSource = LoginDataSource(
            apiService = mockApi,
            tokenManager = tokenManager
        )

        // Act
        val result = dataSource.loginWithGoogle(
            idToken = "sample_id_token",
            displayName = "Bob"
        )

        // Assert
        assertTrue(result is Result.Error)
        verify(exactly = 0) { tokenManager.saveAuthToken(any()) }
        verify { tokenManager.clearAuthToken() }
    }

    @Test
    fun `connection failure does not log in or store a token`() = runTest {
        val mockApi = mockk<AuthApiService>()
        coEvery { mockApi.loginWithGoogle(any()) } throws java.io.IOException("Connection refused")
        val result = LoginDataSource(mockApi, tokenManager).loginWithGoogle("token", "Bob")
        assertTrue(result is Result.Error)
        verify(exactly = 0) { tokenManager.saveAuthToken(any()) }
    }

    @Test
    fun `unexpected success response does not log in`() = runTest {
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
            .setBody("{}").addHeader("Content-Type", "application/json"))
        val result = LoginDataSource(authApiService, tokenManager).loginWithGoogle("token", "Bob")
        assertTrue(result is Result.Error)
        verify(exactly = 0) { tokenManager.saveAuthToken(any()) }
    }
}
