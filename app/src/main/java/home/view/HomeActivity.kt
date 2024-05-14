package home.view

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        configureViews()
    }
    private fun configureViews(){
        val mainTitle = findViewById<TextView>(R.id.txt_test)
        val bottomNavbarView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavbarView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_home_item1 -> {
                    // Respond to navigation item 1 click
                    mainTitle.text = "Favorites page"
                    true
                }
                R.id.nav_home_item2 -> {
                    mainTitle.text = "Map page"
                    true
                }
                R.id.nav_home_item3 -> {
                    mainTitle.text = "Profile page"
                    true
                }
                else -> false
            }
        }
    }
}