package home.presenter.map

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import contract.BaseContract
import contract.MapContract
import home.model.map.MapRepository
import home.model.map.Place
import home.model.map.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapPresenter(private val mapModel: MapContract.MapModel): MapContract.IMapPresenter<MapContract.MapView<BaseContract.IBaseView>>{
    private lateinit var mapView: MapContract.MapView<BaseContract.IBaseView>
    //inicializo el listener que esta en el repo
    private var locationListener: MapRepository.OnNewLocationListener? = null
    override fun attachView(view: MapContract.MapView<BaseContract.IBaseView>) {
        this.mapView = view
    }

    override fun performSearchPlaces(placeToSearch: String) {
        //TODO("Buscar corutinas")
        CoroutineScope(Dispatchers.IO).launch {
            val results = mapModel.getPlacesFromSearch(placeToSearch)
            withContext(Dispatchers.Main){
                mapView.showSearchResults(results)
            }
        }
        //return mapModel.getPlacesFromSearch(placeToSearch)
    }

    override fun getRoute(destination: Place) {
        //TODO("Not yet implemented")
        //stopCheckingDistanceToRoute()
        CoroutineScope(Dispatchers.IO).launch {
            val startPlace = Place("MyPosition", getCurrentPosition()!!)
            val route = mapModel.getRoute(startPlace, destination).map {
                LatLng(it.latitude, it.longitude)
            }
            withContext(Dispatchers.Main){
                mapView.drawRoute(route)
            }
        }
    }

    override suspend fun getCurrentPosition(): Point? {
        //TODO("googlear Deferred y metodo async de corrutinas")
        //DEFERRED ES UN JOB QUE DEVUELVE UN RESULTADO
        val job: Deferred<Point?> = CoroutineScope(Dispatchers.IO).async {
            mapModel.getCurrentPosition()
        }
        //Al usar async puedo usar await o join, si uso launch uso join para esperar que termine el job
        return job.await()
    }

    override fun getResult(search: String): String {
        return mapModel.getResult(search)
    }

    override fun initFusedLocationProviderClient(context: Context){
        //val context = mapView.getParentView().getViewContext()
        mapModel.initFusedLocationProviderClient(context)
    }

    override fun startLocationUpdates(context: Context) {
        startCheckingDistanceToRoute()
        //inicializo el locationListener como un objeto del repo (IMPORTANTE SABER ESTO) y sobrescribo el metodo del listener
        locationListener = object : MapRepository.OnNewLocationListener {
            override fun currentLocationUpdate(point: Point) {
                when (mapModel.isNavigating()) {
                    //si el metodo isNavigating devuelve falso, hago lo de abajo
                    false -> {
                        // Do nothing
                    }
                    //Si da verdadero, corre el metodo de abajo
                    true -> updateMapLocation()
                }
            }
        }
        //esto no entiendo bien, formatea locationListener para decirle que metodo escuchar?
        locationListener?.let { mapModel.startLocationUpdates(context, it) }
        //mapModel.startLocationUpdates(context)
    }

    override fun stopLocationUpdates() {
        mapModel.stopLocationUpdates()
        stopCheckingDistanceToRoute()
    }

    override fun getLastLocation(){
        CoroutineScope(Dispatchers.IO).launch {
            val location = getCurrentPosition()!!
            val myLocation = LatLng(location.latitude, location.longitude)
            withContext(Dispatchers.Main){
                mapView.getLastLocation(myLocation)
            }
        }
    }

    override fun updateMapLocation() {
        mapView.updateMapLocation(mapModel.updateMapLocation())
    }
    override fun startCheckingDistanceToRoute(){
        mapView.getParentView()?.let { mapModel.stopCheckingDistanceToRoute(it.getViewContext()) }
        Log.i("Mati", "Service started")
    }
    override fun stopCheckingDistanceToRoute(){
        mapView.getParentView()?.let { mapModel.stopCheckingDistanceToRoute(it.getViewContext()) }
    }
}