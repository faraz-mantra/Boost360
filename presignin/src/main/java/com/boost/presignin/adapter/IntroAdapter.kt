package com.boost.presignin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.intro.PreSignInIntroFragment

class IntroAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle,
    val items: List<IntroItem>, val skip: () -> Unit,
    val playPauseState: (state: Boolean) -> Unit,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

  override fun getItemCount(): Int {
    return items.size
  }

  override fun createFragment(position: Int): Fragment {
    return PreSignInIntroFragment.newInstance(items[position], position).apply {
      this.onSkip = skip
      this.playPause = playPauseState
    };
  }
}