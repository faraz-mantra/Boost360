package com.boost.presignup.utils

import android.app.Activity

public object PresignupManager {

  interface SignUpLoginHandler {
    fun loginClicked(activity: Activity)
  }

  private var listener: SignUpLoginHandler? = null

  init {
    println("Singleton class invoked.")
  }

  public fun setListener(listener: SignUpLoginHandler) {
    this.listener = listener
  }

  fun getListener(): SignUpLoginHandler? {
    return listener
  }

}