package com.festive.poster.utils

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.framework.views.customViews.CustomTextView

fun changeColorOfSubstring(paramStringInt:Int, color: Int, substring:String, textView: CustomTextView){
    val paramString = textView.context.getString(paramStringInt)
    val spannable = SpannableString(paramString)
    spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(textView.context, color)), paramString.indexOf(substring), paramString.indexOf(substring) + substring.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = spannable
}