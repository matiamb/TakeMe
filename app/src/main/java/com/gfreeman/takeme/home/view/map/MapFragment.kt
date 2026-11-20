package com.gfreeman.takeme.home.view.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.viewmodel.map.MapViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LAT_EXTRA
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LONG_EXTRA
import dagger.hilt.android.AndroidEntryPoint

/**
 * MapFragment hosting MapScreen via ComposeView Interop API + Hilt DI.
 */
@AndroidEntryPoint
class MapFragment : Fragment() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TakeMeTheme {
                    MapScreen(
                        viewModel = mapViewModel,
                        onNavigateToWeather = { point ->
                            val intent = Intent(context, WeatherReportActivity::class.java).apply {
                                putExtra(LAT_EXTRA, point.latitude.toString())
                                putExtra(LONG_EXTRA, point.longitude.toString())
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    /**
     * Called from HomeActivity when starting a route from Favorites.
     */
    fun getRouteFromFavs(startPlace: Place, destination: Place) {
        mapViewModel.getRouteFromFavs(startPlace, destination)
    }
}
