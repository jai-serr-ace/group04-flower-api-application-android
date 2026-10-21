package com.example.group05_flower_api_app.ui.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme

import android.widget.Toast

class UserActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = UserViewModel(applicationContext)

        setContent {
            MaterialTheme {
                UserPage(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onManageFlowersClick = {
                        Toast.makeText(this, "Manage Flowers", Toast.LENGTH_SHORT).show()
                    },
                    onManageUsersClick = {
                        Toast.makeText(this, "Manage Users", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}
