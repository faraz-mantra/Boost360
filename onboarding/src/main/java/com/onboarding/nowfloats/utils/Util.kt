package com.onboarding.nowfloats.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import android.widget.Toast
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.extensions.checkIsFile

fun String.getWebViewUrl(): String {
    return (takeIf { checkIsFile().not() }?.let { this }
        ?: "https://docs.google.com/viewer?url=$this").checkHttp()
}

fun String.checkHttp(): String {
    return when {
        (this.startsWith("http://").not() && this.startsWith("https://").not()) -> "http://$this"
        else -> this
    }
}

fun Context.openWebPage(url: String): Boolean {
    return try {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}


fun viewToBitmap(view: View): Bitmap? {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun openChannelFacebookPage(context: Context, pageIdURI: String, pageURL: String) {
    val facebookIntent: Intent = try {
        context.packageManager.getPackageInfo(context.getString(R.string.facebook_package), 0)
        Intent(Intent.ACTION_VIEW, Uri.parse(pageIdURI))
    } catch (e: Exception) {
        Intent(Intent.ACTION_VIEW, Uri.parse(pageURL))
    }
    facebookIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY
    try {
        context.startActivity(facebookIntent)
    } catch (e: Exception) {
        Toast.makeText(context, context.getString(R.string.unable_to_open_facebook), Toast.LENGTH_SHORT).show()
    }
}