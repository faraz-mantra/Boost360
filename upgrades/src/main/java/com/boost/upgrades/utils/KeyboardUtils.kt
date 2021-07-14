package com.boost.upgrades.utils

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager

object KeyboardUtils : ViewTreeObserver.OnGlobalLayoutListener {

  private val MAGIC_NUMBER = 200

  private var mCallback: SoftKeyboardToggleListener? = null
  private var mRootView: View? = null
  private var prevValue: Boolean? = null
  private var mScreenDensity = 0f
  private val sListenerMap: HashMap<SoftKeyboardToggleListener, KeyboardUtils> = HashMap()

  interface SoftKeyboardToggleListener {
    fun onToggleSoftKeyboard(isVisible: Boolean)
  }

  override fun onGlobalLayout() {
    val r = Rect()
    mRootView!!.getWindowVisibleDisplayFrame(r)

    val heightDiff: Int = mRootView!!.rootView.height - (r.bottom - r.top)
    val dp = heightDiff / mScreenDensity
    val isVisible = dp > MAGIC_NUMBER

    if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
      prevValue = isVisible
      mCallback!!.onToggleSoftKeyboard(isVisible)
    }
  }

  /**
   * Add a new keyboard listener
   * @param act calling activity
   * @param listener callback
   */
  fun addKeyboardToggleListener(
    act: Activity,
    listener: SoftKeyboardToggleListener
  ) {
    removeKeyboardToggleListener(listener)
    sListenerMap.put(listener, KeyboardUtils(act, listener))
  }

  /**
   * Remove a registered listener
   * @param listener [SoftKeyboardToggleListener]
   */
  fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener?) {
    if (sListenerMap.containsKey(listener)) {
      val k = sListenerMap[listener]
      k!!.removeListener()
      sListenerMap.remove(listener)
    }
  }

  /**
   * Remove all registered keyboard listeners
   */
  fun removeAllKeyboardToggleListeners() {
    for (l in sListenerMap.keys) sListenerMap[l]!!.removeListener()
    sListenerMap.clear()
  }

  /**
   * Manually toggle soft keyboard visibility
   * @param context calling context
   */
  fun toggleKeyboardVisibility(context: Context) {
    val inputMethodManager: InputMethodManager =
      context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager != null) inputMethodManager.toggleSoftInput(
      InputMethodManager.SHOW_FORCED,
      0
    )
  }

  /**
   * Force closes the soft keyboard
   * @param activeView the view with the keyboard focus
   */
  fun forceCloseKeyboard(activeView: View) {
    val inputMethodManager: InputMethodManager =
      activeView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (inputMethodManager != null) inputMethodManager.hideSoftInputFromWindow(
      activeView.windowToken,
      0
    )
  }

  private fun removeListener() {
    mCallback = null
    mRootView!!.viewTreeObserver.removeOnGlobalLayoutListener(this)
  }

  private fun KeyboardUtils(
    act: Activity,
    listener: SoftKeyboardToggleListener
  ): KeyboardUtils {
    mCallback = listener
    mRootView =
      (act.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
    mRootView!!.getViewTreeObserver().addOnGlobalLayoutListener(this)
    mScreenDensity = act.resources.displayMetrics.density
    return this
  }
}