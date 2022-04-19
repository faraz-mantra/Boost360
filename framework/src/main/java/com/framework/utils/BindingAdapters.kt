package com.framework.utils

import android.view.View
import androidx.databinding.BindingAdapter


@BindingAdapter("layout_height")
fun setLayoutHeight(view: View, height: Float) {
    val layoutParams = view.getLayoutParams()
    layoutParams.height = height.toInt()
    view.setLayoutParams(layoutParams)
}

@BindingAdapter("layout_width")
fun setLayoutWidth(view: View, width: Float) {
    val layoutParams = view.getLayoutParams()
    layoutParams.width = width.toInt()
    view.setLayoutParams(layoutParams)
}


