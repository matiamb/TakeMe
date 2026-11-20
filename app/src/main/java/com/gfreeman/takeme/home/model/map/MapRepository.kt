package com.gfreeman.takeme.home.model.map

import android.content.Context

/**
 * Clean Architecture Repository interface for Map & Navigation operations.
 *
 * MAIN-SAFETY DIRECTIVE:
 * Every suspend method defined in this interface MUST be main-safe.
 */
interface MapRepository {
    suspend fun getPlacesFromSearch(placeToSearch: String): List<Place>
    suspend fun getRoute(startPlace: Place, destination: Place): List<Point>?
    suspend fun getCurrentPosition(): Point?
    suspend fun initFusedLocationProviderClient(context: Context)
    suspend fun startLocationUpdates(context: Context)
    suspend fun stopLocationUpdates()
    suspend fun stopCheckingDistanceToRoute(context: Context)
}
