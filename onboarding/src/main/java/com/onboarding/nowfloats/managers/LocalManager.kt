package com.onboarding.nowfloats.managers

import java.util.*

object LocalManager {

  enum class Languages {
    EN, HI, TJ, TM
  }

  fun initialize() {
    Locale.setDefault(Locale.SIMPLIFIED_CHINESE)
  }

}