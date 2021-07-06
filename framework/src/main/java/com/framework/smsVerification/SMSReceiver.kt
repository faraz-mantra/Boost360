package com.framework.smsVerification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class SMSReceiver : BroadcastReceiver() {
  private var otpListener: OTPReceiveListener? = null

  fun setOTPListener(otpListener: OTPReceiveListener?) {
    this.otpListener = otpListener
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
      val extras = intent.extras
      val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
      when (status!!.statusCode) {
        CommonStatusCodes.SUCCESS -> {
          val sms = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String?
          sms?.let {
            // val p = Pattern.compile("[0-9]+") check a pattern with only digit
            val p = Pattern.compile("(\\d{4})")
            val m = p.matcher(it)
            if (m.find()) {
              val otp = m.group(0)
              if (otpListener != null) {
                if (otp.isNullOrEmpty().not() && otp.length == 4) {
                  otpListener!!.onOTPReceived(otp)
                }
              }
            }
          }
        }
      }
    }
  }

  interface OTPReceiveListener {
    fun onOTPReceived(otp: String?)
  }
}