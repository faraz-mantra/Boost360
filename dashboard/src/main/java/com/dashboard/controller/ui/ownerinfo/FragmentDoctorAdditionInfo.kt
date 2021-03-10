package com.dashboard.controller.ui.ownerinfo

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAddDoctorsInfoBinding
import com.dashboard.viewmodel.DashboardViewModel

class FragmentDoctorAdditionInfo : AppBaseFragment<FragmentAddDoctorsInfoBinding, DashboardViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_add_doctors_info
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }
}