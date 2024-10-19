package com.gfreeman.takeme.home.view.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gfreeman.takeme.R
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LAT_EXTRA
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LONG_EXTRA
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.search.SearchView
import contract.BaseContract
import contract.MapContract
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.MapRepository
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.presenter.map.MapPresenterFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MapFragment : Fragment(), OnMapReadyCallback, MapContract.MapView<BaseContract.IBaseView> {
    private lateinit var mapPresenter: MapContract.IFragmentMapPresenter<MapContract.MapView<BaseContract.IBaseView>>
    private lateinit var googleMap: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var fabWeather: FloatingActionButton
    private lateinit var fabCancelRoute: FloatingActionButton
    private lateinit var searchProgressBar: ProgressBar
    private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notification = Manifest.permission.POST_NOTIFICATIONS

    private var multiplePermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        checkLocationPermission()
    }
    private var singlePermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        checkNotificationPermission()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        /*val bottomSheet = view.findViewById<View>(R.id.map_bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(com.google.android.material.R.dimen.m3_searchbar_height)*/
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
//        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        searchView = view.findViewById(R.id.map_search_view)
        listView = view.findViewById(R.id.map_list_view)
        fabWeather = view.findViewById(R.id.floating_weather_button)
        searchProgressBar = view.findViewById(R.id.search_places_loading_spinner)
        super.onViewCreated(view, savedInstanceState)
        configureMap()
        initPresenter()
        initFusedLocationProviderClient()

        fabWeather.setOnClickListener{
            CoroutineScope(Dispatchers.Main).launch {
                val currentPosition = mapPresenter.getCurrentPosition()
                val weatherIntent = Intent(context, WeatherReportActivity::class.java)
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    fabWeather,
                    "transition_weather_forecast" // The transition name to be matched in Activity B.
                )
                weatherIntent.putExtra(LAT_EXTRA, currentPosition?.latitude.toString())
                weatherIntent.putExtra(LONG_EXTRA, currentPosition?.longitude.toString())
                startActivity(weatherIntent, options.toBundle())
                //activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out)
            }
        }
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
                //una vez realizada la busqueda, se borra la opcion correctamente,
                // pero si hago otra busqueda el listview queda vacio
                //adapter?.clear()
                mapPresenter.getRoute(query[0])
            }*/
            val text = searchView.text
            mapPresenter.performSearchPlaces(text.toString())
            searchProgressBar.visibility = View.VISIBLE
            Log.i("Mati", text.toString())
            false
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            stopLocationUpdates()
            context?.let { safeContext ->
                mapPresenter.stopCheckingDistanceToRoute(safeContext)
            }
            Log.i("Mati", "Location updates stopped")
        }
        catch (_: UninitializedPropertyAccessException){
        }
   }

