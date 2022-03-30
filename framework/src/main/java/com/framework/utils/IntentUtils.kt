package com.framework.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.framework.BaseApplication
import com.framework.R


object IntentUtils {

    fun shareText(context:Context,text:String,packageName:String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.`package`=packageName
        try {
            context.startActivity(intent)
        }catch (e:ActivityNotFoundException){
            Toast.makeText(BaseApplication.instance, BaseApplication.instance
                .getString(R.string.req_app_not_installed), Toast.LENGTH_LONG).show()

        }
    }

    fun composeEmail(context:Context,body:String,subject: String?=null,addresses: Array<String?>?=null) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body);
        try {
            context.startActivity(intent)
        }catch (e:ActivityNotFoundException){
            Toast.makeText(BaseApplication.instance, BaseApplication.instance
                .getString(R.string.no_email_app_installed), Toast.LENGTH_LONG).show()

        }

    }
}