package com.framework.views.shadowview

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.widget.RelativeLayout
import com.framework.R

class ShadowLayout(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
  RelativeLayout(context, attrs, defStyleAttr) {
  private val mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val mRectF: RectF = RectF()


  private var mShadowColor: Int = Color.TRANSPARENT


  private var mShadowRadius = 0f

  private var mShadowDx = 0f


  private var mShadowDy = 0f


  private var mShadowSide = ALL

  private var mShadowShape = SHAPE_RECTANGLE

  constructor(context: Context?) : this(context, null)
  constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val effect = mShadowRadius + dip2px(5f)
    var rectLeft = 0f
    var rectTop = 0f
    var rectRight: Float = this.measuredWidth.toFloat()
    var rectBottom: Float = this.measuredHeight.toFloat()
    var paddingLeft = 0
    var paddingTop = 0
    var paddingRight = 0
    var paddingBottom = 0
    this.width
    if (mShadowSide and LEFT == LEFT) {
      rectLeft = effect
      paddingLeft = effect.toInt()
    }
    if (mShadowSide and TOP == TOP) {
      rectTop = effect
      paddingTop = effect.toInt()
    }
    if (mShadowSide and RIGHT == RIGHT) {
      rectRight = this.measuredWidth - effect
      paddingRight = effect.toInt()
    }
    if (mShadowSide and BOTTOM == BOTTOM) {
      rectBottom = this.measuredHeight - effect
      paddingBottom = effect.toInt()
    }
    if (mShadowDy != 0.0f) {
      rectBottom -= mShadowDy
      paddingBottom += mShadowDy.toInt()
    }
    if (mShadowDx != 0.0f) {
      rectRight -= mShadowDx
      paddingRight += mShadowDx.toInt()
    }
    mRectF.left = rectLeft
    mRectF.top = rectTop
    mRectF.right = rectRight
    mRectF.bottom = rectBottom
    this.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  }

  /**
   * 真正绘制阴影的方法
   */
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    setUpShadowPaint()
    if (mShadowShape == SHAPE_RECTANGLE) {
      canvas.drawRect(mRectF, mPaint)
    } else if (mShadowShape == SHAPE_OVAL) {
      canvas.drawCircle(
        mRectF.centerX(),
        mRectF.centerY(),
        Math.min(mRectF.width(), mRectF.height()) / 2,
        mPaint
      )
    }
  }

  fun setShadowColor(shadowColor: Int) {
    mShadowColor = shadowColor
    requestLayout()
    postInvalidate()
  }

  fun setShadowRadius(shadowRadius: Float) {
    mShadowRadius = shadowRadius
    requestLayout()
    postInvalidate()
  }

  /**
   * @param attrs
   */
  private fun init(attrs: AttributeSet?) {
    setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    this.setWillNotDraw(false)
    val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowLayout)
    if (typedArray != null) {
      mShadowColor = typedArray.getColor(
        R.styleable.ShadowLayout_shadowColor,
        context.resources.getColor(R.color.black)
      )
      mShadowRadius = typedArray.getDimension(R.styleable.ShadowLayout_shadowRadius, dip2px(0f))
      mShadowDx = typedArray.getDimension(R.styleable.ShadowLayout_shadowDx, dip2px(0f))
      mShadowDy = typedArray.getDimension(R.styleable.ShadowLayout_shadowDy, dip2px(0f))
      mShadowSide = typedArray.getInt(R.styleable.ShadowLayout_shadowSide, ALL)
      mShadowShape = typedArray.getInt(R.styleable.ShadowLayout_shadowShape, SHAPE_RECTANGLE)
      typedArray.recycle()
    }
    setUpShadowPaint()
  }

  private fun setUpShadowPaint() {
    mPaint.reset()
    mPaint.isAntiAlias = true
    mPaint.color = Color.TRANSPARENT
    mPaint.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor)
  }

  /**
   * dip2px dp 值转 px 值
   *
   * @param dpValue dp 值
   * @return px 值
   */
  private fun dip2px(dpValue: Float): Float {
    val dm: DisplayMetrics = context.resources.displayMetrics
    val scale: Float = dm.density
    return dpValue * scale + 0.5f
  }

  companion object {
    const val ALL = 0x1111
    const val LEFT = 0x0001
    const val TOP = 0x0010
    const val RIGHT = 0x0100
    const val BOTTOM = 0x1000
    const val SHAPE_RECTANGLE = 0x0001
    const val SHAPE_OVAL = 0x0010
  }

  init {
    init(attrs)
  }
}