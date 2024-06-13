package home.view.map

import android.content.Context
import com.gfreeman.takeme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.lang.invoke.TypeDescriptor.OfField
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

object MapsManager {
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
        } else 0f
        val cameraPosition = CameraPosition.Builder()
            .target(route[0])
            .zoom(18f)
            .bearing(bearing)
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
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2500, null)
    }
    fun addMarkerToMap(googleMap: GoogleMap, initialPoint: LatLng, title: String = "No title") {
        googleMap.addMarker(
            MarkerOptions()
                .position(initialPoint)
                .title(title)
        )
    }
}