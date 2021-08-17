package com.framework.views.customViews

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import com.framework.R
import com.framework.enums.TextType
import com.framework.enums.setTextStyle

open class CustomEditText : AppCompatEditText {

  constructor(context: Context) : super(context) {
    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    setCustomAttrs(context, attrs)
  }

  @SuppressLint("CustomViewStyleable")
  private fun setCustomAttrs(context: Context, attrs: AttributeSet?) {
    if (attrs == null) return
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView)
    setTextStyle(typedArray)
//    inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
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
