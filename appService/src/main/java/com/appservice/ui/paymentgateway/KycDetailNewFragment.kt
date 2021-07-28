package com.appservice.ui.paymentgateway

import android.Manifest.permission.CALL_PHONE
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentKycDetailNewBinding
import com.appservice.model.kycData.DataKyc
import com.framework.models.BaseViewModel

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
      try {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:18601231233")
        if (ContextCompat.checkSelfPermission(
            baseActivity,
            CALL_PHONE
          ) == PackageManager.PERMISSION_GRANTED
        ) {
          baseActivity.startActivity(intent)
        } else requestPermissions(arrayOf(CALL_PHONE), 1)
      } catch (e: ActivityNotFoundException) {
        showLongToast("Error in your phone call!")
      }
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