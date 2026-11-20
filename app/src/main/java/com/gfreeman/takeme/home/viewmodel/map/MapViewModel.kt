package com.gfreeman.takeme.home.viewmodel.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gfreeman.takeme.home.model.map.MapRepository
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.maplibre.android.geometry.LatLng
import javax.inject.Inject

/**
 * Immutable UI State for the Map Screen.
 */
data class MapUiState(
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: List<Place> = emptyList(),
    val routePoints: List<LatLng> = emptyList(),
    val isNavigating: Boolean = false,
    val startPlace: Place? = null,
    val destinationPlace: Place? = null,
    val errorMessage: String? = null,
    val navigateToWeather: Point? = null,
    val userLocation: Point? = null
)

/**
 * HiltViewModel for Map Screen managing map search, routing, and location state.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Performs place search via Retrofit.
     */
    fun performSearchPlaces() {
        val query = _uiState.value.searchQuery.trim()
        if (query.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSearching = true, errorMessage = null) }
            try {
                val results = mapRepository.getPlacesFromSearch(query)
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        searchResults = results,
                        errorMessage = if (results.isEmpty()) "No places found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        errorMessage = "Search failed: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    /**
     * Fetches route coordinates for a selected destination Place.
     */
    fun selectDestination(destination: Place) {
        viewModelScope.launch {
            val currentPos = mapRepository.getCurrentPosition()
            val start = Place("MyPosition", currentPos ?: Point(-31.4201, -64.1888))
            val rawRoute = mapRepository.getRoute(start, destination)

            val latLngRoute = rawRoute?.map { LatLng(it.longitude, it.latitude) } ?: emptyList()

            _uiState.update {
                it.copy(
                    startPlace = start,
                    destinationPlace = destination,
                    routePoints = latLngRoute,
                    isNavigating = latLngRoute.isNotEmpty(),
                    searchResults = emptyList()
                )
            }
        }
    }

    /**
     * Fetches route from pre-defined favorite places.
     */
    fun getRouteFromFavs(startPlace: Place, destination: Place) {
        viewModelScope.launch {
            val rawRoute = mapRepository.getRoute(startPlace, destination)
            val latLngRoute = rawRoute?.map { LatLng(it.longitude, it.latitude) } ?: emptyList()

            _uiState.update {
                it.copy(
                    startPlace = startPlace,
                    destinationPlace = destination,
                    routePoints = latLngRoute,
                    isNavigating = latLngRoute.isNotEmpty()
                )
            }
        }
    }

    /**
     * Cancels active navigation and clears drawn route.
     */
    fun cancelRoute(context: Context) {
        viewModelScope.launch {
            mapRepository.stopLocationUpdates()
            mapRepository.stopCheckingDistanceToRoute(context)
            _uiState.update {
                it.copy(
                    routePoints = emptyList(),
                    isNavigating = false,
                    startPlace = null,
                    destinationPlace = null
                )
            }
        }
    }

    fun clearSearchResults() {
        _uiState.update { it.copy(searchResults = emptyList()) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun getCurrentPosition() {
        viewModelScope.launch {
            val currentPosition = mapRepository.getCurrentPosition()
            _uiState.update {
                it.copy(
                    userLocation = currentPosition
                )
            }
        }
    }
    fun onWeatherFabClicked(){
        viewModelScope.launch {
            val currentPosition = mapRepository.getCurrentPosition() ?: Point(-31.4201, -64.1888)
            _uiState.update {
                it.copy(navigateToWeather = currentPosition)
            }
        }
    }
    fun onWeatherNavigationHandled() {
        _uiState.update { it.copy(navigateToWeather = null) }
    }
    init {
        getCurrentPosition()
    }
}
