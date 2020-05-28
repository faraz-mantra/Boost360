package com.framework.views.customViews

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout


class CustomTextInputLayout : TextInputLayout {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    setCustomAttrs(context, attrs)
  }

  private fun setCustomAttrs(ctx: Context, attrs: AttributeSet?) {

  }
}
