package com.example.group05_flower_api_app.data

import com.example.group05_flower_api_app.data.api.ApiClient
import com.example.group05_flower_api_app.data.api.AuthApiService
import com.example.group05_flower_api_app.data.api.GoogleAuthRequest
import com.example.group05_flower_api_app.data.api.TokenManager
import com.example.group05_flower_api_app.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException
import java.io.IOException

/**
 * Data source class that handles authentication requests with remote REST API (Spring Boot)
 * for Google sign-in.
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

    /** Verify with the backend before storing the Google ID token for bearer requests. */
    suspend fun loginWithGoogle(idToken: String, displayName: String): Result<LoggedInUser> =
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.loginWithGoogle(GoogleAuthRequest(idToken = idToken))
                if (!response.isSuccessful) {
                    tokenManager?.clearAuthToken()
                    return@withContext Result.Error(IOException("Sign-in failed (HTTP ${response.code()})"))
                }
                val identity = response.body()
                val subject = identity?.subject
                if (subject.isNullOrBlank()) {
                    tokenManager?.clearAuthToken()
                    return@withContext Result.Error(IOException("Invalid sign-in response from server"))
                }
                tokenManager?.saveAuthToken(idToken)
                val finalName = displayName.ifBlank { identity.email ?: "Google User" }
                tokenManager?.saveUserName(finalName)
                Result.Success(LoggedInUser(
                    userId = subject,
                    displayName = finalName
                ))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                tokenManager?.clearAuthToken()
                Result.Error(IOException("Unable to sign in. Check your connection and try again.", e))
            }
        }

    fun logout() {
        tokenManager?.clearAuthToken()
    }
}
