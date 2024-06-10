package home.view.map

import android.os.Bundle
import android.util.Log
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
import home.model.map.Point
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

        super.onViewCreated(view, savedInstanceState)
        configureMap()
        initPresenter()
        //Recibo la lista de tipo places desde el backend
        //val query: ArrayList<Place> = ArrayList(showSearchResults(text.toString()))
        //con el mapTo solo busco la propiedad que me interesa de la lista
        /*val searchResults: ArrayList<String> = query.mapTo(arrayListOf()){
            it.displayName
        }*/
        //Esto es para setear un listener en el searchView de Material3 ya que no tiene el onQueryTextListener
        searchView.editText.setOnEditorActionListener { v, actionId, event ->
            //Esto comentado de abajo es para filtrar con lo que se escriba en el editText
            //adapter?.filter?.filter(text)
            //Esto es para mostrar el ListView dentro de un Fragment
            /*val adapter = activity?.let {
                ArrayAdapter<String>(
                    it, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, searchResults
                )
            }*/
            //adapter?.filter?.filter(text)
            //click listener para los items de la lista
            /*listView.setOnItemClickListener { parent, view, position, id ->
                searchView.hide()
                //TODO una vez realizada la busqueda, se borra la opcion correctamente,
                // pero si hago otra busqueda el listview queda vacio
                //adapter?.clear()
                mapPresenter.getRoute(query[0])
            }*/
            val text = searchView.text
            mapPresenter.performSearchPlaces(text.toString())
            Log.i("Mati", text.toString())
            false
        }


    }

    private fun configureMap(){
        val mapSupportFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapSupportFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val initialFakePoint = LatLng(-34.679437, -58.553777)
        MapsManager.addMarkerToMap(googleMap, initialFakePoint)
        MapsManager.centerMapIntoLocation(googleMap, initialFakePoint)
    }

    private fun initPresenter(){
        val mapModel = MapRepository()
        mapPresenter = MapPresenter(mapModel)
        mapPresenter.attachView(this)
    }

    override fun showSearchResults(search: List<Place>){
        val adapter = activity?.let {
            ArrayAdapter<String>(
                it, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, search.mapTo(arrayListOf()){
                    it.displayName
                }
            )
        }
        listView.adapter = adapter
        listView.setOnItemClickListener { parent, view, position, id ->
            searchView.hide()
            mapPresenter.getRoute(search[0])
        }
    }

    override fun drawRoute(route: List<LatLng>) {
        context?.let { safeContext ->
            MapsManager.addRouteToMap(safeContext, googleMap, route)
            MapsManager.alignMapToRoute(googleMap, route)
            MapsManager.addMarkerToMap(googleMap, route.last())
        }
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
