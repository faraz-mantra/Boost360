package com.appservice.staffs.ui.profile

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentStaffListingBinding
import com.framework.models.BaseViewModel

class StaffProfileListingFragment : AppBaseFragment<FragmentStaffListingBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_staff_listing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}