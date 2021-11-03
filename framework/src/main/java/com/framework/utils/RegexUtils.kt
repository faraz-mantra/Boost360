package com.framework.utils

import android.util.Log
import java.util.regex.Matcher
import java.util.regex.Pattern


object RegexUtils {
    private const val TAG = "RegexUtils"
    fun addStarToNumbers(text:String?): String {
        var result =text?:""
        val p: Pattern = Pattern.compile("\\d+")
        val m: Matcher = p.matcher(text)
        val star="*"
        while (m.find()) {
            result = result.replace(m.group(),"$star${m.group()}$star")
        }

        return result
        Log.i(TAG, "extractNumber: $result")
    }
}