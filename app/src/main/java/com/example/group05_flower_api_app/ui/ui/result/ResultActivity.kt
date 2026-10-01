package com.example.group05_flower_api_app.ui.ui.result

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.group05_flower_api_app.R

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge();

        val viewModel = ResultViewModel()

        setContent{
            MaterialTheme{
                ResultPage(viewModel = viewModel)
            }
        }

    }
}