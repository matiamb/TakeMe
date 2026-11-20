package com.gfreeman.takeme.home.viewmodel.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.favs.FavoriteRepository
import com.gfreeman.takeme.home.model.favs.ResultOk
import com.gfreeman.takeme.home.model.map.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Immutable UI State for ArrivedToDestinationScreen.
 */
data class ArrivedToDestinationUiState(
    val startPlace: Place? = null,
    val finishPlace: Place? = null,
    val isFavorite: Boolean = false,
    val showNameDialog: Boolean = false,
    val startPointNameInput: String = "",
    val userMessage: String? = null,
    val errorMessage: String? = null
)

/**
 * HiltViewModel for ArrivedToDestinationScreen.
 */
@HiltViewModel
class ArrivedToDestinationViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArrivedToDestinationUiState())
    val uiState: StateFlow<ArrivedToDestinationUiState> = _uiState.asStateFlow()

    fun setPlaces(start: Place?, finish: Place?) {
        _uiState.update {
            it.copy(
                startPlace = start,
                finishPlace = finish,
                startPointNameInput = start?.displayName ?: ""
            )
        }
    }

    fun onStartPointNameChanged(name: String) {
        _uiState.update { it.copy(startPointNameInput = name) }
    }

    fun toggleFavoriteClick() {
        val currentStart = _uiState.value.startPlace
        val currentFinish = _uiState.value.finishPlace
        val newFavState = !_uiState.value.isFavorite

        if (currentStart == null || currentFinish == null) {
            _uiState.update { it.copy(errorMessage = "Places are null!") }
            return
        }

        // If toggling ON and startPlace has no name, show dialog first
        if (newFavState && (currentStart.displayName.isEmpty() || _uiState.value.startPointNameInput.isEmpty())) {
            _uiState.update { it.copy(showNameDialog = true, isFavorite = true) }
            return
        }

        _uiState.update { it.copy(isFavorite = newFavState) }
        saveOrDeleteFavorite(currentStart, currentFinish, newFavState)
    }

    fun saveFavoriteWithCustomName() {
        val customName = _uiState.value.startPointNameInput.trim()
        val currentStart = _uiState.value.startPlace
        val currentFinish = _uiState.value.finishPlace

        if (currentStart == null || currentFinish == null) return

        val updatedStart = currentStart.copy(displayName = customName)
        _uiState.update {
            it.copy(
                startPlace = updatedStart,
                showNameDialog = false,
                isFavorite = true
            )
        }
        saveOrDeleteFavorite(updatedStart, currentFinish, true)
    }

    fun dismissDialog() {
        _uiState.update { it.copy(showNameDialog = false, isFavorite = false) }
    }

    private fun saveOrDeleteFavorite(start: Place, finish: Place, isFav: Boolean) {
        viewModelScope.launch {
            if (isFav) {
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())

                val result = favoriteRepository.deleteFavoriteRoute(
                    FavoriteRoute(
                        startPlace = start,
                        destinationPlace = finish,
                        date = currentDate
                    ).id
                )
                // Save route to DB
                _uiState.update { it.copy(userMessage = "Route saved to favorites!") }
            } else {
                _uiState.update { it.copy(userMessage = "Route removed from favorites!") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(userMessage = null, errorMessage = null) }
    }
}
