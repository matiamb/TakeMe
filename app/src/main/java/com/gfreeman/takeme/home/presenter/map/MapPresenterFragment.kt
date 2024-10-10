package com.gfreeman.takeme.home.presenter.map

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import contract.BaseContract
import contract.MapContract
import com.gfreeman.takeme.home.model.map.MapRepository
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.model.map.services.RouteCheckService
import com.gfreeman.takeme.home.view.map.ArrivedToDestinationActivity.Companion.EXTRA_FINISH_PLACE
import com.gfreeman.takeme.home.view.map.ArrivedToDestinationActivity.Companion.EXTRA_START_PLACE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapPresenterFragment(private val mapModel: MapContract.MapModel): MapContract.IFragmentMapPresenter<MapContract.MapView<BaseContract.IBaseView>>{
    private lateinit var mapView: MapContract.MapView<BaseContract.IBaseView>
    //inicializo el listener que esta en el repo
    private var locationListener: MapRepository.OnNewLocationListener? = null
    private lateinit var startPlace: Place
    private lateinit var finishPlace: Place
    override fun attachView(view: MapContract.MapView<BaseContract.IBaseView>) {
        this.mapView = view
    }

    override fun performSearchPlaces(placeToSearch: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val results = mapModel.getPlacesFromSearch(placeToSearch)
            withContext(Dispatchers.Main){
                mapView.showSearchResults(results)
            }
        }
        //return mapModel.getPlacesFromSearch(placeToSearch)
    }

    override fun getRoute(destination: Place) {
        //stopCheckingDistanceToRoute()
        CoroutineScope(Dispatchers.IO).launch {
            startPlace = Place("MyPosition", getCurrentPosition()!!)
            finishPlace = destination
            val route = mapModel.getRoute(startPlace, finishPlace)?.map {
                LatLng(it.latitude, it.longitude)
            }
            withContext(Dispatchers.Main){
                if (route != null) {
                    mapView.drawRoute(route)
                }
            }
        }
    }

    override fun getRouteFromFavs(startPlace: Place, destination: Place) {
        this.startPlace = startPlace
        finishPlace = destination
        CoroutineScope(Dispatchers.IO).launch {
            val route = mapModel.getRoute(this@MapPresenterFragment.startPlace, finishPlace)?.map {
                LatLng(it.latitude, it.longitude)
            }
            withContext(Dispatchers.Main){
                if (route != null) {
                    mapView.drawRoute(route)
                }
            }
        }
    }

    override suspend fun getCurrentPosition(): Point? {
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
        startCheckingDistanceToRoute(context)
        mapModel.startCheckingBatteryStatus(context)
        //mapModel.registerRouteAlarm(context)
        //mapView.openCongratsScreen(getCongratsParams())
    }

    override fun stopLocationUpdates() {
        mapModel.stopLocationUpdates()
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
    override fun startCheckingDistanceToRoute(context: Context){
        mapModel.startCheckingDistanceToRoute(context)
        mapModel.setArriveDestinationListener(object : RouteCheckService.OnArriveDestinationListener {
            override fun onArriveDestination() {
                mapView.openCongratsScreen(getCongratsParams())
                stopCheckingDistanceToRoute(context)
                stopLocationUpdates()
            }
        })
    }
    override fun stopCheckingDistanceToRoute(context: Context){
        try {
            mapModel.stopCheckingDistanceToRoute(context)
            mapModel.setArriveDestinationListener(null)
        }
        catch (_: IllegalArgumentException){}
    }
    fun getCongratsParams(): Bundle {
        val congratsParams = Bundle()
        Log.i("Mati", "Is navigating? "+ mapModel.isNavigating().toString())
        Log.i("Mati", "getCongratsParams start, startPlace: ${startPlace.displayName}, finishPlace: ${finishPlace.displayName}")
        if (mapModel.isNavigating()) {
                congratsParams.putSerializable(
                    EXTRA_START_PLACE,
                    Place(
                        displayName = startPlace.displayName,
                        point = Point(startPlace.point.latitude, startPlace.point.longitude)
                    )
                )
                congratsParams.putSerializable(
                    EXTRA_FINISH_PLACE,
                    Place(
                        displayName = finishPlace.displayName,
                        point = Point(finishPlace.point.latitude, finishPlace.point.longitude)
                    )
                )
            }
        return congratsParams
    }
}