package com.framework.views.customViews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.framework.R
import com.framework.enums.TextType
import com.framework.enums.setTextStyle


class CustomButton : AppCompatButton {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
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

    this.setTextStyle(textType)
  }

}
