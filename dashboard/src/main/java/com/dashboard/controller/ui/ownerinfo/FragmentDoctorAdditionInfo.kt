package com.dashboard.controller.ui.ownerinfo

import android.os.Bundle
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentAddDoctorsInfoBinding
import com.dashboard.viewmodel.DashboardViewModel

class FragmentDoctorAdditionInfo : AppBaseFragment<FragmentAddDoctorsInfoBinding, DashboardViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_add_doctors_info
    }
    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): FragmentDoctorAdditionInfo {
            val fragment = FragmentDoctorAdditionInfo()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }
}