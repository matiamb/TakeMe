package contract

import com.google.android.gms.maps.model.LatLng
import home.model.map.Place
import home.model.map.Point

interface MapContract {
    interface MapView <T : BaseContract.IBaseView>: FragmentBaseContract.IFragmentBaseView<T>{
        fun showSearchResults(placeResults: List<Place>)
        fun drawRoute(route: List<LatLng>)
    }
    interface IMapPresenter<T: FragmentBaseContract.IFragmentBaseView<*>>: FragmentBaseContract.IBasePresenter<T>{
        fun performSearchPlaces(placeToSearch: String)
        fun getRoute(destination: Place)
        //TODO buscar cual es la funcion del suspend
        suspend fun getCurrentPosition(): Point
    }

    interface MapModel {
        fun getPlacesFromSearch(placeToSearch: String): List<Place>
        fun getRoute(startPlace: Place, destination: Place): List <Point>
        fun getCurrentPosition(): Point
    }
}