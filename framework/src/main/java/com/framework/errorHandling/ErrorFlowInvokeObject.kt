package com.framework.errorHandling

import com.framework.BaseApplication.Companion.currentActivity

object ErrorFlowInvokeObject {

  fun errorOccurred(errorCode: Int, errorMessage: String, correlationId: String) {
    when (errorCode) {
      400, in 500..599 -> {
        currentActivity()?.supportFragmentManager?.let {
          ErrorOccurredBottomSheet(correlationId, errorMessage).show(it, ErrorOccurredBottomSheet::class.java.name)
        }
//                BaseApplication.instance.startActivity(Intent(BaseApplication.instance, ErrorTransparentActivity::class.java)
//                    .putExtra(IntentConstants.ERROR_CODE_OCCURRED.name, correlationId)
//                    .putExtra(IntentConstants.ERROR_MESSAGE.name, errorMessage)
//                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS))
      }
    }
  }
}