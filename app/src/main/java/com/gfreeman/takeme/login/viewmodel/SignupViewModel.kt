package com.gfreeman.takeme.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gfreeman.takeme.login.model.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Immutable UI State for Signup Screen.
 */
data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSignupSuccess: Boolean = false
)

/**
 * HiltViewModel for Signup Screen.
 */
@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun signUp() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Email or password cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val success = authRepository.signup(email, password)
            if (success) {
                authRepository.saveSession()
                _uiState.update { it.copy(isLoading = false, isSignupSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Signup failed. Try again.") }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
