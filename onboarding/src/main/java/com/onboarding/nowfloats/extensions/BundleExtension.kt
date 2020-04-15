package com.onboarding.nowfloats.extensions

import android.os.Bundle
import android.os.Parcelable
import com.onboarding.nowfloats.constant.IntentConstant

fun Bundle.addParcelable(key: IntentConstant, value: Parcelable?): Bundle {
  this.putParcelable(key.name, value)
  return this
}

fun Bundle.addInt(key: IntentConstant, value: Int): Bundle {
  this.putInt(key.name, value)
  return this
}

fun Bundle.addString(key: IntentConstant, value: String?): Bundle {
  this.putString(key.name, value)
  return this
}

fun <T : Parcelable> Bundle.getParcelable(key: IntentConstant): T? {
  return this.getParcelable<T>(key.name)
}

fun Bundle.getInt(key: IntentConstant): Int {
  return this.getInt(key.name)
}