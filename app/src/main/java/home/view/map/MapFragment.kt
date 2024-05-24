package home.view.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gfreeman.takeme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import contract.BaseContract
import contract.MapContract
import home.model.map.Place

import home.model.map.MapRepository
import home.presenter.map.MapPresenter


class MapFragment : Fragment(), OnMapReadyCallback, MapContract.MapView<BaseContract.IBaseView> {
    //private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    //private lateinit var placesSearchView: CustomSearchView
    private lateinit var mapPresenter: MapContract.IMapPresenter<MapContract.MapView<BaseContract.IBaseView>>
    private lateinit var googleMap: GoogleMap
    private lateinit var searchView: SearchView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        /*val bottomSheet = view.findViewById<View>(R.id.map_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(com.google.android.material.R.dimen.m3_searchbar_height)*/
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView = view.findViewById(R.id.map_search_view)
        super.onViewCreated(view, savedInstanceState)
        configureMap()
        initPresenter()
        //TODO IMPLEMENTAR LA BUSQUEDA CON LA BARRA Y QUE MUESTRE RESULTADOS
        //searchView.setOnMenuItemClickListener()
    }

    private fun configureMap(){
        val mapSupportFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapSupportFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        val mountainView = LatLng(37.4, -122.1)
        // Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
        val cameraPosition = CameraPosition.Builder()
            .target(mountainView) // Sets the center of the map to Mountain View
            .zoom(17f)            // Sets the zoom
            .bearing(90f)         // Sets the orientation of the camera to east
            .tilt(30f)            // Sets the tilt of the camera to 30 degrees
            .build()              // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    private fun initPresenter(){
        val mapModel = MapRepository()
        mapPresenter = MapPresenter(mapModel)
        mapPresenter.attachView(this)
    }

    override fun showSearchResults(placeResults: List<Place>) {
        TODO("Not yet implemented")
        mapPresenter.performSearchPlaces(searchView.text.toString())

    }

    override fun drawRoute(route: List<LatLng>) {
        TODO("Not yet implemented")
    }

    override fun getParentView(): BaseContract.IBaseView {
        TODO("Not yet implemented")
    }

}
