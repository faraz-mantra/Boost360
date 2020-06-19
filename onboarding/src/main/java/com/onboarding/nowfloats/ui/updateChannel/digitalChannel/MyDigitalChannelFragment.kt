package com.onboarding.nowfloats.ui.updateChannel.digitalChannel

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import com.framework.models.BaseViewModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseFragment
import com.onboarding.nowfloats.databinding.FragmentDigitalChannelBinding

class MyDigitalChannelFragment : AppBaseFragment<FragmentDigitalChannelBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): MyDigitalChannelFragment {
      val fragment = MyDigitalChannelFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_digital_channel
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_alert_icon, menu)
  }
}