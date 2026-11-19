package com.gfreeman.takeme.home.viewmodel.profile

import androidx.lifecycle.ViewModel
import com.gfreeman.takeme.home.model.profile.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/**
 * Immutable UI State for the Profile Screen.
 */
data class ProfileUiState(
    val name: String = "",
    val email: String = ""
)

/**
 * HiltViewModel managing Profile Screen state and actions.
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        val user = profileRepository.getUserData()
        _uiState.update {
            it.copy(
                name = user.name,
                email = user.email
            )
        }
    }

    /**
     * Clears session preferences.
     */
    fun logOut() {
        profileRepository.logOut()
    }
}
