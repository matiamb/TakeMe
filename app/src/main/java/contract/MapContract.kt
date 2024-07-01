package contract

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import home.model.map.Place
import home.model.map.Point

interface MapContract {
    interface MapView <T : BaseContract.IBaseView>: FragmentBaseContract.IFragmentBaseView<T>{
        fun showSearchResults(search: List<Place>)
        fun drawRoute(route: List<LatLng>)
        fun showResult(search: String): String
        fun initFusedLocationProviderClient()
    }
    interface IMapPresenter<T: FragmentBaseContract.IFragmentBaseView<*>>: FragmentBaseContract.IBasePresenter<T>{
        fun performSearchPlaces(placeToSearch: String)
        fun getRoute(destination: Place)
        //TODO buscar cual es la funcion del suspend
        suspend fun getCurrentPosition(): Point?
        fun getResult(search: String): String
        fun initFusedLocationProviderClient(context: Context)
    }

    interface MapModel {
        suspend fun getPlacesFromSearch(placeToSearch: String): List<Place>
        suspend fun getRoute(startPlace: Place, destination: Place): List <Point>
        fun getCurrentPosition(): Point?
        fun getResult(search: String): String
        fun initFusedLocationProviderClient(context: Context)
    }
}