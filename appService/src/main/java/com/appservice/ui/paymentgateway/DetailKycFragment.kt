package com.appservice.ui.paymentgateway

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentDetailsKycBinding
import com.appservice.model.SessionData
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import org.json.JSONObject

class DetailKycFragment : AppBaseFragment<FragmentDetailsKycBinding, WebBoostKitViewModel>() {

  private var session: SessionData? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): DetailKycFragment {
      val fragment = DetailKycFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_details_kyc
  }

  override fun onCreateView() {
    super.onCreateView()
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData ?: return
    getKycDetails()
  }

  private fun getKycDetails() {
    showProgress()
    viewModel?.getKycData(session?.auth_1,getQuery())?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val resp = it as? PaymentKycDataResponse
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          if (resp?.data.isNullOrEmpty().not()) {
            binding?.mainView?.visible()
            setData(resp!!.data!![0])
          } else {
            binding?.mainView?.gone()
            showLongToast("Kyc detail not found")
          }
        } else {
          binding?.mainView?.gone()
          showLongToast(if (it.message().isNotEmpty()) it.message() else "Kyc detail getting error")
        }
      } else showLongToast(resources.getString(R.string.internet_connection_not_available))
      hideProgress()
    })
  }

  private fun setData(saveKycData: DataKyc) {
    binding?.tvAccount?.text = "A/C No. ${saveKycData.bankAccountNumber}"
    binding?.tvBranch?.text = "${saveKycData.nameOfBank} - ${saveKycData.bankBranchName}"
  }

  private fun getQuery(): String? {
    val json = JSONObject()
    json.put("fpTag", session?.fpTag)
    return json.toString()
  }

  override fun getViewModelClass(): Class<WebBoostKitViewModel> {
    return WebBoostKitViewModel::class.java
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
