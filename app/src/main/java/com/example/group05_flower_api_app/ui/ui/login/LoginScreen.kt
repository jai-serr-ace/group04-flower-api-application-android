package com.example.group05_flower_api_app.ui.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.group05_flower_api_app.R
import kotlinx.coroutines.launch

/**
 * Top-level Composable for the Login Screen that observes ViewModel state
 * and triggers Google OAuth 2.0 authentication via [GoogleAuthHelper].
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: (LoggedInUserView) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val loginResult by viewModel.loginResult.observeAsState()

    // Observe login authentication state changes
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            isLoading = false
            if (result.success != null) {
                val welcome = context.getString(R.string.welcome)
                Toast.makeText(context, "$welcome ${result.success.displayName}", Toast.LENGTH_LONG).show()
                onLoginSuccess(result.success)
            } else if (result.error != null) {
                val errorMsg = context.getString(result.error)
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginContent(
        isLoading = isLoading,
        onGoogleSignInClick = {
            isLoading = true
            coroutineScope.launch {
                // Trigger Google OAuth 2.0 Credential Manager sign-in prompt
                GoogleAuthHelper.signInWithGoogle(
                    context = context,
                    onSuccess = { idToken, displayName ->
                        viewModel.loginWithGoogle(idToken, displayName)
                    },
                    onFailure = { error ->
                        isLoading = false
                        val message = error.message ?: "Google Sign-In failed"
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        viewModel.onGoogleLoginFailed(error.message)
                    }
                )
            }
        }
    )
}

/**
 * UI layout content for the Login screen displaying the Google Sign-In button and loading indicator.
 */
@Composable
fun LoginContent(
    isLoading: Boolean,
    onGoogleSignInClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Welcome",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(stringResource(R.string.action_sign_in_google))
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        LoginContent(
            isLoading = false,
            onGoogleSignInClick = {}
        )
    }
}
