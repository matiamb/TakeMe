package com.gfreeman.takeme.home.view.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.toColorInt
import com.gfreeman.takeme.R
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.google.android.gms.maps.model.PolylineOptions
import com.gfreeman.takeme.home.model.map.Point
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.LocationComponentOptions
import org.maplibre.android.location.modes.CameraMode
import org.maplibre.android.location.modes.RenderMode
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object MapsManager {
    private const val DISTANCE_TO_BE_OUT_OF_ROUTE = 100
//    private fun bearingBetweenLocations(latLng1: LatLng, latLng2: LatLng): Double {
//        // Convert latitude and longitude to radians
//        val lat1 = latLng1.latitude * PI / 180.0
//        val long1 = latLng1.longitude * PI / 180.0
//        val lat2 = latLng2.latitude * PI / 180.0
//        val long2 = latLng2.longitude * PI / 180.0
//
//        // Calculate difference in longitude
//        val dLon = (long2 - long1)
//
//        // Calculate components of the bearing formula
//        val y = sin(dLon) * cos(lat2)
//        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
//
//        // Calculate bearing using atan2
//        var brng = atan2(y, x)
//        brng = Math.toDegrees(brng)
//
//        // Ensure bearing is within range 0 to 360 degrees
//        brng = (brng + 360) % 360
//
//        return brng
//    }
    fun addRouteToMap(route: List<LatLng>, currentStyle: Style, context: Context){
        //currentMap.clear()
        if (route.isEmpty()){
            return
        }
        val routeCoordinates = route.map { coordinate ->
//            Point.fromLngLat(
//                coordinate[0],
//                coordinate[1]
//            )
            org.maplibre.geojson.Point.fromLngLat(
                coordinate.longitude,
                coordinate.latitude
            )
        }

        val lineString = LineString.fromLngLats(routeCoordinates)

        // Fuente GeoJSON
        val geoJsonSource = GeoJsonSource(
            "route-source",
            Feature.fromGeometry(lineString)
        )

        // Eliminar ruta anterior si existe
        currentStyle.removeLayer("route-layer")
        currentStyle.removeSource("route-source")
        currentStyle.removeLayer("destination-layer")
        currentStyle.removeSource("destination-source")

        currentStyle.addSource(geoJsonSource)
        // Dibujar línea
        val lineLayer = LineLayer(
            "route-layer",
            "route-source"
        ).withProperties(
            PropertyFactory.lineColor("#2878FF"),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND)
        )

        currentStyle.addLayer(lineLayer)

    if (currentStyle.getImage("destination-icon") == null) {
        ContextCompat.getDrawable(context, R.drawable.baseline_location_pin_24)?.let { drawable ->
            val tinted = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(tinted, "#FF504AD3".toColorInt())
            currentStyle.addImage("destination-icon", tinted)
        }
    }

    val endPoint = routeCoordinates.last()
    currentStyle.addSource(
        GeoJsonSource("destination-source", Feature.fromGeometry(endPoint))
    )

    val symbolLayer = SymbolLayer("destination-layer", "destination-source").withProperties(
        PropertyFactory.iconImage("destination-icon"),
        PropertyFactory.iconAnchor(Property.ICON_ANCHOR_BOTTOM),
        PropertyFactory.iconSize(1.5f),
        PropertyFactory.iconAllowOverlap(true),
        PropertyFactory.iconIgnorePlacement(true)
    )
    currentStyle.addLayer(symbolLayer)

}
//    fun alignMapToRoute(googleMap: GoogleMap, route: List<LatLng>){
//        val bearing = if (route.size >= 2){
//            val startPoint = route[0]
//            val endPoint = route[1]
//            bearingBetweenLocations(startPoint, endPoint).toFloat()
//        } else {
//            0f
//        }
//        val cameraPosition = CameraPosition.Builder()
//            .target(route.first())
//            .zoom(18f)
//            .bearing(bearing)
//            .tilt(0f)
//            .build()
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//    }
//    fun centerMapIntoLocation(googleMap: GoogleMap, initialPoint: LatLng) {
//        val cameraPosition = CameraPosition.Builder()
//            .target(initialPoint) // Sets the center of the map to Mountain View
//            .zoom(17f)            // Sets the zoom
//            .bearing(90f)         // Sets the orientation of the camera to east
//            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
//            .build()
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 500, null)
//    }
//    fun addMarkerToMap(googleMap: GoogleMap, initialPoint: LatLng, title: String = "No title") {
//        googleMap.addMarker(
//            MarkerOptions()
//                .position(initialPoint)
//                .title(title)
//        )
//    }
fun fitCameraToRoute(map: MapLibreMap, route: List<LatLng>, context: Context) {
    if (route.isEmpty()) return

    // Release tracking mode so LocationComponent doesn't fight the camera zoom-out
    if (map.locationComponent.isLocationComponentActivated) {
        map.locationComponent.cameraMode = CameraMode.NONE
    }

    if (route.size == 1) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(route.first(), 15.0))
        return
    }

    val bounds = LatLngBounds.Builder()
        .includes(route)
        .build()

    val density = context.resources.displayMetrics.density
    val sidePadding = (64 * density).toInt()
    val topPadding = (110 * density).toInt() // Extra space so SearchBar doesn't cover the route
    val bottomPadding = (64 * density).toInt()

    map.animateCamera(
        CameraUpdateFactory.newLatLngBounds(
            bounds,
            sidePadding,
            topPadding,
            sidePadding,
            bottomPadding
        ),
        1000 // smooth 1-second animation
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
//    fun isOutsideOfRoute(currentPoint: Point, route: List<Point>): Boolean {
//        val nearestPoint = if (route.isEmpty()) -1 else findNearestPointIndex(
//            currentPoint,
//            route.map { LatLng(it.latitude, it.longitude) })
//        return nearestPoint >= 0 && calculateDistance(
//            currentPoint,
//            route[nearestPoint]
//        ) > DISTANCE_TO_BE_OUT_OF_ROUTE
//    }
//    private fun findNearestPointIndex(userLocation: Point, route: List<LatLng>): Int {
//        var nearestIndex = -1
//        var shortestDistance = Double.MAX_VALUE
//        for (i in route.indices) {
//            val distance =
//                calculateDistance(userLocation, Point(route[i].latitude, route[i].longitude))
//            //Log.i("Mati", "Distance from MapsManager: $distance/m")
//            if (distance < shortestDistance) {
//                shortestDistance = distance
//                nearestIndex = i
//            }
//        }
//        return nearestIndex
//    }
    /**
     * Enables MapLibre GL location puck (blue dot) and pulsing ring.
     */
    @SuppressLint("MissingPermission")
    fun enableLocationComponent(map: MapLibreMap, style: Style, context: Context) {
        val hasFine = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasCoarse = androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) return

        try {
            val locationComponent = map.locationComponent

            if (!locationComponent.isLocationComponentActivated) {
                val options = LocationComponentOptions.builder(context)
                    .pulseEnabled(true)
                    .build()

                val activationOptions = LocationComponentActivationOptions
                    .builder(context, style)
                    .locationComponentOptions(options)
                    .build()

                locationComponent.activateLocationComponent(activationOptions)
            }

            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.NORMAL
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun centerOnUserLocation(
        map: MapLibreMap,
        fallbackLocation: Point?,
        context: Context
    ): Boolean {
        if (!PermissionUtils.hasLocationPermission(context)){
            return false
        }
        val locationComponent = map.locationComponent
        val liveLocation = if (locationComponent.isLocationComponentActivated){
            locationComponent.lastKnownLocation
        } else { null }
        val target = when{
            liveLocation != null -> {
                LatLng(liveLocation.latitude, liveLocation.longitude)
            }
            fallbackLocation != null -> {
                LatLng(fallbackLocation.latitude, fallbackLocation.longitude)
            }
            else -> {
                return false
            }
        }
        if (locationComponent.isLocationComponentActivated && liveLocation != null){
            if (locationComponent.cameraMode == CameraMode.TRACKING){
                locationComponent.zoomWhileTracking(
                    15.5,
                    600L
                )
            } else {
                locationComponent.setCameraMode(
                    CameraMode.TRACKING,
                    600L,
                    15.5,
                    null,
                    null,
                    null
                )
            }
        } else {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(target, 15.5),
                600
            )
        }
        return true
    }
    fun clearRoute(currentStyle: Style){
        currentStyle.removeLayer("route-layer")
        currentStyle.removeSource("route-source")
        currentStyle.removeLayer("destination-layer")
        currentStyle.removeSource("destination-source")
    }
}