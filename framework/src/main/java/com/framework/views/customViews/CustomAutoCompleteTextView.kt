package com.framework.views.customViews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.framework.R
import com.framework.enums.TextType
import com.framework.enums.setTextStyle


class CustomAutoCompleteTextView : AppCompatAutoCompleteTextView {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
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
    val textType = TextType.values().firstOrNull {
      it.type == typedArray?.getInt(R.styleable.CustomTextView_textType, -1)
    }
    this.setTextStyle(textType)
  }

  override fun enoughToFilter(): Boolean {
    return true
  }

  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_DOWN) {
      performClick()
    }
    return super.onTouchEvent(event)
  }

  override fun performClick(): Boolean {
    if (filter != null && !isPopupShowing) {
      performFiltering(text, 0)
      showDropDown()
    }
    return super.performClick()
  }
}
