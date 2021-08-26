package com.example.template.views

import android.os.Bundle
import com.dashboard.base.AppBaseFragment
import com.example.template.R
import com.example.template.databinding.ActivityUpdateCreationBinding
import com.example.template.databinding.FragmentTodaysPickBinding
import com.example.template.models.TodaysPickModel
import com.framework.models.BaseViewModel

class TodaysPickFragment: AppBaseFragment<FragmentTodaysPickBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_todays_pick
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): TodaysPickFragment {
            val fragment = TodaysPickFragment()
            fragment.arguments = bundle
            return fragment
        }


    }

    override fun onCreateView() {
        super.onCreateView()
        val datList = arrayListOf(
            TodaysPickModel(),
            TodaysPickModel(),
            TodaysPickModel(),

            )

    }
}