package com.gfreeman.takeme.weather_forecast.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gfreeman.takeme.ui.theme.TakeMeTheme

class WeatherReportActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val lat = intent.getStringExtra(LAT_EXTRA)
        val long = intent.getStringExtra(LONG_EXTRA)

        setContent {
            TakeMeTheme {
                WeatherReportScreen(
                    lat = lat,
                    long = long,
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        const val LAT_EXTRA = "lat"
        const val LONG_EXTRA = "long"
    }
}
