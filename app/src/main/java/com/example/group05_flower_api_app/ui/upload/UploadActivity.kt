package com.example.group05_flower_api_app.ui.upload

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme

class UploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = UploadViewModel()

        setContent {
            MaterialTheme {
                UploadPage(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onUploadSuccess = {
                        Toast.makeText(this, "Flower uploaded successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                )
            }
        }
    }
}
