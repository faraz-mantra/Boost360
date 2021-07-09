package com.framework.smsVerification

import android.content.Context
import android.content.IntentFilter
import com.google.android.gms.auth.api.phone.SmsRetriever

object SmsManager {
    private var intentFilter: IntentFilter? = null
    private var smsReceiver: SMSReceiver? = null
    private var otpListener: SMSReceiver.OTPReceiveListener? = null
    private lateinit var context: Context

    fun initManager(context: Context, listener: SMSReceiver.OTPReceiveListener?){
        this.context = context
        this.otpListener = listener
        // Init Sms Retriever >>>>
        initSmsListener()
        initBroadCast()
    }

    fun initBroadCast() {
        intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        smsReceiver = SMSReceiver()
        smsReceiver?.setOTPListener(otpListener)
    }


    fun initSmsListener() {
        val client = SmsRetriever.getClient(context)
        client.startSmsRetriever()
    }

    fun register(){
        context.registerReceiver(smsReceiver, intentFilter)
    }

    fun unregister(){
        context.unregisterReceiver(smsReceiver)
    }

}