//    override fun onResume() {
//        super.onResume()
//        startLocationUpdates()
//    }

    private fun configureMap(){
        val mapSupportFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapSupportFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
//        val initialFakePoint = LatLng(-34.679437, -58.553777)
//        MapsManager.addMarkerToMap(googleMap, initialFakePoint)
//        MapsManager.centerMapIntoLocation(googleMap, initialFakePoint)
        checkLocationPermission()
    }

    private fun initPresenter(){
        val mapModel = MapRepository()
        mapPresenter = MapPresenterFragment(mapModel)
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
        searchProgressBar.visibility = View.GONE
        listView.adapter = adapter
        if (search.size == 0){
            Toast.makeText(context, R.string.search_result_failed, Toast.LENGTH_LONG).show()
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            searchView.hide()
            mapPresenter.getRoute(search[position])
        }
    }
    @SuppressLint("MissingPermission")
    private fun checkLocationPermission(){
        val fineLoc = isPermissionGranted(fineLocation)
        val coarseLoc = isPermissionGranted(coarseLocation)

        when {
            fineLoc or coarseLoc -> {
                enableMyLocation()
            }
            !fineLoc or !coarseLoc -> {
                multiplePermissionRequest.launch(arrayOf(fineLocation, coarseLocation))
            }
        }
    }

    @SuppressLint("NewApi")
    private fun checkNotificationPermission(){
        val notif = isPermissionGranted(notification)
        if(!notif){
            singlePermissionRequest.launch(notification)
        }
    }

    override fun drawRoute(route: List<LatLng>) {
        checkNotificationPermission()
        context?.let { safeContext ->
            MapsManager.addRouteToMap(safeContext, googleMap, route)
            MapsManager.alignMapToRoute(googleMap, route)
            MapsManager.addMarkerToMap(googleMap, route.last())
            startLocationUpdates()
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            //requestNotificationPermission()
        }
    }

    override fun showResult(search: String): String {
        val result = mapPresenter.getResult(search)
        return result
    }

    override fun initFusedLocationProviderClient() {
        context?.let { safeContext ->
            mapPresenter.initFusedLocationProviderClient(safeContext)
        }
    }

    override fun startLocationUpdates() {
        fabCancelRoute = requireView().findViewById(R.id.floating_endroute_button)
        context?.let { safeContext ->
            mapPresenter.startLocationUpdates(safeContext)
        }
        fabCancelRoute.show()
        fabCancelRoute.setOnClickListener{
            googleMap.clear()
            stopLocationUpdates()
            context?.let { safeContext ->
                mapPresenter.stopCheckingDistanceToRoute(safeContext)
            }
            fabCancelRoute.hide()
        }
        //mapPresenter.updateMapLocation()
    }

    override fun stopLocationUpdates() {
        mapPresenter.stopLocationUpdates()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        //throw UninitializedPropertyAccessException()
    }

    override fun getParentView(): BaseContract.IBaseView? {
        return activity as? BaseContract.IBaseView
    }

    override fun openCongratsScreen(congratsParams: Bundle) {
        context?.let {
            val congratsIntent = Intent(it, ArrivedToDestinationActivity::class.java)
            congratsIntent.putExtras(congratsParams)
            startActivity(congratsIntent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        context?.let { safeContext ->
                googleMap.isMyLocationEnabled = true
                try {
                    mapPresenter.getLastLocation()
                } catch (e: Exception){
                    Log.i("Mati", "mCurrentLocation is $e")
                }
                return
            }
            //PERMISSION RATIONALE ES PARA MOSTRAR UN DIALOGO QUE EXPLIQUE POR QUE NECESITAS EL PERMISO
            // 2. If if a permission rationale dialog should be shown
            /*if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ) {
                PermissionUtils.RationaleDialog.newInstance(
                    LOCATION_PERMISSION_REQUEST_CODE, true
                ).show(supportFragmentManager, "dialog")
                return
            }*/
            //SI ESTOY EN UN FRAGMENT, NO NECESITO EL ACTIVITYCOMPAT ANTES DE REQUEST PERMISSIONS O SHOULDSHOW...
            // 3. Otherwise, request permission
            //requestLocationPermission()
        }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
//            super.onRequestPermissionsResult(
//                requestCode,
//                permissions,
//                grantResults
//            )
//            requestLocationPermission()
//            return
//        }
//
//        if (isPermissionGranted(
//                permissions,
//                grantResults,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) || isPermissionGranted(
//                permissions,
//                grantResults,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            )
//        ) {
//            // Enable the my location layer if the permission has been granted.
//            enableMyLocation()
//        } else {
//            // Permission was denied. Display an error message
//            Toast.makeText(
//                context,
//                "Location Permission denied, please change it through settings",
//                Toast.LENGTH_LONG
//            ).show()
//        }
//        //siempre da false, por que post notifications no esta en el array, no se como agregarlo
//        if (isPermissionGranted(
//                permissions,
//                grantResults,
//                Manifest.permission.POST_NOTIFICATIONS
//            )
//        ) {
//            //do nothing
//        } else {
//            // Permission was denied. Display an error message
//            Toast.makeText(context, "Notification Permission denied, please change it through settings", Toast.LENGTH_LONG).show()
//        }
//    }

//    private fun requestLocationPermission(){
//        requestPermissions(
//            arrayOf(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ),
//            LOCATION_PERMISSION_REQUEST_CODE
//        )
//    }
    override fun getLastLocation(myLocation: LatLng) {
        MapsManager.addMarkerToMap(googleMap, myLocation)
        MapsManager.centerMapIntoLocation(googleMap, myLocation)
    }

    override fun updateMapLocation(location: Point) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(
            location.latitude, location.longitude
        )))
    }

//    private fun requestNotificationPermission(){
//        if (Build.VERSION.SDK_INT >= 33){
//            requestPermissions(
//                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
//                NOTIFICATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }

    override fun getRouteFromFavs(startPlace: Place, destination: Place){
        mapPresenter.getRouteFromFavs(startPlace, destination)
    }

    private fun isPermissionGranted(name: String) = ContextCompat.checkSelfPermission(
        requireContext(), name
    ) == PackageManager.PERMISSION_GRANTED

//    companion object {
//        /**
//         * Request code for location permission request.
//         *
//         * @see .onRequestPermissionsResult
//         */
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
//        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 2
//    }

}
