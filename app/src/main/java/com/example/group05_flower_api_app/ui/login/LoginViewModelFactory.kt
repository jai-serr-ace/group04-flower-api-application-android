package com.example.group05_flower_api_app.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.group05_flower_api_app.data.LoginDataSource
import com.example.group05_flower_api_app.data.LoginRepository
import com.example.group05_flower_api_app.data.api.TokenManager

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Accepts optional [Context] to initialize [TokenManager] for secure token storage.
 */
class LoginViewModelFactory(private val context: Context? = null) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            val tokenManager = context?.let { TokenManager(it.applicationContext) }
            return LoginViewModel(
                loginRepository = LoginRepository(
                    dataSource = LoginDataSource(tokenManager = tokenManager)
                )
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
