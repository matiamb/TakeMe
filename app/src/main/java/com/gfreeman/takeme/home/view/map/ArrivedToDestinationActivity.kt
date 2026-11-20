package com.gfreeman.takeme.home.view.map

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.viewmodel.map.ArrivedToDestinationViewModel
import com.gfreeman.takeme.ui.theme.TakeMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArrivedToDestinationActivity : ComponentActivity() {

    private val arrivedViewModel: ArrivedToDestinationViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startPlace = intent.extras?.getSerializable(EXTRA_START_PLACE, Place::class.java)
        val finishPlace = intent.extras?.getSerializable(EXTRA_FINISH_PLACE, Place::class.java)

        arrivedViewModel.setPlaces(startPlace, finishPlace)

        setContent {
            TakeMeTheme {
                ArrivedToDestinationScreen(viewModel = arrivedViewModel)
            }
        }
    }

    companion object {
        const val EXTRA_START_PLACE = "START PLACE"
        const val EXTRA_FINISH_PLACE = "FINISH PLACE"
    }
}
