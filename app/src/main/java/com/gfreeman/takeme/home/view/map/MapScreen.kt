package com.gfreeman.takeme.home.view.map

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.model.map.Point
import com.gfreeman.takeme.home.viewmodel.map.MapUiState
import com.gfreeman.takeme.home.viewmodel.map.MapViewModel
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

/**
 * MapScreen Composable using Material 3 Scaffold layout.
 */
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToWeather: (Point) -> Unit,
    onRequestPermission: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapsManager = MapsManager

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var currentStyle by remember { mutableStateOf<Style?>(null) }
    val mapView = remember {
        MapLibre.getInstance(context)
        MapView(context).apply {
            onCreate(null)
        }
    }

    // Toast error observer
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    // Update polyline route on map whenever routePoints changes
    LaunchedEffect(uiState.routePoints, currentStyle, mapLibreMap) {
        val style = currentStyle
        val map = mapLibreMap
        if (map != null && style != null && uiState.routePoints.isNotEmpty()) {
            MapsManager.addRouteToMap(uiState.routePoints, style, context)
            MapsManager.fitCameraToRoute(map, route = uiState.routePoints, context = context)
        }
    }
    LaunchedEffect(uiState.navigateToWeather) {
        uiState.navigateToWeather?.let { pos ->
            onNavigateToWeather(pos)
            viewModel.onWeatherNavigationHandled()
        }
    }
    LaunchedEffect(mapLibreMap, currentStyle, uiState.userLocation) {
        val map = mapLibreMap
        val style = currentStyle
        val userLoc = uiState.userLocation
        if (map != null && style != null && uiState.routePoints.isEmpty()) {
            mapsManager.enableLocationComponent(map, style, context)
            if (userLoc != null) {
                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(userLoc.latitude, userLoc.longitude),
                        15.0
                    )
                )
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FabColumn(
                uiState = uiState,
                onCancelRoute = {
                    viewModel.cancelRoute(context)
                },
                onWeatherFabClicked = {
                    viewModel.onWeatherFabClicked()
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. MapLibre Map Embedded via AndroidView
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = {
                    mapView.apply {
                        getMapAsync { map ->
                            map.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->
                                mapLibreMap = map
                                currentStyle = style

                                // Enable location blue dot and pulse ring
                                mapsManager.enableLocationComponent(map, style, context)
                                map.cameraPosition = CameraPosition.Builder()
                                    .target(
                                        LatLng(
                                            -31.4201,
                                             -64.1888
                                        )
                                    )
                                    .zoom(13.0)
                                    .build()
                            }
                        }
                    }
                }
            )

            // 2. Lifecycle Management for MapView
            DisposableEffect(lifecycleOwner, mapView) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> mapView.onStart()
                        Lifecycle.Event.ON_RESUME -> mapView.onResume()
                        Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                        Lifecycle.Event.ON_STOP -> mapView.onStop()
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                        mapView.onPause()
                    }
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        mapView.onStop()
                    }
                    mapView.onDestroy()
                }
            }

            // 3. Floating Search Overlay
            SearchBar(
                uiState = uiState,
                onValueChange = { text ->
                    viewModel.onSearchQueryChanged(text)
                },
                onClearSearchClicked = {
                    viewModel.onSearchQueryChanged("")
                    viewModel.clearSearchResults()
                },
                onDestinationSelected = { place ->
                    if (PermissionUtils.hasLocationPermission(context)){
                        viewModel.selectDestination(place)
                    } else {
                        onRequestPermission()
                    }
                },
                onSearchDestination = {
                    viewModel.performSearchPlaces()
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    uiState: MapUiState,
    onValueChange: (String) -> Unit,
    onClearSearchClicked: () -> Unit,
    onDestinationSelected: (Place) -> Unit,
    onSearchDestination: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val focusManager = LocalFocusManager.current
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { text ->
                onValueChange(text)
            },
            placeholder = { Text("Search places or destination...") },
            leadingIcon = {
                if (uiState.isSearching) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = onClearSearchClicked) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear Search")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearchDestination()
                focusManager.clearFocus()
            }),
            modifier = Modifier.fillMaxWidth()
        )

        // Search Results Dropdown List
        AnimatedVisibility(
            visible = uiState.searchResults.isNotEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    items(uiState.searchResults) { place ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onDestinationSelected(place)
                                    focusManager.clearFocus()
                                }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = place.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun FabColumn(
    uiState: MapUiState,
    onCancelRoute: () -> Unit,
    onWeatherFabClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cancel Route FAB
        if (uiState.isNavigating) {
            SmallFloatingActionButton(
                onClick = onCancelRoute,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_map_cancel_route),
                    contentDescription = "Cancel Route"
                )
            }
        }

        // Weather Forecast FAB
        FloatingActionButton(
            onClick = onWeatherFabClicked,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_forecast_sunny),
                contentDescription = "Weather Forecast",
                tint = null,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
