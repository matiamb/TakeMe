package com.gfreeman.takeme.home.model.map


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
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
import com.gfreeman.takeme.home.model.map.broadcasts.BatteryLowReceiver
import com.gfreeman.takeme.home.model.map.services.RouteCheckService
import com.gfreeman.takeme.home.view.HomeActivity
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
    //Este objeto es la conexion de la activity con mi service
    private var isServiceBound = false
    private lateinit var routeCheckService: RouteCheckService
    private lateinit var  context: Context
    var currentRoute: List<Point>? = null
    private lateinit var routeCheckServiceConnection: ServiceConnection
    private var batteryLowReceiver: BatteryLowReceiver? = null
    private var alarmMgr: AlarmManager? = null
    private var arriveDestinationListener: RouteCheckService.OnArriveDestinationListener? = null

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

    override suspend fun getRoute(startPlace: Place, destination: Place): List<Point>? {
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
        mCurrentLocation = Point(startPlace.point.latitude, startPlace.point.longitude)
        val rawRouteResponse = ApiServiceProvider.routesServiceApi.getRoute(
            getFormatedPoints(startPlace),
            getFormatedPoints(destination)
        )
        if (rawRouteResponse.isSuccessful){
            currentRoute = mapRoute(rawRouteResponse.body()?.route?.geometry?.coordinates)
            Log.i("Mati", "Current route list: ${currentRoute.toString()}")
        } else {
            return emptyList()
        }
        return currentRoute
    }

    private fun getFormatedPoints(place: Place): String =
        "${place.point.latitude}, ${place.point.longitude}"

    private fun mapRoute(coordinates: List<List<Double>>?): List<Point> =
        coordinates?.map{Point(it.first(), it.last())} ?: emptyList()

    @SuppressLint("MissingPermission")
    override fun getCurrentPosition(): Point? {
        //return Point(-34.679437, -58.553777)
        var lastKnownLocation: Point?
        val lastLocation = fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                //Log.i("Mati", "Location request successful")
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Do something with the latitude and longitude
                    //Log.i("Mati", "Location from inside: "+latitude.toString() +" "+ longitude.toString())
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
        //Log.i("Mati", "Last location test: " + lastLocation.result.latitude.toString())
        lastKnownLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
        mCurrentLocation = Point(lastLocation.result.latitude, lastLocation.result.longitude)
        return lastKnownLocation
    }
    @SuppressLint("MissingPermission")
    override fun startLocationUpdates(context: Context, locationListener: OnNewLocationListener){
        navigationStarted = true
        Log.i("Mati", "Map Repo: Is navigation started? " + navigationStarted.toString())
        this.context = context
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
                //super.onLocationResult(locationResult)
                mCurrentLocation =
                    locationResult.lastLocation.let { Point(it!!.latitude, it.longitude) }
                //Log.i("Mati", "$mCurrentLocation")
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
        //setupServiceConnection()
    }

    override fun updateMapLocation(): Point{
        return mCurrentLocation
    }

    override fun isNavigating(): Boolean{
        return navigationStarted
    }
    private fun setupServiceConnection(){
        Log.i("Mati", "Serivce connection started")
        routeCheckServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                Log.i("Mati", "Starting connection")
                val binder = service as RouteCheckService.RouteCheckBinder
                routeCheckService = binder.getService()
                routeCheckService.setLocationProvider(object :
                RouteCheckService.CurrentLocationStatusProvider{
                    override fun getCurrentLocation(): Point? {
                        //Log.i("Mati", "setupServiceConnection: ${mCurrentLocation.latitude}, ${mCurrentLocation.longitude}")
                        return mCurrentLocation
                    }
                    override fun getCurrentRoute(): List<Point>? {
                        Log.i("Mati", "setupServiceConnection currentRoute: $currentRoute")
                        //Devuelve null para getroutefromfavs....
                        return currentRoute
                    }
                })
                routeCheckService.setArriveDestinationListener(object :
                    RouteCheckService.OnArriveDestinationListener {
                    override fun onArriveDestination() {
                        arriveDestinationListener?.onArriveDestination()
                    }
                })
                //routeCheckService.showNotification()
                isServiceBound = true
                //Log.i("Mati", "Service mCurrentLocation: $mCurrentLocation")

            }
            override fun onServiceDisconnected(name: ComponentName?) {
                isServiceBound = false
                Log.i("Mati", "Service disconnected")
            }
        }
        Log.i("Mati", "Service connection finished")
        Log.i("Mati", "Service started? $isServiceBound")
    }

    //aca recibo el context para controlar que no me desvie de la ruta
    override fun startCheckingDistanceToRoute(context: Context){
        //este if es para que si el service esta bindeado, salga de este metodo y no me lo bindee cada vez que entra
        Log.i("Mati", "startCheckingDistanceToRoute MapRepo, isServiceBound? $isServiceBound")
        if(isServiceBound){
            return
        }
        setupServiceConnection()
        //ACA VA EL NOMBRE DE LA CLASE DEL SERVICE!!!! SI NO DA NULL POINTER EXCEPTION
        val intent = Intent(context, RouteCheckService::class.java)
        routeCheckServiceConnection?.let { context.bindService(intent, it, BIND_AUTO_CREATE) }
    }
    override fun stopCheckingDistanceToRoute(context: Context){
        routeCheckServiceConnection?.let { context.unbindService(it) }
        isServiceBound = false
    }

    override fun setArriveDestinationListener(arriveDestinationListener: RouteCheckService.OnArriveDestinationListener?) {
        this.arriveDestinationListener = arriveDestinationListener
    }

    override fun registerRouteAlarm(context: Context) {
        alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context, HomeActivity::class.java)
        alarmMgr!!.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 5000,
            PendingIntent.getActivity(context,0,alarmIntent, PendingIntent.FLAG_IMMUTABLE)
        )
    }

    override fun startCheckingBatteryStatus(context: Context?){
        batteryLowReceiver = BatteryLowReceiver()
        val batteryIntentFilter = IntentFilter(Intent.ACTION_BATTERY_LOW)
        context?.registerReceiver(batteryLowReceiver, batteryIntentFilter)
    }

    //Creo un custom listener
    interface OnNewLocationListener {
        fun currentLocationUpdate(point: Point)
    }
    interface OnArriveDestinationListener {
        fun onArriveDestination()
    }
}