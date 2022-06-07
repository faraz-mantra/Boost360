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
import com.appservice.extension.fadeIn
import com.appservice.model.SessionData
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.model.kycData.saveBusinessKycDetail
import com.appservice.model.paymentKyc.PaymentKycRequest
import com.appservice.utils.WebEngageController
import com.appservice.utils.changeColorOfSubstring
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.webengageconstant.KYC_VERIFICATION
import com.framework.webengageconstant.KYC_VERIFIED
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

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
    setupUIColor()
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData
    saveKycData = arguments?.getSerializable(IntentConstant.KYC_DETAIL.name) as? PaymentKycRequest
    if (saveKycData == null) getKycDetails()
    else updateUiData()
    initLottieAnimation()
  }

  private fun setupUIColor() {
    changeColorOfSubstring(R.string.name_as_mentioned_on_pan_card, R.color.colorAccent, "*", binding?.headingPanName!!)
  }

  private fun getKycDetails() {
    showProgress()
    viewModel?.getKycData(session?.auth_1, getQuery())?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val resp = it as? PaymentKycDataResponse
        if (it.isSuccess()) {
          if (resp?.data.isNullOrEmpty().not()) {
            resp?.data?.first()?.saveBusinessKycDetail()
            setData(resp?.data?.first()!!)
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
    dataKyc.panCardDocument?.let {
      activity?.glideLoad(
        binding?.ivPanCardImage!!,
        it,
        R.drawable.placeholder_image,
        isBlur = true
      )
    }
    binding?.tvPanNumber?.text = dataKyc.panNumber?.toUpperCase(Locale.ROOT)
    binding?.tvPanName?.text = dataKyc.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${dataKyc.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text = "${dataKyc.nameOfBank} - ${dataKyc.bankBranchName}"
    if (dataKyc.isVerified == DataKyc.Verify.YES.name) {
      session?.fpTag?.let { it1 ->
        WebEngageController.trackEvent(
          KYC_VERIFICATION,
          KYC_VERIFIED,
          it1
        )
      }
      startFragmentPaymentActivity(
        FragmentType.KYC_DETAIL_NEW,
        Bundle().apply { putSerializable(IntentConstant.KYC_DETAIL.name, dataKyc) })
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
    saveKycData?.actionData?.panCardDocument?.let {
      activity?.glideLoad(
        binding?.ivPanCardImage!!,
        it,
        R.drawable.placeholder_image
      )
    }
    binding?.tvPanNumber?.text = saveKycData?.actionData?.panNumber
    binding?.tvPanName?.text = saveKycData?.actionData?.nameOfPanHolder
    binding?.tvBankAccNumber?.text = "A/C No. ${saveKycData?.actionData?.bankAccountNumber}"
    binding?.tvBankBranchDetails?.text =
      "${saveKycData?.actionData?.nameOfBank} - ${saveKycData?.actionData?.bankBranchName}"
  }

  private fun initLottieAnimation() {
    binding?.lottieAnimation?.apply {
      setAnimation(R.raw.business_kyc_verification_gif)
      repeatCount = LottieDrawable.INFINITE
      if (isAnimating) pauseAnimation() else playAnimation()
    }
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.btnKycStatusRefresh -> {
        binding?.txtVerification?.post {
          initLottieAnimationRefresh()
          binding?.mainView?.gone()
          binding?.refreshView?.visible()
          binding?.txtVerification?.alpha = 0F
          binding?.txtDescKyc?.alpha = 0F
          binding?.btnKycStatusRefresh?.alpha = 0F
          Timer().schedule(6000) {
            baseActivity.runOnUiThread {
              binding?.lottieAnimationRefresh?.pauseAnimation()
              binding?.mainView?.visible()
              binding?.refreshView?.gone()
              (binding?.txtVerification?.fadeIn())?.andThen(binding?.txtDescKyc?.fadeIn(400L))
                ?.andThen(binding?.btnKycStatusRefresh?.fadeIn(400L))?.subscribe()
            }
          }
        }
      }
    }
  }


  private fun initLottieAnimationRefresh() {
    binding?.lottieAnimationRefresh?.apply {
      setAnimation(R.raw.verificatioan_status_loader)
      repeatCount = LottieDrawable.INFINITE
      if (isAnimating) pauseAnimation() else playAnimation()
    }
  }

  override fun getViewModelClass(): Class<WebBoostKitViewModel> {
    return WebBoostKitViewModel::class.java
  }

  fun onNavPressed() {
    if (saveKycData != null) {
      try {
        val intent =
          Intent(baseActivity, Class.forName("com.dashboard.controller.DashboardActivity"))
        baseActivity.startActivity(intent)
      } catch (e: Exception) {
        baseActivity.finish()
      }
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