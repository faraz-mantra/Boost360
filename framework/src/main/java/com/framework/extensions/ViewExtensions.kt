@file:JvmName("ViewExtensionsKt")

package com.framework.extensions

import android.view.View

fun View.visible() {
  this.visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
  return this.visibility == View.VISIBLE
}

fun View.gone() {
  this.visibility = View.GONE
}

fun View.isGone(): Boolean {
  return this.visibility == View.GONE
}

fun View.invisible() {
  this.visibility = View.INVISIBLE
}

fun View.isInvisible(): Boolean {
  return this.visibility == View.INVISIBLE
}

fun View.refreshLayout() {
  this.requestLayout()
  this.invalidate()
}

fun View.getDensity(): Float {
  return this.context.resources.displayMetrics.density
}