package com.appservice.ui.paymentgateway

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentKycDetailNewBinding
import com.appservice.model.kycData.DataKyc
import com.framework.models.BaseViewModel
import com.framework.utils.makeCall

class KycDetailNewFragment : AppBaseFragment<FragmentKycDetailNewBinding, BaseViewModel>() {
  private var dataKyc: DataKyc? = null
  override fun getLayout(): Int {
    return R.layout.fragment_kyc_detail_new
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    dataKyc = arguments?.getSerializable(IntentConstant.KYC_DETAIL.name) as? DataKyc
    dataKyc?.let { setUi(it) }
    binding?.btnContact?.setOnClickListener {
      makeCall(getString(R.string.contact_us_number))
    }
  }

  private fun setUi(dataKyc: DataKyc) {
    binding?.tvPanNumber?.text = dataKyc.panNumber
    binding?.namePanCard?.text = dataKyc.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${dataKyc.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text = "${dataKyc.nameOfBank} - ${dataKyc.bankBranchName}"
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.menu_info, menu)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_info -> {
        showLongToast("Coming soon...")
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }
}