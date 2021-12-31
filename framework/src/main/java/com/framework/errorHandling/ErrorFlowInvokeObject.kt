package com.framework.errorHandling

import com.framework.BaseApplication.Companion.currentActivity

object ErrorFlowInvokeObject {

  fun errorOccurred(errorCode: Int, errorMessage: String, correlationId: String) {
    when (errorCode) {
      400, in 500..599 -> {
        currentActivity()?.supportFragmentManager?.let {
          ErrorOccurredBottomSheet(correlationId, errorMessage).show(it, ErrorOccurredBottomSheet::class.java.name)
        }
      }
    }
  }
}