package com.example.group05_flower_api_app.ui.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.group05_flower_api_app.ui.ui.login.LoginActivity

class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.loadFlowers()

        setContent {
            MaterialTheme {
                HomePage(
                    viewModel = viewModel,
                    username = intent.getStringExtra(EXTRA_USERNAME) ?: "Guest",
                    onSearch = { query ->
                        viewModel.loadFlowers(query.ifBlank { null })
                    },
                    onLogout = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
    }
}
