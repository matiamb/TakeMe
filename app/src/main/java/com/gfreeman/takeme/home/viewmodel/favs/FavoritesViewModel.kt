package com.gfreeman.takeme.home.viewmodel.favs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gfreeman.takeme.home.model.db.entities.FavoriteRoute
import com.gfreeman.takeme.home.model.favs.FavoriteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State representing the state of the Favorites Screen.
 * In Compose, UI is driven by immutable State data classes.
 */
data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<FavoriteRoute> = emptyList()
)

/**
 * HiltViewModel for Favorites.
 *
 * WHY HILT & MVVM:
 * 1. @HiltViewModel injects FavoriteRepository automatically without needing Application Context in constructor.
 * 2. Extends clean ViewModel instead of AndroidViewModel.
 * 3. Reactive StateFlow emitted to Compose UI.
 */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoriteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    /**
     * Loads saved favorite routes from Repository asynchronously.
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val list = repository.getFavorites()
            _uiState.update {
                it.copy(
                    isLoading = false,
                    favorites = list
                )
            }
        }
    }

    /**
     * Deletes a favorite route from Repository and updates the UI state.
     */
    fun deleteFavoriteRoute(route: FavoriteRoute) {
        viewModelScope.launch {
            repository.deleteFavoriteRoute(route.id)
            val updatedList = repository.getFavorites()
            _uiState.update { it.copy(favorites = updatedList) }
        }
    }
}
