package home.model.map


import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Tasks.await
import contract.MapContract
import java.lang.ref.WeakReference

class MapRepository: MapContract.MapModel {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mCurrentLocation: Point
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest
    private lateinit var settingsClient: SettingsClient
    private var navigationStarted = false
    //creo el nuevo listener, weak reference significa que puede ser borrada su ubicacion de memoria por el garbage collector antes de que termine el problema
    //previene memory leaks
    private lateinit var newLocationListener : WeakReference<OnNewLocationListener>
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
        //await va a esperar a que una tarea especifica termine antes de seguir con el resto del codigo
        await(lastLocation)
        lastKnownLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
        //mCurrentLocation = lastLocation.result
        return lastKnownLocation
        }
    @SuppressLint("MissingPermission")
    override fun startLocationUpdates(context: Context, locationListener: OnNewLocationListener){
        navigationStarted = true
        //inicializo el location listener
        this.newLocationListener = WeakReference(locationListener)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                //super.onLocationResult(locationResult)
                // ?: se llama Elvis Operator, devuelve un valor u otro
//                locationResult ?: return
//                for (location in locationResult.locations){
//                    mCurrentLocation = location
//                    updateMapLocation()
//                    Log.i("Mati", mCurrentLocation.toString())
//                }
                super.onLocationResult(locationResult)
                mCurrentLocation =
                    locationResult.lastLocation.let { Point(it!!.latitude, it.longitude) }
                //aplico el listener a mCurrentLocation
                mCurrentLocation.let {
                    //sobreescribo lo que sea que tenga el listener por mCurrentLocation, va actualizando la location a la que hace referencia
                    newLocationListener.get()?.currentLocationUpdate(it)
                }
            }
        }
        //El intervalo esta en 5 segundos o 100 metros pq por cada request de location me resta uno de la api de maps que no es gratis OJO
        locationRequest = LocationRequest.Builder(5000)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateDistanceMeters(50F)
            .build()
        locationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest).build()
        settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(){ task ->
            if(task.isSuccessful){
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            } else {
                /*if(task.exception is ResolvableApiException){
                   val resolvableApiException: ResolvableApiException = task.exception as ResolvableApiException
                    resolvableApiException.
                }*/
                Log.i("Mati", "Location request failed")
            }
        }
    }

    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        navigationStarted = false
    }

    override fun getResult(search: String): String {
        return "Test text from backend"
    }
    override fun initFusedLocationProviderClient(context: Context){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    override fun updateMapLocation(): Point{
        return mCurrentLocation
    }

    override fun isNavigating(): Boolean{
        return navigationStarted
    }

    //Creo un custom listener
    interface OnNewLocationListener {
        fun currentLocationUpdate(point: Point)
    }

}