package com.gfreeman.takeme.login.weather_forecast.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.gfreeman.takeme.R
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.progressindicator.CircularProgressIndicator

class WeatherReportActivity : AppCompatActivity() {
    private lateinit var weatherWebView: WebView
    private lateinit var loadingSpinner: CircularProgressIndicator
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_weather_report)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        weatherWebView = findViewById(R.id.webView_weather_forecast)
        loadingSpinner = findViewById(R.id.weather_webView_loading_spinner)
        loadingSpinner.visibility = View.GONE
        configureWebView()
        Log.i("Mati", "Test webView onCreate")
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(){
        Log.i("Mati", "Test configurewebView start")
        weatherWebView.settings.javaScriptEnabled = true
        weatherWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                loadingSpinner.visibility = View.VISIBLE
                Log.i("Mati", "Test webView onPageStarted")
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                loadingSpinner.visibility = View.GONE
                Log.i("Mati", "Test webView onPageFinished")
            }
        }
        loadCustomUrl()
    }

    private fun loadCustomUrl() {
        Log.i("Mati", "Test webView loadCustomUrl")
        val lat = intent.getStringExtra(LAT_EXTRA)
        val long = intent.getStringExtra(LONG_EXTRA)
        Log.i("Mati","WebView lat=$lat, long=$long")
        val weatherUrl = "https://www.windy.com/$lat/$long?-31.486,-64.114,11"
        weatherWebView.loadUrl(weatherUrl)
    }
    companion object{
        const val LAT_EXTRA = "lat"
        const val LONG_EXTRA = "long"
    }
}