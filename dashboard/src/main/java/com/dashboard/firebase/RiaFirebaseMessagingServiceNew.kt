package com.dashboard.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import com.anachat.chatsdk.AnaCore
import com.crashlytics.android.Crashlytics
import com.dashboard.R
import com.dashboard.rest.repository.WithFloatTwoRepository
import com.framework.models.toLiveData
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.webengage.sdk.android.WebEngage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by NowFloats on 05-10-2016.
 */
class RiaFirebaseMessagingServiceNew : FirebaseMessagingService() {

    private var session: UserSessionManager? = null

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(
            TAG,
            "Token: $token"
        )
        try {
            saveTokenToPreferenceAndUpload(token)
            WebEngage.get().setRegistrationID(token)
            //try {ZopimChat.setPushToken(token);} catch (Exception e) {}
            AnaCore.saveFcmToken(this, token)
        } catch (e: Exception) {
            Crashlytics.log("Failed to process FCM Token by RiaFirebaseMessagingService" + e.message)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        try {
            session = UserSessionManager(this)
            val mapResult: Map<String, String> = remoteMessage.data
            if (mapResult != null) {
                val isWebEnagage =
                    mapResult.containsKey("source") && "webengage" == mapResult["source"]
                if (isWebEnagage) {
                    WebEngage.get().receive(mapResult)
                }
                if (mapResult.containsKey("payload")) {
                    AnaCore.handlePush(this, mapResult["payload"])
                } else if (!isWebEnagage) {
                    val deepLinkUrl = mapResult["url"]
                    val title = mapResult["title"]
                    val message = mapResult["mp_message"]
                    sendNotification(this.baseContext, title, message, deepLinkUrl)
                    //Constants.GCM_Msg = true
                }
            }
        } catch (e: Exception) {
            Crashlytics.log("Failed to process onMessageReceived in RiaFirebaseMessagingService" + e.message)
        }
    }

    private fun sendNotification(
        c: Context,
        messageTitle: String?,
        messageBody: String?,
        deepLinkUrl: String?
    ) {
        val intent = Intent("com.dashboard.controller.DashboardActivity")
        val stackBuilder = TaskStackBuilder.create(c)
        intent.putExtra("from", "notification")
        intent.putExtra("url", deepLinkUrl)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        stackBuilder.addNextIntentWithParentStack(intent)
        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationBuilder = NotificationCompat.Builder(c, "111").apply {
            setSmallIcon(R.drawable.app_launcher2)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.app_launcher))
            setContentText(messageBody)
            setAutoCancel(true)
            setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            color = ContextCompat.getColor(c, R.color.primaryColor)
            setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            setContentIntent(pendingIntent)
            setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            priority = NotificationCompat.PRIORITY_HIGH
            setLights(Color.GREEN, 3000, 3000)
        }
        if (messageTitle != null && messageTitle.isEmpty()) notificationBuilder.setContentTitle(
            messageTitle
        ) else notificationBuilder.setContentTitle(resources.getString(R.string.app_name))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel("111", "NOTIFICATION_CHANNEL_NAME", importance).apply {
                    enableLights(true)
                    lightColor = Color.YELLOW
                    enableVibration(true)
                    setShowBadge(false)
                    vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                }

            assert(notificationManager != null)
            notificationBuilder.setChannelId("111")
            notificationManager!!.createNotificationChannel(notificationChannel)
            notificationManager.notify(0, notificationBuilder.build())
        } else {
            val notificationManager = NotificationManagerCompat.from(c)
            notificationManager.notify(0, notificationBuilder.build())
        }
    }

    fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

    private fun saveTokenToPreferenceAndUpload(refreshedToken: String) {
        if (session?.fPID != null) {
            registerChat(session?.fPID, refreshedToken)
        }
    }

    private fun registerChat(userId: String?, refreshedToken: String) {
        try {
            val params = HashMap<String, String?>()
            params["Channel"] = refreshedToken
            params["UserId"] = userId
            params["DeviceType"] = "ANDROID"
            params["clientId"] = clientId
            WithFloatTwoRepository.postRegisterRIA(params).toLiveData().observeForever {
                Log.d("Response", "Response : $it")
            }
        } catch (e: Exception) {
            Log.e("Ria_Register ", "API Exception:$e")
        }
    }

    companion object {
        private val TAG = RiaFirebaseMessagingServiceNew::class.java.simpleName
        private const val SAM_BUBBLE_MSG = "I have Got some data"
        private const val SAM_BUBBLE_MSG_KEY = "100"
        var deepLinkUrl: String? = null
    }
}