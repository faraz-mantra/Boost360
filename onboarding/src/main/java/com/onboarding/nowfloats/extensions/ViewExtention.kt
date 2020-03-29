package com.onboarding.nowfloats.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import com.framework.views.customViews.CustomTextField
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun CustomTextField.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}


fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun File.getBitmap(): Bitmap? {
    return ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(this.path), 800, 800)
}

fun Context.Get_hash_key() {
    try {
        val info = this.packageManager.getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT))
        }
    } catch (e: PackageManager.NameNotFoundException) {

    } catch (e: NoSuchAlgorithmException) {

    }
}

fun replaceCountryCode(number: String): String? {
    var number = number
    if (!TextUtils.isEmpty(number) && number.trim { it <= ' ' }.startsWith("+91")) {
        number = number.trim { it <= ' ' }.replace("+91", "")
    }
    return number
}
