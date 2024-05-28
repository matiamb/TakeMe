package home.view.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.gfreeman.takeme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var listView: ListView
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
        listView = view.findViewById<ListView>(R.id.map_list_view)
        val text = searchView.text
        super.onViewCreated(view, savedInstanceState)
        configureMap()
        initPresenter()
        //Recibo la lista de tipo places desde el backend
        val query: ArrayList<Place> = ArrayList(showSearchResults(text.toString()))
        //con el mapTo solo busco la propiedad que me interesa de la lista
        val searchResults: ArrayList<String> = query.mapTo(arrayListOf()){
            it.displayName
        }
        //Esto es para setear un listener en el searchView de Material3 ya que no tiene el onQueryTextListener
        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            //TODO implementar cuando hago click en la opcion de busqueda, me lleve o dibuje la ruta en el mapa
            //Esto comentado de abajo es para filtrar con lo que se escriba en el editText
            //adapter?.filter?.filter(text)
            //Esto es para mostrar el ListView dentro de un Fragment
            val adapter = activity?.let {
                ArrayAdapter<String>(
                    it, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, searchResults
                )
            }
            listView.adapter = adapter
            false
        }
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

    override fun showSearchResults(search: String): List<Place> {
        return mapPresenter.performSearchPlaces(searchView.text.toString())
    }

    override fun drawRoute(route: List<LatLng>) {
        //TODO("Not yet implemented")
    }

    override fun showResult(search: String): String {
        val result = mapPresenter.getResult(search)
        return result
    }

    override fun getParentView(): BaseContract.IBaseView {
        //TODO("Not yet implemented")
        return activity as BaseContract.IBaseView
    }

}
