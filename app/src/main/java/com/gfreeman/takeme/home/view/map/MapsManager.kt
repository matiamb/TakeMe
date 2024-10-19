package com.gfreeman.takeme.home.view.map

import android.content.Context
import android.util.Log
import com.gfreeman.takeme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.gfreeman.takeme.home.model.map.Point
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object MapsManager {
    private const val DISTANCE_TO_BE_OUT_OF_ROUTE = 100
    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
        // Convert latitude and longitude to radians
        val lat1 = latLng1.latitude * PI / 180.0
        val long1 = latLng1.longitude * PI / 180.0
        val lat2 = latLng2.latitude * PI / 180.0
        val long2 = latLng2.longitude * PI / 180.0

        // Calculate difference in longitude
        val dLon = (long2 - long1)

        // Calculate components of the bearing formula
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        // Calculate bearing using atan2
        var brng = atan2(y, x)
        brng = Math.toDegrees(brng)

        // Ensure bearing is within range 0 to 360 degrees
        brng = (brng + 360) % 360

        return brng
    }
    fun addRouteToMap(safeContext: Context, googleMap: GoogleMap, route: List<LatLng>){
        googleMap.clear()
        val polyLineOptions = PolylineOptions()
            .addAll(route)
            .color(safeContext.getColor(R.color.md_theme_dark_inversePrimary))
            .width(30f)
        googleMap.addPolyline(polyLineOptions)
    }
    fun alignMapToRoute(googleMap: GoogleMap, route: List<LatLng>){
        val bearing = if (route.size >= 2){
            val startPoint = route[0]
            val endPoint = route[1]
            bearingBetweenLocations(startPoint, endPoint).toFloat()
        } else {
            0f
        }
        val cameraPosition = CameraPosition.Builder()
            .target(route.first())
            .zoom(18f)
            .bearing(bearing)
            .tilt(0f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
    fun centerMapIntoLocation(googleMap: GoogleMap, initialPoint: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(initialPoint) // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null)
    }
    fun addMarkerToMap(googleMap: GoogleMap, initialPoint: LatLng, title: String = "No title") {
        googleMap.addMarker(
            MarkerOptions()
                .position(initialPoint)
                .title(title)
        )
    }
    fun calculateDistance(point1: Point, point2: Point): Double {
        val earthRadius = 6371 // Earth's radius in kilometers
        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1
        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distanceInKm = earthRadius * c // Distance in kilometers
        return distanceInKm * 1000
    }
    fun isOutsideOfRoute(currentPoint: Point, route: List<Point>): Boolean {
        val nearestPoint = if (route.isEmpty()) -1 else findNearestPointIndex(
            currentPoint,
            route.map { LatLng(it.latitude, it.longitude) })
        return nearestPoint >= 0 && calculateDistance(
            currentPoint,
            route[nearestPoint]
        ) > DISTANCE_TO_BE_OUT_OF_ROUTE
    }
    private fun findNearestPointIndex(userLocation: Point, route: List<LatLng>): Int {
        var nearestIndex = -1
        var shortestDistance = Double.MAX_VALUE
        for (i in route.indices) {
            val distance =
                calculateDistance(userLocation, Point(route[i].latitude, route[i].longitude))
            //Log.i("Mati", "Distance from MapsManager: $distance/m")
            if (distance < shortestDistance) {
                shortestDistance = distance
                nearestIndex = i
            }
        }
        return nearestIndex
    }
}