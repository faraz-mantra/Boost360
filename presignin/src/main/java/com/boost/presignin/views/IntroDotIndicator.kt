package com.boost.presignin.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.boost.presignin.R
import com.framework.views.dotsindicator.DotsGradientDrawable
import com.framework.views.dotsindicator.DotsIndicator
import com.framework.views.dotsindicator.OnPageChangeListenerHelper

class IntroDotIndicator @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : DotsIndicator(context, attrs, defStyleAttr) {


  init {
    linearLayout?.gravity = Gravity.CENTER_VERTICAL
  }

  override fun addDot(index: Int) {
    if (index == 0) {
      val dot = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)
      val imageView = dot.findViewById<ImageView>(R.id.dot)
      val params = imageView.layoutParams as RelativeLayout.LayoutParams

      dot.layoutDirection = View.LAYOUT_DIRECTION_LTR

      params.height = dotsSize.toInt() * 2
      params.width = params.height
      params.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
      val background = DotsGradientDrawable()
      background.setColor(ContextCompat.getColor(context, R.color.transparent))
      background.cornerRadius = dotsSize



      if (isInEditMode) {
        background.setColor(if (0 == index) selectedDotColor else dotsColor)
      } else {
        background.setColor(if (pager!!.currentItem == index) selectedDotColor else dotsColor)
      }
      imageView.setBackgroundDrawable(background)
      imageView.setImageResource(R.drawable.psn_indicator_play)
      dot.setOnClickListener {
        if (dotsClickable && index < pager?.count ?: 0) {
          pager!!.setCurrentItem(index, true)
        }
      }
      imageView.setWidth(2 * dotsSize.toInt())
      dots.add(imageView)
      linearLayout!!.addView(dot)
    } else super.addDot(index)
  }

  override fun buildOnPageChangedListener(): OnPageChangeListenerHelper {
    return object : OnPageChangeListenerHelper() {
      override fun onPageScrolled(selectedPosition: Int, nextPosition: Int, positionOffset: Float) {
        val selectedDot = dots[selectedPosition]
        // Selected dot
        // val selectedDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset)).toInt()
        // selectedDot.setWidth(selectedDotWidth)

        if (dots.isInBounds(nextPosition)) {
          val nextDot = dots[nextPosition]

          //  val nextDotWidth = (dotsSize + dotsSize * (dotsWidthFactor - 1) * positionOffset).toInt()
          //  nextDot.setWidth(nextDotWidth)

          val selectedDotBackground = selectedDot.background as DotsGradientDrawable
          val nextDotBackground = nextDot.background as DotsGradientDrawable

          if (selectedDotColor != dotsColor) {
            val selectedColor = argbEvaluator.evaluate(
              positionOffset, selectedDotColor,
              dotsColor
            ) as Int
            val nextColor = argbEvaluator.evaluate(
              positionOffset, dotsColor,
              selectedDotColor
            ) as Int

            nextDotBackground.setColor(nextColor)

            if (progressMode && selectedPosition <= pager!!.currentItem) {
              selectedDotBackground.setColor(selectedDotColor)
            } else {
              selectedDotBackground.setColor(selectedColor)
            }
          }
        }

        invalidate()
      }

      override fun resetPosition(position: Int) {
        // dots[position].setWidth(dotsSize.toInt())
        refreshDotColor(position)
      }

      override val pageCount: Int
        get() = dots.size
    }
  }

}