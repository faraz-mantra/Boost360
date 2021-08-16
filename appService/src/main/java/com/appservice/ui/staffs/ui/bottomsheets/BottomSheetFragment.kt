package com.appservice.ui.staffs.ui.bottomsheets

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appservice.R
import com.appservice.databinding.BottomSheeetPlanningBreakBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel

class BottomSheetFragment :
  BaseBottomSheetDialog<BottomSheeetPlanningBreakBinding, BaseViewModel>() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.bottom_sheeet_planning_break, container, false)
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return super.onCreateDialog(savedInstanceState)
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun getLayout(): Int {
    return R.layout.bottom_sheeet_planning_break
  }

  override fun onCreateView() {
  }
}