package com.boost.presignin.ui.intro

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2


class CircularViewPagerHandler(var mViewPager: ViewPager2) : ViewPager2.OnPageChangeCallback() {

  private var mCurrentPosition = 0
  private var mScrollState = 0

  override fun onPageScrollStateChanged(state: Int) {
    super.onPageScrollStateChanged(state)
    handleScrollState(state)
    this.mScrollState = state
  }

  private fun handleScrollState(state: Int) {
    if (state == ViewPager.SCROLL_STATE_IDLE) {
      setNextItemIfNeeded()
    }
  }

  private fun setNextItemIfNeeded() {
    if (!isScrollStateSettling()) {
      handleSetNextItem()
    }
  }

  private fun isScrollStateSettling(): Boolean {
    return mScrollState == ViewPager.SCROLL_STATE_SETTLING
  }

  private fun handleSetNextItem() {
    val lastPosition: Int? = mViewPager.adapter?.itemCount?.minus(1)
    if (mCurrentPosition == 0) {
      lastPosition?.let { mViewPager.setCurrentItem(it, false) }
    } else if (mCurrentPosition == lastPosition) {
      mViewPager.setCurrentItem(0, false)
    }
  }

  override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
  }

  override fun onPageSelected(position: Int) {
    super.onPageSelected(position)
    mCurrentPosition = position
  }
}