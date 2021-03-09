package com.dashboard.controller.ui.ownerinfo

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentOwnerInfoBinding
import com.dashboard.viewmodel.DashboardViewModel

class FragmentOwenrInfo: AppBaseFragment<FragmentOwnerInfoBinding, DashboardViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_owner_info
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }

}