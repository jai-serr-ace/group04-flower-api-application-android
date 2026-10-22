package com.example.group05_flower_api_app.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.group05_flower_api_app.ui.result.ResultActivity
import com.example.group05_flower_api_app.ui.upload.UploadActivity
import com.example.group05_flower_api_app.ui.user.UserActivity

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = HomeViewModel()

        setContent {
            MaterialTheme {
                HomePage(
                    viewModel = viewModel,
                    onSearch = { query ->
                        val intent = Intent(this, ResultActivity::class.java).apply {
                            putExtra("EXTRA_QUERY", query)
                        }
                        startActivity(intent)
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
}
