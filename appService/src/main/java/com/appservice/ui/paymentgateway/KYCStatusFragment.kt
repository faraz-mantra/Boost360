package com.appservice.ui.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentKycStatusBinding
import com.appservice.model.SessionData
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import org.json.JSONObject
import java.util.*

class KYCStatusFragment : AppBaseFragment<FragmentKycStatusBinding, WebBoostKitViewModel>() {

  private var session: SessionData? = null
  private var saveKycData: PaymentKycRequest? = null
  private var menuEdit: MenuItem? = null

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
    if (saveKycData == null) getKycDetails()
    else updateUiData()
    initLottieAnimation()
  }

  private fun getKycDetails() {
    showProgress()
    viewModel?.getKycData(session?.auth_1, getQuery())?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val resp = it as? PaymentKycDataResponse
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          if (resp?.data.isNullOrEmpty().not()) {
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

  private fun setData(dataKyc: DataKyc) {
    if (dataKyc.isVerified == DataKyc.Verify.ALLOW_EDIT.name) menuEdit?.isVisible = true
    dataKyc.panCardDocument?.let { activity?.glideLoad(binding?.ivPanCardImage!!, it, R.drawable.placeholder_image) }
    binding?.tvPanNumber?.text = dataKyc.panNumber?.toUpperCase(Locale.ROOT)
    binding?.tvPanName?.text = dataKyc.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${dataKyc.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text = "${dataKyc.nameOfBank} - ${dataKyc.bankBranchName}"
    if (dataKyc.isVerified == DataKyc.Verify.YES.name) {
      session?.fpTag?.let { it1 -> WebEngageController.trackEvent("KYC VERIFICATION", "KYC verified", it1) }
      startFragmentPaymentActivity(FragmentType.KYC_DETAIL_NEW, Bundle().apply { putSerializable(IntentConstant.KYC_DETAIL.name, dataKyc) })
      baseActivity.finish()
    } else binding?.mainView?.visible()
  }

  private fun getQuery(): String? {
    val json = JSONObject()
    json.put("fpTag", session?.fpTag)
    return json.toString()
  }

  private fun updateUiData() {
    binding?.mainView?.visible()
    saveKycData?.actionData?.panCardDocument?.let { activity?.glideLoad(binding?.ivPanCardImage!!, it, R.drawable.placeholder_image) }
    binding?.tvPanNumber?.text = saveKycData?.actionData?.panNumber
    binding?.tvPanName?.text = saveKycData?.actionData?.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${saveKycData?.actionData?.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text = "${saveKycData?.actionData?.nameOfBank} - ${saveKycData?.actionData?.bankBranchName}"
  }

  private fun initLottieAnimation() {
    binding?.lottieAnimation?.setAnimation(R.raw.business_kyc_verification_gif)
    binding?.lottieAnimation?.repeatCount = LottieDrawable.INFINITE
    startCheckAnimation()
  }

  private fun startCheckAnimation() {
    binding?.lottieAnimation?.let {
      if (it.isAnimating) it.pauseAnimation()
      else it.playAnimation()
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnKycStatusRefresh -> showLongToast("Refreshing status...")
    }
  }

  override fun getViewModelClass(): Class<WebBoostKitViewModel> {
    return WebBoostKitViewModel::class.java
  }

  fun onNavPressed() {
    if (saveKycData != null) {
      val intent = Intent(baseActivity, Class.forName("com.nowfloats.NavigationDrawer.HomeActivity"))
      baseActivity.startActivity(intent)
    }
    baseActivity.finish()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_edit, menu)
    menuEdit = menu.findItem(R.id.menu_edit)
    menuEdit?.isVisible = false
    super.onCreateOptionsMenu(menu, inflater)
  }


  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.menu_edit -> {
        val bundle = Bundle()
        bundle.putBoolean(IntentConstant.IS_EDIT.name, true)
        bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
        startFragmentPaymentActivity(FragmentType.KYC_DETAILS, bundle, isResult = true)
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 101) {
      val isEdit = data?.getBooleanExtra(IntentConstant.IS_EDIT.name, false)
      if (isEdit == true) getKycDetails()
    }
  }
}