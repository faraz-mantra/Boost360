package com.framework.views.customViews

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.framework.R
import com.framework.extensions.getDensity
import com.google.android.material.card.MaterialCardView

open class CustomCardView : CardView {

  enum class Shadow constructor(val shadow: Int) {
    SHADOW_0(0), SHADOW_2(2), SHADOW_4(4), SHADOW_8(8),
    SHADOW_12(12), SHADOW_16(16), SHADOW_32(32), SHADOW_48(48);
  }

  enum class CornerRadius(val radius: Int) {
    RADIUS_0(0), RADIUS_2(2), RADIUS_4(4),
    RADIUS_8(8), RADIUS_12(12), RADIUS_16(16);
  }

  constructor(context: Context) : super(context) {
//    setCustomAttrs(context, null)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//    setCustomAttrs(context, attrs)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
    context,
    attrs,
    defStyle
  ) {
//    setCustomAttrs(context, attrs)
  }

  private fun setCustomAttrs(context: Context, attrs: AttributeSet?) {
    if (attrs == null) return
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCardView)

    setShadowFromAttr(typedArray)
    setCornerRadiusFromAttr(typedArray)

    this.requestLayout()
    this.invalidate()
    typedArray.recycle()
  }

  private fun setCornerRadiusFromAttr(typedArray: TypedArray) {
    val cornerRadius = CornerRadius.values()
      .firstOrNull { it.radius == typedArray.getInt(R.styleable.CustomCardView_corner_radius, -1) }

    cornerRadius?.let {
      setCornerRadius(it)
    } ?: kotlin.run {
      val radius = typedArray.getDimension(R.styleable.CustomCardView_cardCornerRadius, 0f)
      this.radius = radius * getDensity()
    }
  }

  private fun setShadowFromAttr(typedArray: TypedArray) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      val shadow: Shadow? = Shadow.values().firstOrNull {
        it.shadow == typedArray.getInt(
          R.styleable.CustomCardView_shadow_radius,
          -1
        )
      }
      shadow?.let {
        setShadowRadius(it)
      } ?: kotlin.run {
        val elevation = typedArray.getDimension(R.styleable.CustomCardView_cardElevation, 0f)
        this.cardElevation = getDensity() * elevation
      }
    }
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  fun setShadowRadius(shadow: Shadow) {
    var elevation = when (shadow) {
      Shadow.SHADOW_0 -> 0
      Shadow.SHADOW_2 -> 2
      Shadow.SHADOW_4 -> 4
      Shadow.SHADOW_8 -> 8
      Shadow.SHADOW_12 -> 12
      Shadow.SHADOW_16 -> 16
      Shadow.SHADOW_32 -> 32
      Shadow.SHADOW_48 -> 48
    }
    this.cardElevation = elevation * getDensity()
  }

  fun setCornerRadius(radius: CornerRadius) {
    val radiusValue = when (radius) {
      CornerRadius.RADIUS_0 -> 0
      CornerRadius.RADIUS_2 -> 2
      CornerRadius.RADIUS_4 -> 4
      CornerRadius.RADIUS_8 -> 8
      CornerRadius.RADIUS_12 -> 12
      CornerRadius.RADIUS_16 -> 16
    }
    this.radius = radiusValue * getDensity()
  }
}