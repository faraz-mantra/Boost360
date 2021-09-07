package com.framework.views.bottombar

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.framework.R
import com.framework.views.bottombar.Constants.DEFAULT_INDICATOR_COLOR
import com.framework.views.bottombar.Constants.DEFAULT_TEXT_COLOR
import com.framework.views.bottombar.Constants.DEFAULT_TEXT_COLOR_ACTIVE
import com.framework.views.bottombar.Constants.WHITE_COLOR_HEX
import kotlin.math.abs

class NiceBottomBar : View {
  private var canvas: Canvas? = null
  private val TAG = "NiceBottomBar"

  // Default attribute values
  private var barBackgroundColor = Color.parseColor(WHITE_COLOR_HEX)
  private var barIndicatorColor = Color.parseColor(DEFAULT_INDICATOR_COLOR)
  private var barIndicatorInterpolator = 4
  private var barIndicatorWidth = d2p(50f)
  private var itemTextVisible = true
  private var barIndicatorEnabled = true
  private var barIndicatorGravity = 1
  private var itemIconSize = d2p(18f)
  private var itemIconMargin = d2p(3f)
  private var itemTextColor = Color.parseColor(DEFAULT_TEXT_COLOR)
  private var itemTextColorActive = Color.parseColor(DEFAULT_TEXT_COLOR_ACTIVE)
  private var itemTextSize = d2p(11.0f)
  private var itemBadgeColor = itemTextColorActive
  private var itemBadgeTextColor = Color.parseColor(DEFAULT_TEXT_COLOR)
  private var itemFontActive = 0
  private var itemFontInActive = 0
  private var activeFontTypeface: Typeface? = null
  private var inActiveFontTypeface: Typeface? = null
  private var activeItem = 0
  private var clickPosition: ArrayList<Int>? = null

  /**
   * Dynamic variables
   */
  private var currentActiveItemColor = itemTextColor
  private var indicatorLocation = 0f

  // Represent long press time, when press time > longPressTime call the function callback.onItemLongClick
  private var longPressTime = 500
  private val titleSideMargins = d2p(12f)

  private var items = listOf<BottomBarItem>()
  private var itemsInActive = listOf<BottomBarItem>()
  private var itemsActive = listOf<BottomBarItem>()

  private var onItemSelectedListener: OnItemSelectedListener? = null
  private var onItemReselectedListener: OnItemReselectedListener? = null
  private var onItemLongClickListener: OnItemLongClickListener? = null
  private var pref: SharedPreferences? = null

  var onItemSelected: (Int) -> Unit = {}
  var onItemReselected: (Int) -> Unit = {}
  var onItemLongClick: (Int) -> Unit = {}

