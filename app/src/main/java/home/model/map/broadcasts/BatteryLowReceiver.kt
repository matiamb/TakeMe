package home.model.map.broadcasts

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.gfreeman.takeme.R

class BatteryLowReceiver : BroadcastReceiver() {
    private var notificationManager: NotificationManager? = null
    private var notificationShown = false

    override fun onReceive(context: Context?, p1: Intent?) {
        notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        showNotification(context)
    }

    fun showNotification(context: Context?){
        val notification = context?.let { buildNotification(it) }
        notificationManager?.notify(NOTIFICATION_ID, notification)
        notificationShown = true
    }

    private fun buildNotification(context: Context): Notification {
        if(Build.VERSION.SDK_INT >= 33){
            createNotificationChannel()
        }
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Bateria Baja!")
            .setContentText("Parece que te estas quedando sin bateria. Asegurate de conectar el cargador")
            .setSmallIcon(R.drawable.map_arrow_square)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.app_logo))
            .setAutoCancel(true)
            .build()
    }

    @RequiresApi(33)
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Battery Low",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationManager?.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "Battery Low"
        const val NOTIFICATION_ID = 23224
    }
}