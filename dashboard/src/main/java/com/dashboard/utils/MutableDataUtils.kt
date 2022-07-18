package com.dashboard.utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object MutableDataUtils {
  private val _openBusinessCard = MutableSharedFlow<Boolean>(extraBufferCapacity = 1)
  var openBusinessCard = _openBusinessCard.asSharedFlow()

  fun openBusinessCard(isOpen: Boolean) {
    _openBusinessCard.tryEmit(isOpen)
  }
}