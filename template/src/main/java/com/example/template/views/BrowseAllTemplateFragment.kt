package com.example.template.views

import android.os.Bundle
import com.dashboard.base.AppBaseFragment
import com.example.template.R
import com.example.template.databinding.FragmentTodaysPickBinding
import com.framework.models.BaseViewModel

class BrowseAllTemplateFragment: AppBaseFragment<FragmentTodaysPickBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_todays_pick
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
    companion object {
        fun newInstance(bundle: Bundle = Bundle()): BrowseAllTemplateFragment {
            val fragment = BrowseAllTemplateFragment()
            fragment.arguments = bundle
            return fragment
        }


    }
}