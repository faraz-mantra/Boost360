package com.appservice.ui.staffs.ui.services

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentServiceConfirmBinding
import com.framework.models.BaseViewModel

class StaffServicesConfirmFragment : AppBaseFragment<FragmentServiceConfirmBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_service_confirm
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }
}