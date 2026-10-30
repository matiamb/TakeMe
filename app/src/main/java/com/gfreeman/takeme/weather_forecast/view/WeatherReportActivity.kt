package com.gfreeman.takeme.weather_forecast.view

import android.animation.Animator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback

class WeatherReportActivity : AppCompatActivity() {
    private lateinit var weatherWebView: WebView
    private lateinit var loadingSpinner: ProgressBar
    private var isFirstLoad = true
    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)

        enableEdgeToEdge()
        setContentView(R.layout.activity_weather_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_weather_forecast)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //animation attempt
        findViewById<View>(android.R.id.content).transitionName = "transition_weather_forecast"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 500L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 400L
        }
        //
        super.onCreate(savedInstanceState)
        weatherWebView = findViewById(R.id.webView_weather_forecast)
        loadingSpinner = findViewById(R.id.weather_webView_loading_spinner)
        loadingSpinner.visibility = View.GONE
        configureWebView()
        //Log.i("Mati", "Test webView onCreate")
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(){
        //Log.i("Mati", "Test configurewebView start")
        weatherWebView.visibility = View.INVISIBLE
        weatherWebView.settings.javaScriptEnabled = true
        weatherWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSpinner.visibility = View.VISIBLE
                Log.i("Mati", "Test webView onPageStarted")
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (isFirstLoad){
                    loadPageWithAnimation()
                }
                loadingSpinner.visibility = View.GONE
                Log.i("Mati", "Test webView onPageFinished")
            }
        }
        loadCustomUrl()
    }

    private fun loadPageWithAnimation() {
        val fadeInAnim = android.view.animation.AnimationUtils.loadAnimation(this@WeatherReportActivity, R.anim.fade_in)
        fadeInAnim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                loadingSpinner.visibility = View.GONE
                weatherWebView.visibility = View.VISIBLE
                isFirstLoad = false
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }
        })
        weatherWebView.startAnimation(fadeInAnim)
    }

    private fun loadCustomUrl() {
        //Log.i("Mati", "Test webView loadCustomUrl")
        val lat = intent.getStringExtra(LAT_EXTRA)
        val long = intent.getStringExtra(LONG_EXTRA)
        //Log.i("Mati","WebView lat=$lat, long=$long")
        val weatherUrl = "https://www.windy.com/$lat/$long"
        weatherWebView.loadUrl(weatherUrl)
    }
    companion object{
        const val LAT_EXTRA = "lat"
        const val LONG_EXTRA = "long"
    }
}