  private val paintIndicator = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.STROKE
    strokeWidth = 10f
    color = barIndicatorColor
    strokeCap = Paint.Cap.ROUND
  }

  private val paintText = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL
    color = itemTextColor
    textSize = itemTextSize
    textAlign = Paint.Align.CENTER
    isFakeBoldText = true
  }

  private val paintBadge = Paint().apply {
    isAntiAlias = true
    style = Paint.Style.FILL_AND_STROKE
    color = itemBadgeColor
    strokeWidth = 4f
  }

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    pref = context.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
    val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NiceBottomBar, 0, 0)
    barBackgroundColor = typedArray.getColor(R.styleable.NiceBottomBar_backgroundColor, this.barBackgroundColor)
    barIndicatorColor = typedArray.getColor(R.styleable.NiceBottomBar_indicatorColor, this.barIndicatorColor)
    barIndicatorWidth = typedArray.getDimension(R.styleable.NiceBottomBar_indicatorWidth, this.barIndicatorWidth)
    barIndicatorEnabled = typedArray.getBoolean(R.styleable.NiceBottomBar_indicatorEnabled, this.barIndicatorEnabled)
    itemTextColor = typedArray.getColor(R.styleable.NiceBottomBar_textColor, this.itemTextColor)
    itemTextVisible = typedArray.getBoolean(R.styleable.NiceBottomBar_textVisible, this.itemTextVisible)
    itemTextColorActive = typedArray.getColor(R.styleable.NiceBottomBar_textColorActive, this.itemTextColorActive)
    itemTextSize = typedArray.getDimension(R.styleable.NiceBottomBar_textSize, this.itemTextSize)
    itemIconSize = typedArray.getDimension(R.styleable.NiceBottomBar_iconSize, this.itemIconSize)
    itemIconMargin = typedArray.getDimension(R.styleable.NiceBottomBar_iconMargin, this.itemIconMargin)
    activeItem = typedArray.getInt(R.styleable.NiceBottomBar_activeItem, this.activeItem)
    barIndicatorInterpolator = typedArray.getInt(R.styleable.NiceBottomBar_indicatorInterpolator, this.barIndicatorInterpolator)
    barIndicatorGravity = typedArray.getInt(R.styleable.NiceBottomBar_indicatorGravity, this.barIndicatorGravity)
    itemBadgeColor = typedArray.getColor(R.styleable.NiceBottomBar_badgeColor, this.itemBadgeColor)
    itemBadgeTextColor = typedArray.getColor(R.styleable.NiceBottomBar_textBadgeColor, this.itemBadgeTextColor)
    itemFontActive = typedArray.getResourceId(R.styleable.NiceBottomBar_itemFontActive, this.itemFontActive)
    itemFontInActive = typedArray.getResourceId(R.styleable.NiceBottomBar_itemFontInActive, this.itemFontInActive)
    items = BottomBarParser(context, typedArray.getResourceId(R.styleable.NiceBottomBar_menu, 0)).parse()
    itemsInActive = BottomBarParser(context, typedArray.getResourceId(R.styleable.NiceBottomBar_menu, 0)).parse()
    itemsActive = BottomBarParser(context, typedArray.getResourceId(R.styleable.NiceBottomBar_activeMenu, 0)).parse()
    val clickPositionValue = typedArray.getString(R.styleable.NiceBottomBar_clickPosition)
    clickPosition = ArrayList()
    clickPositionValue?.split(",")?.forEach { it.toIntOrNull()?.let { it1 -> clickPosition!!.add(it1) } }
    typedArray.recycle()

    setBackgroundColor(barBackgroundColor)

    // Update default attribute values
    paintIndicator.color = barIndicatorColor
    paintText.color = itemTextColor
    paintText.textSize = itemTextSize
    paintBadge.color = itemBadgeColor

    // paintText.typeface
    if (itemFontActive != 0) activeFontTypeface = ResourcesCompat.getFont(context, itemFontActive)
    if (itemFontInActive != 0) inActiveFontTypeface = ResourcesCompat.getFont(context, itemFontInActive)
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)
    var lastX = 0f
    val itemWidth = width / items.size
    for (item in items) {
      // Prevent text overflow by shortening the item title
      var shorted = false
      while (paintText.measureText(item.title) > (itemWidth - titleSideMargins)) {
        item.title = item.title.dropLast(1)
        shorted = true
      }
      // Add ellipsis character to item text if it is shorted
      if (shorted) {
        item.title = item.title.dropLast(1)
        item.title += context.getString(R.string.ellipsis)
      }
      item.rect = RectF(lastX, 0f, itemWidth + lastX, height.toFloat())
      lastX += itemWidth
    }
    // Set initial active item
    setActiveItem(activeItem)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    this.canvas = canvas
    val textHeight = (paintText.descent() + paintText.ascent()) / 2
    // Push the item components from the top a bit if the indicator is at the top
    var additionalTopMargin = if (barIndicatorGravity == 1) 0f else 10f
    additionalTopMargin = if (itemTextVisible) additionalTopMargin else 20f
    for ((i, item) in items.withIndex()) {
      if (itemsActive.isNullOrEmpty().not() && (itemsActive.size == items.size)) {
        item.icon = if (i == activeItem) itemsActive[i].icon else itemsInActive[i].icon
        item.title = if (i == activeItem) itemsActive[i].title else itemsInActive[i].title
      } else DrawableCompat.setTint(item.icon, if (i == activeItem) currentActiveItemColor else itemTextColor)
      item.icon.mutate()
      item.icon.setBounds(
        item.rect.centerX().toInt() - itemIconSize.toInt() / 2,
        height / 2 - itemIconSize.toInt() - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt(),
        item.rect.centerX().toInt() + itemIconSize.toInt() / 2,
        height / 2 - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt()
      )
      item.icon.draw(canvas)
      // Draw item title
      this.paintText.color = if (i == activeItem) currentActiveItemColor else itemTextColor
      (if (i == activeItem) activeFontTypeface else inActiveFontTypeface)?.let { this.paintText.typeface = it }
      if (itemTextVisible) {
        canvas.drawText(item.title, item.rect.centerX(), item.rect.centerY() - textHeight + itemIconSize / 2 + (this.itemIconMargin / 2) + additionalTopMargin, paintText)
      }
      // Draw item badge
      if (item.badgeSize > 0) drawBadge(canvas, item, item.badgeText)
    }

    // Draw indicator
    if (barIndicatorEnabled)
      canvas.drawLine(
        indicatorLocation - barIndicatorWidth / 2, (if (barIndicatorGravity == 1) height - 5.0f else 5f),
        indicatorLocation + barIndicatorWidth / 2, (if (barIndicatorGravity == 1) height - 5.0f else 5f), paintIndicator
      )
  }

  // Handle item clicks
  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (event.action == MotionEvent.ACTION_UP && abs(event.downTime - event.eventTime) < longPressTime)
      for ((i, item) in items.withIndex())
        if (item.rect.contains(event.x, event.y))
          if (isClickPosition(i).not()) {
            if (i != this.activeItem) {
              setActiveItem(i)
              onItemSelected(i)
              onItemSelectedListener?.onItemSelect(i)
            } else {
              onItemReselected(i)
              onItemReselectedListener?.onItemReselect(i)
            }
          } else onItemSelectedListener?.onItemClick(i)

    if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_UP)
      if (abs(event.downTime - event.eventTime) > longPressTime)
        for ((i, item) in items.withIndex())
          if (item.rect.contains(event.x, event.y)) {
            if (isClickPosition(i).not()) {
              onItemLongClick(i)
              onItemLongClickListener?.onItemLongSelect(i)
            } else onItemLongClickListener?.onItemLongClick(i)
          }
    return true
  }

  private fun isClickPosition(i: Int): Boolean {
    return (clickPosition?.firstOrNull { it == i } != null)
  }

  // Draw item badge
  private fun drawBadge(canvas: Canvas, item: BottomBarItem, badgeText: String?) {
    Log.i(TAG, "drawBadge: $badgeText")
    Log.i(TAG, "drawBadge33:" + item.badgeText)
    paintBadge.style = Paint.Style.FILL
    paintBadge.color = Color.RED

    canvas.drawCircle(
      item.rect.centerX() + itemIconSize / 2 - 4,
      (height / 2).toFloat() - itemIconSize - itemIconMargin / 2 + 24, 18F, paintBadge
    )
    paintBadge.style = Paint.Style.STROKE
    paintBadge.color = barBackgroundColor

    canvas.drawText(
      badgeText ?: "", item.rect.centerX() + itemIconSize / 2 - 4,
      (height / 2).toFloat() - itemIconSize - itemIconMargin / 2 + 30,
      paintText.apply { color = Color.WHITE;textSize = 20f }
    )
  }

  // Add item badge
  fun setBadge(pos: Int, count: String) {
    if (pos >= 0 && pos < items.size && items[pos].badgeSize == 0f) {
      items[pos].badgeText = count
      val animator = ValueAnimator.ofFloat(0f, 15f)
      animator.duration = 100
      animator.addUpdateListener { animation ->
        items[pos].badgeSize = animation.animatedValue as Float
        invalidate()
      }
      animator.start()
    }
  }

  // Remove item badge
  fun removeBadge(pos: Int) {
    if (pos > 0 && pos < items.size && items[pos].badgeSize > 0f) {
      val animator = ValueAnimator.ofFloat(items[pos].badgeSize, 0f)
      animator.duration = 100
      animator.addUpdateListener { animation ->
        items[pos].badgeSize = animation.animatedValue as Float
        invalidate()
      }
      animator.start()
    }
  }

  fun getActiveItem(): Int {
    return activeItem
  }

  fun setActiveItem(pos: Int) {
    if (isClickPosition(pos).not()) {
      activeItem = pos
      animateIndicator(pos)
      setItemColors()
    }
  }

  private fun animateIndicator(pos: Int) {
    val animator = ValueAnimator.ofFloat(indicatorLocation, items[pos].rect.centerX())
    animator.interpolator = when (this.barIndicatorInterpolator) {
      0 -> AccelerateInterpolator()
      1 -> DecelerateInterpolator()
      2 -> AccelerateDecelerateInterpolator()
      3 -> AnticipateInterpolator()
      4 -> AnticipateOvershootInterpolator()
      5 -> LinearInterpolator()
      6 -> OvershootInterpolator()
      else -> AnticipateOvershootInterpolator()
    }

    animator.addUpdateListener { animation ->
      indicatorLocation = animation.animatedValue as Float
      invalidate()
    }
    animator.start()
  }

  // Apply transition animation to item color
  private fun setItemColors() {
    val animator = ValueAnimator.ofObject(ArgbEvaluator(), itemTextColor, itemTextColorActive)
    animator.addUpdateListener { currentActiveItemColor = it.animatedValue as Int }
    animator.start()
  }

  private fun d2p(dp: Float): Float {
    return resources.displayMetrics.densityDpi.toFloat() / 160.toFloat() * dp
  }

  fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
    this.onItemSelectedListener = listener
  }

  fun setOnItemReselectedListener(listener: OnItemReselectedListener) {
    this.onItemReselectedListener = listener
  }

  fun setOnItemLongClickListener(listener: OnItemLongClickListener) {
    this.onItemLongClickListener = listener
  }
}