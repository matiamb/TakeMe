package com.gfreeman.takeme.home.view.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.congrats.ArrivedToDestinationModel
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.presenter.congrats.ArrivedToDestinationPresenter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.platform.MaterialFadeThrough
import contract.ArrivedToDestinationContract

class ArrivedToDestinationActivity : AppCompatActivity(), ArrivedToDestinationContract.ArrivedToDestinationView {
    private lateinit var fab_fav_route: FloatingActionButton
    private lateinit var txt_start_location: TextView
    private lateinit var txt_end_location: TextView
    private var isFavorite: Boolean = false
    private var startPlace: Place? = null
    private var finishPlace: Place? = null
    private var startPointName = ""
    private lateinit var arrivedToDestinationPresenter: ArrivedToDestinationContract.IArrivedToDestinationPresenter<ArrivedToDestinationContract.ArrivedToDestinationView>
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        val enter = MaterialFadeThrough().apply {
            addTarget(R.id.arrived_destination_activity)
        }
        window.enterTransition = enter

        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_arrived_to_destination)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.arrived_destination_activity)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initPresenter()
        txt_start_location = findViewById(R.id.txt_start_location)
        txt_end_location = findViewById(R.id.txt_end_location)
        fab_fav_route = findViewById(R.id.btn_fav_route)
        getParamsFromIntent()
        txt_start_location.text = startPlace?.displayName
        txt_end_location.text = finishPlace?.displayName?.split(",")?.take(1)?.joinToString(",")?:""
        fab_fav_route.setOnClickListener {
            isFavorite = !isFavorite
            configFavButton()
                if (startPointName == "") {
                    showAddNameToRouteDialog()
                    return@setOnClickListener
                }
                arrivedToDestinationPresenter.saveFavoriteRoute(startPlace, finishPlace, isFavorite)
                //notifyFavoriteSaved()
            }
        }

    private fun configFavButton() {
        if (isFavorite) {
            fab_fav_route.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.fav_route_heart, null)
            )
        } else {
            fab_fav_route.setImageDrawable(
                ResourcesCompat.getDrawable(resources, R.drawable.not_fav_route, null)
            )
        }
    }

    private fun showAddNameToRouteDialog() {
        val customView = LayoutInflater.from(this@ArrivedToDestinationActivity)
            .inflate(R.layout.name_route_dialog, null)
        val editTextPlaceName = customView.findViewById<EditText>(R.id.editTextPlaceName)

        val dialog = MaterialAlertDialogBuilder(
            this@ArrivedToDestinationActivity,
            com.google.android.material.R.style.AlertDialog_AppCompat
        )
            .setTitle("Ingresa el nombre del punto inicial")
            .setView(customView)
            .setPositiveButton(
                "Ok"
            ) { dialog, _ ->
                startPointName = editTextPlaceName.text.toString()
                startPlace?.displayName = startPointName
                txt_start_location.text = startPointName
                arrivedToDestinationPresenter.saveFavoriteRoute(startPlace, finishPlace, isFavorite)
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

        dialog.show()
    }

    private fun getParamsFromIntent() {
        startPlace = intent.extras?.getSerializable(EXTRA_START_PLACE) as? Place
        finishPlace = intent.extras?.getSerializable(EXTRA_FINISH_PLACE) as? Place
    }

    override fun notifyFavoriteSaved() {
        Toast.makeText(this, "Route saved!", Toast.LENGTH_SHORT).show()
    }

    override fun notifyFavoriteDeleted() {
        Toast.makeText(this, "Route deleted!", Toast.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun getViewContext() = this

    private fun initPresenter(){
        val congratsModel = ArrivedToDestinationModel(this)
        arrivedToDestinationPresenter = ArrivedToDestinationPresenter(congratsModel)
        arrivedToDestinationPresenter.attachView(this)
    }

    companion object {
        const val EXTRA_START_PLACE = "START PLACE"
        const val EXTRA_FINISH_PLACE = "FINISH PLACE"
    }
}