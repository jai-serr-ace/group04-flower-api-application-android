package com.example.group05_flower_api_app.ui.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.group05_flower_api_app.ui.ui.home.HomeActivity

class LoginActivity : ComponentActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        setContent {
            MaterialTheme {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = { _ ->
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}
