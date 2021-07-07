package com.framework.views.customViews

import android.content.Context
import android.util.AttributeSet
import com.framework.R
import com.google.android.material.button.MaterialButton

class CustomMaterialButton : MaterialButton {

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init(context, attrs, 0)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
    context,
    attrs,
    defStyleAttr
  ) {
    init(context, attrs, defStyleAttr)
  }

  private fun init(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
    val typedArray =
      context.theme.obtainStyledAttributes(attrs, R.styleable.CustomMaterialButton, defStyleAttr, 0)
    isEnabled = isEnabled
    requestLayout()
    invalidate()
    typedArray.recycle()
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    isClickable = enabled
  }
}
