package com.festive.poster.customviews

import android.content.Context
import android.util.AttributeSet
import com.framework.R
import com.framework.views.customViews.CustomImageView

class SquareImageView: CustomImageView {

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomImageView)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)

    }

}