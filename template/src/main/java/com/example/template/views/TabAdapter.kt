package com.example.template.views

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


internal class TabAdapter(val fragmentList:List<Fragment>, fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    // Looks familiar? Right, because it is our good old function from RecyclerView adapters
    override fun getItemCount(): Int = fragmentList.size


}