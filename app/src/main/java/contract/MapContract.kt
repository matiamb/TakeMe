package contract

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.gfreeman.takeme.home.model.map.MapRepository.OnNewLocationListener
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.model.map.services.RouteCheckService

interface MapContract {
    interface MapView <T : BaseContract.IBaseView>: FragmentBaseContract.IFragmentBaseView<T>{
        fun showSearchResults(search: List<Place>)
        fun drawRoute(route: List<LatLng>)
        fun showResult(search: String): String
        fun initFusedLocationProviderClient()
        fun startLocationUpdates()
        fun stopLocationUpdates()
        fun getLastLocation(myLocation: LatLng)
        fun updateMapLocation(location: Point)
        fun openCongratsScreen()
        fun cleanMap()
    }
    interface IMapPresenter<T: FragmentBaseContract.IFragmentBaseView<*>>: FragmentBaseContract.IBasePresenter<T>{
        fun performSearchPlaces(placeToSearch: String)
        fun getRoute(destination: Place)
        suspend fun getCurrentPosition(): Point?
        fun getResult(search: String): String
        fun initFusedLocationProviderClient(context: Context)
        fun startLocationUpdates(context: Context)
        fun getLastLocation()
        fun updateMapLocation()
        fun startCheckingDistanceToRoute(context: Context)
        fun stopCheckingDistanceToRoute(context: Context)
        fun stopLocationUpdates()
    }

    interface MapModel {
        suspend fun getPlacesFromSearch(placeToSearch: String): List<Place>
        suspend fun getRoute(startPlace: Place, destination: Place): List<Point>?
        fun getCurrentPosition(): Point?
        fun getResult(search: String): String
        fun initFusedLocationProviderClient(context: Context)
        fun startLocationUpdates(context: Context, locationListener: OnNewLocationListener)
        fun stopLocationUpdates()
        fun updateMapLocation(): Point
        fun isNavigating(): Boolean
        fun startCheckingDistanceToRoute(context: Context)
        fun stopCheckingDistanceToRoute(context: Context)
        fun registerRouteAlarm(context: Context)
        fun startCheckingBatteryStatus(context: Context?)
        fun setArriveDestinationListener(arriveDestinationListener: RouteCheckService.OnArriveDestinationListener?)
    }
}