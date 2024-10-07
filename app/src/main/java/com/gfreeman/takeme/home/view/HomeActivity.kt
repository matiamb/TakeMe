package com.gfreeman.takeme.home.view

import android.content.Context
import android.os.Bundle
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.model.map.Place
import com.gfreeman.takeme.home.presenter.map.MapPresenterFragment
import com.google.android.gms.maps.GoogleMap
import com.gfreeman.takeme.home.view.map.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gfreeman.takeme.home.view.fav.FavFragment
import com.gfreeman.takeme.home.view.profile.ProfileFragment
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.android.material.transition.platform.MaterialFadeThrough
import com.google.android.material.transition.platform.MaterialSharedAxis
import contract.HomeContract
import contract.MapContract

class HomeActivity : AppCompatActivity(), HomeContract.HomeView {
    private lateinit var searchFragment: Fragment
    private lateinit var favFragment: Fragment
    private lateinit var profileFragment: Fragment
    private lateinit var bottomNavbarView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        //transition para activity con weather forecast
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        val exit = MaterialFadeThrough().apply {

            // Only run the transition on the contents of this activity, excluding
            // system bars or app bars if provided by the app’s theme.
            addTarget(R.id.home_container)
        }
        window.exitTransition = exit
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        configureViews()
    }
    private fun configureViews(){
        bottomNavbarView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        searchFragment = MapFragment()
        favFragment = FavFragment()
        profileFragment = ProfileFragment()
        loadFragment(searchFragment)

        bottomNavbarView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home_item1 -> {
                    searchFragment.enterTransition = MaterialSharedAxis(
                        MaterialSharedAxis.X, /* forward= */ true)
                    searchFragment.exitTransition = MaterialSharedAxis(
                        MaterialSharedAxis.X, /* forward= */ false)
                    loadFragment(favFragment)
                    true
                }
                R.id.nav_home_item2 -> {
                    loadFragment(searchFragment)
                    true
                }
                R.id.nav_home_item3 -> {
                    searchFragment.enterTransition = MaterialSharedAxis(
                        MaterialSharedAxis.X, /* forward= */ false)
                    searchFragment.exitTransition = MaterialSharedAxis(
                        MaterialSharedAxis.X, /* forward= */ true)
                    loadFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
        bottomNavbarView.selectedItemId = R.id.nav_home_item2
    }
    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container, fragment)
        transaction.commit()
    }

    override fun openMapsScreenWithDestination(startPlace: Place, destinationPlace: Place) {
        bottomNavbarView.selectedItemId = R.id.nav_home_item2
        (searchFragment as? MapContract.MapView<*>)?.getRouteFromFavs(
            startPlace,
            destinationPlace
        )
    }

    override fun showErrorMessage(message: String) {
        //TODO("Not yet implemented")
    }

    override fun getViewContext(): Context {
        return this
    }

}