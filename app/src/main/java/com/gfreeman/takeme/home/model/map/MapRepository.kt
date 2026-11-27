package com.gfreeman.takeme.home.model.map

import android.content.Context
import kotlinx.coroutines.flow.Flow

/**
 * Clean Architecture Repository interface for Map & Navigation operations.
 *
 * MAIN-SAFETY DIRECTIVE:
 * Every suspend method defined in this interface MUST be main-safe.
 */
interface MapRepository {
    val locationFlow: Flow<Point>
    suspend fun getPlacesFromSearch(placeToSearch: String): List<Place>
    suspend fun getRoute(startPlace: Place, destination: Place): List<Point>?
    suspend fun getCurrentPosition(): Point?
    suspend fun initFusedLocationProviderClient()
    suspend fun startLocationUpdates()
    suspend fun stopLocationUpdates()
    suspend fun stopCheckingDistanceToRoute(context: Context)
}
