package com.appservice.ui.aptsetting.ui

import com.appservice.R
import com.appservice.base.AppBaseFragment
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
}