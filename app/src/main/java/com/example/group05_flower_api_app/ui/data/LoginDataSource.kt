package com.example.group05_flower_api_app.ui.data

import android.util.Log
import com.example.group05_flower_api_app.ui.data.api.ApiClient
import com.example.group05_flower_api_app.ui.data.api.AuthApiService
import com.example.group05_flower_api_app.ui.data.api.GoogleAuthRequest
import com.example.group05_flower_api_app.ui.data.api.TokenManager
import com.example.group05_flower_api_app.ui.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Data source class that handles authentication requests with remote REST API (Spring Boot)
 * or local fallback.
 */
class LoginDataSource(
    private val apiService: AuthApiService = ApiClient.authApiService,
    private val tokenManager: TokenManager? = null
) {

    suspend fun login(username: String, password: String): Result<LoggedInUser> =
        withContext(Dispatchers.IO) {
            try {
                // Fake/placeholder login
                val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), username)
                Result.Success(fakeUser)
            } catch (e: Throwable) {
                Result.Error(IOException("Error logging in", e))
            }
        }

    /**
     * Authenticates the user by sending Google OAuth 2.0 ID Token to the Spring Boot REST API (`POST /api/auth/google`).
     * On success, saves the returned session token into [TokenManager].
     */
    suspend fun loginWithGoogle(idToken: String, displayName: String): Result<LoggedInUser> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginWithGoogle(GoogleAuthRequest(idToken = idToken))
                if (response.isSuccessful && response.body() != null) {
                    val authData = response.body()!!
                    Log.d("AUTH_TEST", "Backend Session Token received: ${authData.token}")

                    // Securely store the returned Spring Boot JWT token
                    tokenManager?.saveAuthToken(authData.token)

                    val user = LoggedInUser(
                        userId = authData.userId,
                        displayName = authData.displayName.ifBlank { displayName }
                    )
                    Result.Success(user)
                } else {
                    // Fallback for local testing if Spring Boot backend is not running
                    val fallbackUser = LoggedInUser(
                        userId = idToken.takeLast(10).ifBlank { java.util.UUID.randomUUID().toString() },
                        displayName = displayName.ifBlank { "Google User" }
                    )
                    Result.Success(fallbackUser)
                }
            } catch (e: Throwable) {
                // Fallback for local dev/testing if offline or connection refused
                val fallbackUser = LoggedInUser(
                    userId = idToken.takeLast(10).ifBlank { java.util.UUID.randomUUID().toString() },
                    displayName = displayName.ifBlank { "Google User" }
                )
                Result.Success(fallbackUser)
            }
        }

    fun logout() {
        tokenManager?.clearAuthToken()
    }
}
