package com.festive.poster.ui.promoUpdates.pastUpdates

import android.os.Bundle
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.databinding.FragmentStatsBinding
import com.framework.models.BaseViewModel

class StatsFragment : AppBaseFragment<FragmentStatsBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): StatsFragment {
            val fragment = StatsFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_stats
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}