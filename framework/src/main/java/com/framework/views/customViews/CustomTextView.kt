package com.framework.views.customViews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.framework.R
import com.framework.enums.TextType.values
import com.framework.enums.setTextStyle
import android.graphics.PorterDuff

import android.graphics.PorterDuffColorFilter

import androidx.core.content.ContextCompat


open class CustomTextView : AppCompatTextView {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
    context,
    attrs,
    defStyle
  ) {
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
    val textType = values().firstOrNull {
      it.type == typedArray?.getInt(R.styleable.CustomTextView_textType, -1)
    }

    this.setTextStyle(textType)
  }

   fun setDrawableColor(customTextView:CustomTextView, color: Int) {
    for (drawable in customTextView.compoundDrawables) {
      if (drawable != null) {
        drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(customTextView.context, color), PorterDuff.Mode.SRC_IN)
      }
    }
  }
}