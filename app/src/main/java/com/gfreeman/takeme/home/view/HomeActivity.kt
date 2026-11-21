package com.gfreeman.takeme.home.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.gfreeman.takeme.home.viewmodel.favs.FavoritesViewModel
import com.gfreeman.takeme.home.viewmodel.map.MapViewModel
import com.gfreeman.takeme.home.viewmodel.profile.ProfileViewModel
import com.gfreeman.takeme.login.view.LoginActivity
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LAT_EXTRA
import com.gfreeman.takeme.weather_forecast.view.WeatherReportActivity.Companion.LONG_EXTRA
import dagger.hilt.android.AndroidEntryPoint

/**
 * HomeActivity hosting the main Compose navigation shell (HomeScreen).
 */
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {

    private val favoritesViewModel: FavoritesViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TakeMeTheme {
                HomeScreen(
                    favoritesViewModel = favoritesViewModel,
                    mapViewModel = mapViewModel,
                    profileViewModel = profileViewModel,
                    onNavigateToWeather = { point ->
                        val intent = Intent(this, WeatherReportActivity::class.java).apply {
                            putExtra(LAT_EXTRA, point.latitude.toString())
                            putExtra(LONG_EXTRA, point.longitude.toString())
                        }
                        startActivity(intent)
                    },
                    onLogoutClick = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}
