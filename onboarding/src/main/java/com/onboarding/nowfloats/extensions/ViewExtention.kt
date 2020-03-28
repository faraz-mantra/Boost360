package com.onboarding.nowfloats.extensions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.text.Editable
import android.text.TextWatcher
import com.framework.views.customViews.CustomTextField
import java.io.File

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