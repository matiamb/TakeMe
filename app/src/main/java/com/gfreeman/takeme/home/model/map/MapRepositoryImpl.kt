package com.gfreeman.takeme.home.model.map

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks.await
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of MapRepository for location search and routing.
 */
class MapRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MapRepository {

    private var fusedLocationClient: FusedLocationProviderClient? = LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getPlacesFromSearch(placeToSearch: String): List<Place> = withContext(ioDispatcher) {
        try {
            ApiServiceProvider.searchServiceApi.getPlacesFromSearch(placeToSearch = placeToSearch)
                .body()?.map {
                    val convertedLong = it.long.toDouble()
                    val convertedLat = it.lat.toDouble()
                    Place(displayName = it.displayName, point = Point(latitude = convertedLat, longitude = convertedLong))
                } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getRoute(startPlace: Place, destination: Place): List<Point> = withContext(ioDispatcher) {
        try {
            val myLocation = Point(startPlace.point.latitude, startPlace.point.longitude)
            val destPoint = destination.point
            val coordinates = "${myLocation.longitude},${myLocation.latitude};${destPoint.longitude},${destPoint.latitude}"
            val rawRouteResponse = ApiServiceProvider.routesServiceApi.getRoute(coordinates = coordinates)
            if (rawRouteResponse.isSuccessful) {
                mapRoute(rawRouteResponse.body()?.routes?.firstOrNull()?.geometry?.coordinates)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapRoute(coordinates: List<List<Double>>?): List<Point> =
        coordinates?.map { Point(it.first(), it.last()) } ?: emptyList()

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentPosition(): Point? =
        withContext(ioDispatcher){
            val client = fusedLocationClient ?: return@withContext null//?: Point(-31.4201, -64.1888)
            return@withContext try {
                val lastLocation = client.lastLocation
                await(lastLocation)
                val loc = lastLocation.result
                if (loc != null) {
                    Point(loc.latitude, loc.longitude)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun initFusedLocationProviderClient(context: Context) {
        withContext(ioDispatcher){
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        }
    }

    override suspend fun startLocationUpdates(context: Context) {
        withContext(ioDispatcher){
            initFusedLocationProviderClient(context)
        }
    }

    override suspend fun stopLocationUpdates() {
        // Stop location updates
    }

    override suspend fun stopCheckingDistanceToRoute(context: Context) {
        // Stop checking distance
    }
}
