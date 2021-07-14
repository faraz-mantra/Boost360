package com.appservice.ui.staffs.ui.breaks

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentBreaksConfirmBinding
import com.framework.models.BaseViewModel

class StaffBreakConfirmFragment : AppBaseFragment<FragmentBreaksConfirmBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_breaks_confirm
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }
}