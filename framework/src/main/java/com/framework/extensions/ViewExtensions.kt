@file:JvmName("ViewExtensionsKt")

package com.framework.extensions

import android.view.View
import android.view.animation.AccelerateInterpolator

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

fun View.fadeIn() {
  if (isVisible()) return
  this.visible()
  this.alpha = 0f
  this.animate().alpha(1f).setDuration(240).setInterpolator(AccelerateInterpolator()).start()
}

fun View.fadeOut() {
  this.alpha = 1f
  this.animate().alpha(0f).setDuration(240).setInterpolator(AccelerateInterpolator()).start()
}

fun View.getDensity(): Float {
  return this.context.resources.displayMetrics.density
}