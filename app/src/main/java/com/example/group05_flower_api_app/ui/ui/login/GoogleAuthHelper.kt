package com.example.group05_flower_api_app.ui.ui.login

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.group05_flower_api_app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * Helper object that integrates Android's Credential Manager API (`androidx.credentials`)
 * and Google Identity library (`googleid`) to perform Google OAuth 2.0 Sign-In.
 */
object GoogleAuthHelper {
    private const val TAG = "GoogleAuthHelper"

    /**
     * Recursively traverses context wrappers to locate the hosting [Activity].
     * Credential Manager requires an Activity context to render the Google Sign-In bottom sheet dialog.
     */
    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }

    /**
     * Launches the Google OAuth 2.0 Credential Manager sign-in prompt.
     *
     * @param context The UI context (must resolve to an [Activity]).
     * @param onSuccess Callback invoked when authentication succeeds with a valid ID Token and user display name.
     * @param onFailure Callback invoked when sign-in fails or is canceled by the user.
     */
    suspend fun signInWithGoogle(
        context: Context,
        onSuccess: (idToken: String, displayName: String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val activity = context.findActivity()
        if (activity == null) {
            Log.e(TAG, "Context is not an Activity")
            onFailure(IllegalStateException("Context is not an Activity"))
            return
        }

        val credentialManager = CredentialManager.create(activity)
        val webClientId = activity.getString(R.string.default_web_client_id)

        // Validate that the placeholder Web Client ID has been replaced with a real Google Cloud Web Client ID
        if (webClientId.contains("YOUR_WEB_CLIENT_ID")) {
            val msg = "Please replace default_web_client_id in strings.xml with your Google Cloud Web Client ID."
            Log.e(TAG, msg)
            onFailure(IllegalStateException(msg))
            return
        }

        // Configure Google Sign-In options using the Web Client ID as the server audience
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            // Request credentials via Android Credential Manager
            val result = credentialManager.getCredential(
                context = activity,
                request = request
            )

            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                try {
                    // Extract Google ID Token and User Information
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val displayName = googleIdTokenCredential.displayName
                        ?: googleIdTokenCredential.givenName
                        ?: googleIdTokenCredential.id
                    onSuccess(idToken, displayName)
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received invalid Google ID token response", e)
                    onFailure(e)
                }
            } else {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                onFailure(IllegalArgumentException("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException during Google sign-in: ${e.message}", e)
            onFailure(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception during Google sign-in: ${e.message}", e)
            onFailure(e)
        }
    }
}
