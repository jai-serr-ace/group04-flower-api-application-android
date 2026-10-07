package com.example.group05_flower_api_app.ui.data

import com.example.group05_flower_api_app.ui.data.model.LoggedInUser
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            // TODO: handle loggedInUser authentication
            val fakeUser = LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
            return Result.Success(fakeUser)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    /**
     * Authenticates the user using Google OAuth 2.0 ID Token credentials.
     * In a production app, the ID Token is typically sent to your backend server for cryptographic verification.
     */
    fun loginWithGoogle(idToken: String, displayName: String): Result<LoggedInUser> {
        try {
            val user = LoggedInUser(
                userId = idToken.takeLast(10).ifBlank { java.util.UUID.randomUUID().toString() },
                displayName = displayName.ifBlank { "Google User" }
            )
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in with Google", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}
