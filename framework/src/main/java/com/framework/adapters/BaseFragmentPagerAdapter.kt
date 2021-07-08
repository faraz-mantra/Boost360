package com.framework.adapters

import androidx.fragment.app.FragmentPagerAdapter
import com.framework.base.BaseFragment

class BaseFragmentPagerAdapter(
  fm: androidx.fragment.app.FragmentManager,
  private val pages: List<BaseFragment<*, *>>
) : FragmentPagerAdapter(fm) {

  override fun getItem(i: Int): BaseFragment<*, *> {
    return pages[i]
  }

  override fun getCount(): Int {
    return pages.size
  }
}
