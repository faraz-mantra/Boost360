package com.festive.poster.ui.promoUpdates

import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.festive.poster.base.AppBaseFragment
import com.framework.models.BaseViewModel


internal class TabAdapter(val fragmentList: ArrayList<AppBaseFragment<out ViewDataBinding, out BaseViewModel>>,
                          baseFragment: Fragment) :
    FragmentStateAdapter(baseFragment) {

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position] as Fragment
    }

    // Looks familiar? Right, because it is our good old function from RecyclerView adapters
    override fun getItemCount(): Int = fragmentList.size


}