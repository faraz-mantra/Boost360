package com.dashboard.controller.ui.more

import android.graphics.Color
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentMoreBinding
import com.framework.models.BaseViewModel

class MoreFragment : AppBaseFragment<FragmentMoreBinding, BaseViewModel>() {
  override fun getLayout(): Int {
    return R.layout.fragment_more
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {

  }
}