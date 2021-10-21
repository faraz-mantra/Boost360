package com.framework.extensions

import android.graphics.drawable.Drawable
import com.framework.views.customViews.CustomTextField
import com.framework.views.customViews.CustomTextView

var CustomTextField.drawableStart: Drawable?
  get() = compoundDrawablesRelative.get(0)
  set(value) = setDrawables(start = value)

var CustomTextField.drawableTop: Drawable?
  get() = compoundDrawablesRelative.get(1)
  set(value) = setDrawables(top = value)

var CustomTextField.drawableEnd: Drawable?
  get() = compoundDrawablesRelative.get(2)
  set(value) = setDrawables(end = value)

var CustomTextField.drawableBottom: Drawable?
  get() = compoundDrawablesRelative.get(2)
  set(value) = setDrawables(bottom = value)


fun CustomTextField.setDrawables(
  start: Drawable? = null,
  top: Drawable? = null,
  end: Drawable? = null,
  bottom: Drawable? = null
) = setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)

var CustomTextView.drawableStart: Drawable?
  get() = compoundDrawablesRelative.get(0)
  set(value) = setDrawables(start = value)

var CustomTextView.drawableTop: Drawable?
  get() = compoundDrawablesRelative.get(1)
  set(value) = setDrawables(top = value)

var CustomTextView.drawableEnd: Drawable?
  get() = compoundDrawablesRelative.get(2)
  set(value) = setDrawables(end = value)

var CustomTextView.drawableBottom: Drawable?
  get() = compoundDrawablesRelative.get(2)
  set(value) = setDrawables(bottom = value)


fun CustomTextView.setDrawables(
  start: Drawable? = null,
  top: Drawable? = null,
  end: Drawable? = null,
  bottom: Drawable? = null
) = setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom)