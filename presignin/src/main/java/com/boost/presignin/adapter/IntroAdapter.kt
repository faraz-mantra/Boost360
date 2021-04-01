package com.boost.presignin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.intro.PreSigninIntroFragment


class IntroAdapter(fragmentManager: FragmentManager,lifecycle: Lifecycle, val items: List<IntroItem>) : FragmentStateAdapter(fragmentManager,lifecycle) {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return PreSigninIntroFragment.newInstance(items[position],position);
    }
}