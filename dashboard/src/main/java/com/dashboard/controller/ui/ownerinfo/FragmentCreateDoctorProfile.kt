package com.dashboard.controller.ui.ownerinfo

import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAddDoctorBinding
import com.dashboard.viewmodel.DashboardViewModel

class FragmentCreateDoctorProfile : AppBaseFragment<FragmentAddDoctorBinding, DashboardViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_add_doctor
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }
}