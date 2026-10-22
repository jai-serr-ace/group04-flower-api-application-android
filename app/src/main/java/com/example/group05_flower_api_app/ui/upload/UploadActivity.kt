package com.example.group05_flower_api_app.ui.upload

import android.os.Bundle
import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.group05_flower_api_app.ui.result.ResultActivity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme

class UploadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this, viewModelFactory {
            initializer { UploadViewModel(applicationContext) }
        })[UploadViewModel::class.java]

        setContent {
            MaterialTheme {
                UploadPage(
                    viewModel = viewModel,
                    onBackClick = { finish() },
                    onUploadSuccess = {
                        Toast.makeText(this, "Flower uploaded successfully!", Toast.LENGTH_SHORT).show()
                        viewModel.uiState.value.uploadedFlowerId?.let { flowerId ->
                            startActivity(Intent(this, ResultActivity::class.java).apply {
                                putExtra("EXTRA_FLOWER_ID", flowerId)
                            })
                        }
                        finish()
                    }
                )
            }
        }
    }
}
