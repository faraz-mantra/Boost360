package com.appservice.ui.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentKycStatusBinding
import com.appservice.model.SessionData
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.framework.glide.util.glideLoad
import com.framework.models.BaseViewModel

class KYCStatusFragment : AppBaseFragment<FragmentKycStatusBinding, BaseViewModel>() {

  private var session: SessionData? = null
  private var saveKycData: PaymentKycRequest? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): KYCStatusFragment {
      val fragment = KYCStatusFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_kyc_status
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(binding?.btnKycStatusRefresh)
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData
    saveKycData = arguments?.getSerializable(IntentConstant.KYC_DETAIL.name) as? PaymentKycRequest
    updateUiData()
  }

  private fun updateUiData() {
    saveKycData?.actionData?.panCardDocument?.let { activity?.glideLoad(binding?.ivPanCardImage!!, it, R.drawable.placeholder_image) }
    binding?.tvPanNumber?.text = saveKycData?.actionData?.panNumber
    binding?.tvPanName?.text = saveKycData?.actionData?.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${saveKycData?.actionData?.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text = "${saveKycData?.actionData?.nameOfBank} - ${saveKycData?.actionData?.bankBranchName}"
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnKycStatusRefresh -> showLongToast("Refreshing status...")
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun onNavPressed() {
    val intent = Intent(baseActivity, Class.forName("com.nowfloats.NavigationDrawer.HomeActivity"))
    baseActivity.startActivity(intent)
    baseActivity.finish()
  }
}