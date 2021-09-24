package com.dashboard.controller.ui.profile

import android.os.Bundle
import android.view.View
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.databinding.FragmentUserProfileBinding
import com.framework.models.BaseViewModel
import com.framework.pref.UserSessionManager

class CropProfileImageFragment : AppBaseFragment<FragmentUserProfileBinding, BaseViewModel>() {

  private lateinit var session: UserSessionManager

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): CropProfileImageFragment {
      val fragment = CropProfileImageFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_user_profile
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()

  }

  override fun onClick(v: View) {
    super.onClick(v)

  }


}