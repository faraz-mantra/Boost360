package com.festive.poster.customviews

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.constraintlayout.widget.ConstraintLayout
import com.framework.R


class PercentageConstraintLayout:ConstraintLayout {

    var widthPercent=-1f
    var heightPercent=-1f
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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PercentageConstraintLayout)
        widthPercent = typedArray.getFloat(R.styleable.PercentageConstraintLayout_widthDisplayPercent,-1f)
        heightPercent = typedArray.getFloat(R.styleable.PercentageConstraintLayout_heightDisplayPercent,-1f)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {



        val width =if(widthPercent!=-1f) (context.resources.displayMetrics.widthPixels*widthPercent).toInt() else widthMeasureSpec
        val height =if(heightPercent!=-1f) (context.resources.displayMetrics.heightPixels*heightPercent).toInt() else heightMeasureSpec

        super.onMeasure(if(widthPercent!=-1f) MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY) else width,
            if(heightPercent!=-1f) MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY) else height)
    }
}