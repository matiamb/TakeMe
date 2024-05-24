package home.presenter.map

import com.google.android.gms.maps.model.LatLng
import contract.BaseContract
import contract.MapContract
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
    override fun attachView(view: MapContract.MapView<BaseContract.IBaseView>) {
        this.mapView = view
    }

    override fun performSearchPlaces(placeToSearch: String) {
        TODO("Buscar corutinas")
        CoroutineScope(Dispatchers.IO).launch {
            val results = mapModel.getPlacesFromSearch(placeToSearch)
            withContext(Dispatchers.Main){
                mapView.showSearchResults(results)
            }
        }
    }

    override fun getRoute(destination: Place) {
        TODO("Not yet implemented")
        CoroutineScope(Dispatchers.IO).launch {
            val startPlace = Place("MyPosition", getCurrentPosition())
            val route = mapModel.getRoute(startPlace, destination).map {
                LatLng(it.latitude, it.longitude)
            }
            withContext(Dispatchers.Main){
                mapView.drawRoute(route)
            }
        }
    }

    override suspend fun getCurrentPosition(): Point {
        TODO("googlear Deferred y metodo async de corrutinas")
        val job: Deferred<Point> = CoroutineScope(Dispatchers.IO).async {
            mapModel.getCurrentPosition()
        }
        return job.await()
    }
}