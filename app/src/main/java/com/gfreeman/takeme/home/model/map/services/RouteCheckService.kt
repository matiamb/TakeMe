package com.gfreeman.takeme.home.model.map.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gfreeman.takeme.R
import com.gfreeman.takeme.home.view.map.MapsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.gfreeman.takeme.home.model.map.Point

class RouteCheckService : Service() {

    private val routeCheckBinder = RouteCheckBinder()
    private var notificationManager: NotificationManager? = null
    private var isOutOfRoute = false
    private var routeCheckJob: Job? = null
    private val checkIntervalMs = 5000L
    private var locationProvider: CurrentLocationStatusProvider? = null
    private var notificationShown = false
    private var arrivedToDestinationListener: OnArriveDestinationListener? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        startRouteCheck()
        //Log.i("Mati", "Service created")
    }

    fun setArriveDestinationListener(arriveDestinationListener: OnArriveDestinationListener) {
        this.arrivedToDestinationListener = arriveDestinationListener
    }

    fun setLocationProvider ( locationProvider: CurrentLocationStatusProvider){
        this.locationProvider = locationProvider
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRouteCheck()
    }

    inner class RouteCheckBinder : Binder(){
        fun getService(): RouteCheckService = this@RouteCheckService
    }
    fun showNotification(){
        val notification = buildNotification()
        notificationManager?.notify(NOTIFICATION_ID, notification)
        notificationShown = true
    }

    private fun buildNotification(): Notification{
        if(Build.VERSION.SDK_INT >= 33){
            createNotificationChannel()
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cuidado! Saliste de la ruta")
            .setContentText("Go to Route ha diseñado una ruta para tu destino. ¿Estás seguro de que quieres salir de ella?")
            .setSmallIcon(R.drawable.map_arrow_square)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.app_logo))
            .setAutoCancel(true)
            .build()
    }

    @RequiresApi(33)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Out of route notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(channel)
    }

    interface CurrentLocationStatusProvider {
        fun getCurrentLocation(): Point?
        fun getCurrentRoute(): List<Point>?
    }
    override fun onBind(p0: Intent?): IBinder? {
        return routeCheckBinder
    }

    private fun startRouteCheck(){
        routeCheckJob = CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                isOutOfRoute = checkIfOutOfRoute()
                Log.i("Mati", "Is out of route? $isOutOfRoute")
                if (isOutOfRoute && !notificationShown){
                    showNotification()
                    stopSelf()
                }
                if (arrivedToDestination()) {
                    arrivedToDestinationListener?.onArriveDestination()
                    stopSelf()
                }
                delay(checkIntervalMs)
            }
        }
        //Log.i("Mati", "RoutecheckJob= ${routeCheckJob.toString()}")
    }

    private fun arrivedToDestination(): Boolean {
        return locationProvider?.let {
            val currentLocation = it.getCurrentLocation()
            val currentRoute = it.getCurrentRoute()
            Log.i("Mati", "arrivedToDestination service currentLocation: $currentLocation, currentRoute: $currentRoute")
            return currentRoute?.let {
                currentLocation?.let {
                    val distance =
                        MapsManager.calculateDistance(currentLocation, currentRoute.last())
                    distance < DISTANCE_TO_ARRIVE
                }
            } ?: false
        } ?: false
    }

    private fun stopRouteCheck(){
        routeCheckJob?.cancel()
    }

    private fun checkIfOutOfRoute(): Boolean {
        return locationProvider?.let {
            val currentLocation = it.getCurrentLocation()
            val currentRoute = it.getCurrentRoute()
            return currentRoute?.let {
                currentLocation?.let {
                    MapsManager.isOutsideOfRoute(currentLocation, currentRoute) }
            } ?: false
        } ?: false
    }

    interface OnArriveDestinationListener{
        fun onArriveDestination()
    }

    companion object {
        const val CHANNEL_ID = "Out of route"
        const val NOTIFICATION_ID = 23223
        const val DISTANCE_TO_ARRIVE = 50
    }
}