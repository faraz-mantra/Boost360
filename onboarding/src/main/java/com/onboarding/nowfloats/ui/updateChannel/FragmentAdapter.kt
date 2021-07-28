package com.onboarding.nowfloats.ui.updateChannel

import android.view.View
import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.framework.base.BaseFragment

class FragmentAdapter(
  private val fragmentList: ArrayList<BaseFragment<*, *>>,
  @NonNull fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

  override fun createFragment(position: Int): Fragment = fragmentList[position]

  override fun getItemCount(): Int = fragmentList.size

  fun getViewAtPosition(position: Int): View? = fragmentList[position].view
}
