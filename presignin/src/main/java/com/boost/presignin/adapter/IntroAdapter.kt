package com.boost.presignin.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.intro.PreSigninIntroFragment


class IntroAdapter(fragmentManager: FragmentManager, val items: List<IntroItem>) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
            return items.size;
    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return  view===`object`;
//    }

    override fun getItem(position: Int): Fragment {
        return PreSigninIntroFragment.newInstance(items[position],position);
    }



}