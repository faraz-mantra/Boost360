package com.framework.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

import com.framework.BaseApplication
import com.framework.R

object NotiUtils {


    var notificationBuilder: NotificationCompat.Builder?=null
    var notificationManager: NotificationManager?=null
    val NOTIFICATION_CHANNEL_ID=BaseApplication.instance.packageName

    private fun prepareNotification(){
       notificationManager =
           BaseApplication.instance.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC


            notificationManager?.createNotificationChannel(notificationChannel)
        }

        notificationBuilder = NotificationCompat.Builder(BaseApplication.instance, NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


    }






    class NotiInfo(val notification:Notification?,val builder:NotificationCompat.Builder?,val remoteViews: RemoteViews){

    }



    fun showNoti(title:String,progress:Int,max:Int=0,i: Intent?=null,priority:Int=NotificationCompat.PRIORITY_HIGH): Notification? {

        notificationBuilder = NotificationCompat.Builder(BaseApplication.instance, NOTIFICATION_CHANNEL_ID)
            .setPriority(priority)

        prepareNotification()

       var intent = i


        var resultIntent:PendingIntent?=null

        if (intent!=null){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            resultIntent = PendingIntent.getActivity(
                BaseApplication.instance,
                System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        }



        notificationBuilder?.setAutoCancel(true)
            ?.setDefaults(Notification.DEFAULT_ALL)
            ?.setOngoing(true)
            ?.setOnlyAlertOnce(true)
            ?.setContentIntent(resultIntent)
            ?.setProgress(100,0,false)
            ?.setSmallIcon(R.drawable.ic_book)
            ?.setContentTitle(title)

        return notificationBuilder?.build()
    }


}