package com.example.group05_flower_api_app.ui.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.group05_flower_api_app.R
import com.example.group05_flower_api_app.ui.data.LoginRepository
import com.example.group05_flower_api_app.ui.data.Result

/**
 * ViewModel managing login state and authentication requests (including Google OAuth 2.0).
 */
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>().apply {
        value = LoginFormState(isDataValid = true)
    }
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        val result = loginRepository.login(username, password)
        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    /**
     * Handles Google OAuth 2.0 login result by delegating to [LoginRepository] and posting UI state.
     */
    fun loginWithGoogle(idToken: String, displayName: String) {
        val result = loginRepository.loginWithGoogle(idToken, displayName)
        if (result is Result.Success) {
            _loginResult.value = LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
    }

    /**
     * Handles Google OAuth 2.0 sign-in failure.
     */
    fun onGoogleLoginFailed(errorMessage: String? = null) {
        _loginResult.value = LoginResult(error = R.string.login_failed)
    }

    fun loginDataChanged(username: String, password: String) {
        _loginForm.value = LoginFormState(isDataValid = true)
    }
}
