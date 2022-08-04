package com.framework.utils

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

object ApiLevelUtil {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
    fun isApiLevelEqualGreaterThanR(versionCode: Int) : Boolean{
        return Build.VERSION.SDK_INT >= versionCode
    }
}