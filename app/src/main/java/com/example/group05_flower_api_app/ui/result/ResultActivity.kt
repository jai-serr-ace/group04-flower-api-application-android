package com.example.group05_flower_api_app.ui.result

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val query = intent.getStringExtra("EXTRA_QUERY") ?: ""
        val flowerId = intent.getStringExtra("EXTRA_FLOWER_ID")
        val viewModel = androidx.lifecycle.ViewModelProvider(this)[ResultViewModel::class.java].apply {
            loadFlowerDetails(query, flowerId)
        }

        setContent {
            MaterialTheme {
                ResultPage(
                    viewModel = viewModel,
                    query = query,
                    onBackClick = { finish() },
                    onRetry = { viewModel.loadFlowerDetails(query, flowerId) }
                )
            }
        }
    }
}
