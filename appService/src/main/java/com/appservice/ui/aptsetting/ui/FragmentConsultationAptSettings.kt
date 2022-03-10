package com.appservice.ui.aptsetting.ui

import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentConsultationAptSettingBinding
import com.framework.models.BaseViewModel

class FragmentConsultationAptSettings : AppBaseFragment<FragmentConsultationAptSettingBinding, BaseViewModel>() {

  companion object {
    fun newInstance(): FragmentConsultationAptSettings {
      return FragmentConsultationAptSettings()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_consultation_apt_setting
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnConfirm, binding?.btnMinutes)
    binding?.toggleHome?.setOnToggledListener { _, isOn ->
      binding?.viewData?.visibility = if (isOn) View.VISIBLE else View.GONE
      binding?.noteTxt?.visibility = if (isOn) View.VISIBLE else View.GONE
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnConfirm -> {

      }
      binding?.btnMinutes -> {

      }
    }
  }
}