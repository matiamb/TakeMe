package home.view

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.gfreeman.takeme.R
import home.view.map.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import home.view.fav.FavFragment
import home.view.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {
    private lateinit var mapFragment: Fragment
    private lateinit var favFragment: Fragment
    private lateinit var profileFragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
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

        mapFragment = MapFragment()
        favFragment = FavFragment()
        profileFragment = ProfileFragment()
        loadFragment(mapFragment)
        val bottomNavbarView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavbarView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home_item1 -> {
                    loadFragment(favFragment)
                    true
                }
                R.id.nav_home_item2 -> {
                    loadFragment(mapFragment)
                    true
                }
                R.id.nav_home_item3 -> {
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


}