package com.boost.presignin.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.model.IntroItem
import com.boost.presignin.model.newOnboarding.IntroItemNew
import com.boost.presignin.ui.intro.PreSignInIntroFragment
import com.boost.presignin.ui.newOnboarding.IntroSlideItemFragment

class IntroNewAdapter(
  fragmentManager: FragmentManager, lifecycle: Lifecycle,
  val items: List<IntroItemNew>,val next: (pos: Int?) -> Unit
) : FragmentStateAdapter(fragmentManager, lifecycle) {

  private val TAG = "IntroAdapter"
  override fun getItemCount(): Int {
    return items.size
  }

  override fun createFragment(position: Int): Fragment {
    Log.i(TAG, "createFragment: ")
    return IntroSlideItemFragment.newInstance(Bundle().apply {
      putSerializable(IntentConstant.INTRO_SLIDE_ITEM.name, items[position])
      putInt(IntentConstant.POSITION.name, position)
    }).apply { this.onNext = next }
  }
}