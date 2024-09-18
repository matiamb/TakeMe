package com.gfreeman.takeme.home.view.map

import android.os.Bundle
import android.view.Window
import android.window.OnBackInvokedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.google.android.material.transition.platform.MaterialSharedAxis

class ArrivedToDestinationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        val enter = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            //addTarget(R.id.b_container)
        }
        window.enterTransition = enter
        // TODO: Configure a return transition in the backwards direction.
        val exit = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            //addTarget(R.id.b_container)
        }
        window.exitTransition = exit

        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_arrived_to_destination)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}