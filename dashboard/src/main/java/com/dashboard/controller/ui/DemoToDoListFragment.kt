package com.dashboard.controller.ui

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentDemoBinding
import com.dashboard.model.DemoToDoListCardsModel
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.framework.models.BaseViewModel
import com.framework.views.VerticalSpaceItemDecoration

class DemoToDoListFragment : AppBaseFragment<FragmentDemoBinding, BaseViewModel>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): DemoToDoListFragment {
            val fragment = DemoToDoListFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_demo
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
    }

    private fun initUI() {
        binding?.rvCards?.adapter = AppBaseRecyclerViewAdapter(baseActivity, DemoToDoListCardsModel().getData())
    }

}