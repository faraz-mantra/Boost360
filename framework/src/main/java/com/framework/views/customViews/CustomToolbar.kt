package com.framework.views.customViews

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import android.widget.TextView
import com.framework.decorators.getChildOrNull
import com.google.android.material.appbar.MaterialToolbar


class CustomToolbar : MaterialToolbar {

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

  }

  fun getTitleTextView(): TextView? {
    return this.getChildOrNull(1) as? TextView
  }

  fun getNavImageButton(): ImageButton? {
    return this.getChildOrNull(0) as? ImageButton
  }
}
