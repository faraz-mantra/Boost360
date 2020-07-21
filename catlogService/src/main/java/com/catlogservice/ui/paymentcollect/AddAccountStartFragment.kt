package com.catlogservice.ui.paymentcollect

import android.os.Bundle
import android.view.View
import com.catlogservice.R
import com.catlogservice.base.AppBaseFragment
import com.catlogservice.constant.FragmentType
import com.catlogservice.databinding.FragmentAddAccountStartBinding
import com.framework.models.BaseViewModel

class AddAccountStartFragment : AppBaseFragment<FragmentAddAccountStartBinding, BaseViewModel>() {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddAccountStartFragment {
      val fragment = AddAccountStartFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_add_account_start
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.closeBtn, binding?.startBtn)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.closeBtn -> baseActivity.onNavPressed()
      binding?.startBtn -> arguments?.let { startFragmentAccountActivity(FragmentType.BANK_ACCOUNT_DETAILS, it) }
    }
  }
}
