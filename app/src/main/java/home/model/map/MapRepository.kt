package home.model.map


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Tasks.await
import contract.MapContract

class MapRepository: MapContract.MapModel {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override suspend fun getPlacesFromSearch(placeToSearch: String): List<Place> {
        /*return listOf(
            Place(
                "Abasto, Balvanera, Buenos Aires, Comuna 3, Autonomous City of Buenos Aires, C1193AAF, Argentina",
                Point("-34.6037283".toDouble(), "-58.4125926".toDouble())
            )
        )*/
        return ApiServiceProvider.searchServiceApi.getPlacesFromSearch(placeToSearch = placeToSearch)
            .body()?.map{
            val convertedLong = it.long.toDouble()
            val convertedLat = it.lat.toDouble()
            Place(displayName = it.displayName, point = Point(latitude = convertedLat, longitude = convertedLong))
        }?: emptyList()
    }

    override suspend fun getRoute(startPlace: Place, destination: Place): List<Point> {
        /*return listOf(
            Point(-34.679437, -58.553777),
            Point(
                -34.679217,
                -58.553513
            ),
            Point(
                -34.678996,
                -58.553279
            ),
            Point(
                -34.678119,
                -58.554457
            ),
            Point(
                -34.677234,
                -58.55561
            ),
            Point(
                -34.676409,
                -58.556671
            )
        )*/
        val rawRouteResponse = ApiServiceProvider.routesServiceApi.getRoute(
            getFormatedPoints(startPlace),
            getFormatedPoints(destination)
        )
        return if (rawRouteResponse.isSuccessful){
            mapRoute(rawRouteResponse.body()?.route?.geometry?.coordinates)
        } else {
            emptyList()
        }
    }

    private fun getFormatedPoints(place: Place): String =
        "${place.point.latitude}, ${place.point.longitude}"

    private fun mapRoute(coordinates: List<List<Double>>?): List<Point> =
        coordinates?.map{Point(it.first(), it.last())}?: emptyList()
    @SuppressLint("MissingPermission")
    override fun getCurrentPosition(): Point? {
        //return Point(-34.679437, -58.553777)
            var lastKnownLocation: Point?
                val lastLocation = fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    Log.i("Mati", "Location request successful")
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        // Do something with the latitude and longitude
                        Log.i("Mati", "Location from inside: "+latitude.toString() +" "+ longitude.toString())
                    }
                }
                .addOnFailureListener { exception: Exception ->
                    // Handle location retrieval failure here
                    Log.e("Mati", "Location request failed")
                }
        //pero aca me pone el valor por default, por que? tiene algo que ver con que la task no se completa, y por eso no debe guardar los cambios.
        //Log.i("Mati", lastKnownLocation.latitude.toString() +" "+ lastKnownLocation.longitude.toString())
        /*if (lastLocation.isComplete){
            lastKnownLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
            return lastKnownLocation
        } else {
            await(lastLocation)
            lastKnownLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
            return lastKnownLocation
        }*/
        await(lastLocation)
        lastKnownLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
        return lastKnownLocation
        }

    override fun getResult(search: String): String {
        return "Test text from backend"
    }
    override fun initFusedLocationProviderClient(context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

}