package com.example.group05_flower_api_app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.group05_flower_api_app.ui.upload.UploadActivity
import com.example.group05_flower_api_app.ui.user.UserActivity
import androidx.activity.viewModels

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
                        viewModel.loadFlowers(query.trim().ifBlank { null })
                    },
                    onUploadClick = {
                        val intent = Intent(this, UploadActivity::class.java)
                        startActivity(intent)
                    },
                    onUserClick = {
                        val intent = Intent(this, UserActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
    }
}
