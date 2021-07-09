package com.framework.views.customViews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import com.framework.R
import com.framework.enums.TextType
import com.framework.enums.setTextStyle
import com.google.android.material.radiobutton.MaterialRadioButton

class CustomRadioButton : MaterialRadioButton {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    setCustomAttrs(context, attrs)
  }

  private fun setCustomAttrs(context: Context, attrs: AttributeSet?) {
    if (attrs == null) return
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)

    setTextStyle(typedArray)

    this.requestLayout()
    this.invalidate()
    typedArray.recycle()
  }

  private fun setTextStyle(typedArray: TypedArray?) {
    val textType = TextType.values().firstOrNull {
      it.type == typedArray?.getInt(R.styleable.CustomTextView_textType, -1)
    }

    this.setTextStyle(textType ?: TextType.BODY_1)
  }
}
