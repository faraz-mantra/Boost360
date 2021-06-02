package com.boost.presignin.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.boost.presignin.R

class PsnCustomToolbar @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  init {
    inflate(context, R.layout.psn_item_toolbar, this);
    val iconView = findViewById<ImageView>(R.id.psn_right_icon)
    setBackgroundColor(ContextCompat.getColor(context, R.color.white))
    if (attrs != null) {
      val a = context.obtainStyledAttributes(attrs, R.styleable.PsnCustomToolbar)
      val rightIcon = a.getDrawable(R.styleable.PsnCustomToolbar_rightIcon)
      if (rightIcon != null) {
        iconView.setImageDrawable(rightIcon)
      }
      a.recycle()
    }
  }
}