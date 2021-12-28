package com.framework.errorHandling

import android.content.Intent
import com.framework.BaseApplication
import com.framework.enums.IntentConstants

object ErrorFlowInvokeObject {

    fun errorOccurred(errorCode: Int) {
        when (errorCode) {
            400, in 500..599 -> {
                BaseApplication.instance.startActivity(Intent(BaseApplication.instance, ErrorTransparentActivity::class.java)
                    .putExtra(IntentConstants.ERROR_CODE_OCCURRED.name, errorCode.toString())
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS))
            }
        }
    }
}