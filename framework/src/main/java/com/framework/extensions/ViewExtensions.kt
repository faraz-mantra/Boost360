package com.framework.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.framework.views.customViews.CustomAutoCompleteTextView
import com.framework.views.customViews.CustomEditText


fun CustomEditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(editable: Editable?) {
      afterTextChanged.invoke(editable.toString())
    }
  })
}

fun CustomEditText.onTextChanged(onTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      onTextChanged.invoke(p0.toString())
    }

    override fun afterTextChanged(editable: Editable?) {

    }
  })
}

fun CustomAutoCompleteTextView.onTextChanged(onTextChanged: (String) -> Unit) {
  this.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      onTextChanged.invoke(p0.toString())
    }

    override fun afterTextChanged(editable: Editable?) {

    }
  })
}

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
