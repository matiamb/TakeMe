package home.model.map.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.gfreeman.takeme.R

class RouteCheckService : Service() {

    private val routeCheckBinder = RouteCheckBinder()

    inner class RouteCheckBinder : Binder(){
        fun getService() = this@RouteCheckService
    }
    fun showNotification(context: Context){
        val testNotification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("test notification")
            .setContentInfo("Esta es una test de notificaciones y del service")
            //.setLargeIcon(Icon.createWithResource(context, R.drawable.app_logo))
            .build()

        val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Test channel", NotificationManager.IMPORTANCE_DEFAULT)
        )
        notificationManager.notify(1, testNotification)
    }
    override fun onBind(p0: Intent?): IBinder? {
        return routeCheckBinder
    }
    companion object {
        const val CHANNEL_ID = "Out of route"
    }
}