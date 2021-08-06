package com.appservice.ui.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentPaymentActiveBinding
import com.appservice.model.SessionData
import com.appservice.model.StatusKyc
import com.appservice.model.kycData.DataKyc
import com.appservice.model.kycData.PaymentKycDataResponse
import com.appservice.model.paymentKyc.update.KycSet
import com.appservice.model.paymentKyc.update.UpdateKycValue
import com.appservice.model.paymentKyc.update.UpdatePaymentKycRequest
import com.appservice.ui.bankaccount.WhyBottomSheet
import com.appservice.viewmodel.WebBoostKitViewModel
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.Key_Preferences
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class PaymentGatewayFragment : AppBaseFragment<FragmentPaymentActiveBinding, WebBoostKitViewModel>() {

  private var isInstaMojoAccount: Boolean = false
  private lateinit var session: SessionData
  private var dataKyc: DataKyc? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): PaymentGatewayFragment {
      val fragment = PaymentGatewayFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    session = arguments?.getSerializable(IntentConstant.SESSION_DATA.name) as? SessionData ?: return
    isInstaMojoAccount = arguments?.getBoolean("isInstaMojoAccount") ?: false
    setOnClickListener(binding?.paymentGatewayTermsToggle, binding?.activePaymentBottomButton, binding?.btnViewStore, binding?.btnViewDetails)
    radioButtonToggle()
    checkData()
  }

  private fun checkData() {
    binding?.viewBac?.setBackgroundColor(
      ContextCompat.getColor(baseActivity, if (session.isPaymentGateway) R.color.colorPrimary else R.color.color_primary)
    )
    if (session.isPaymentGateway) (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.colorPrimary, R.color.colorPrimaryDark)
    else (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.color_primary, R.color.color_primary_dark)

    if (session.isPaymentGateway && session.isSelfBrandedAdd) {
      checkInstaMojo()
    } else if (session.isPaymentGateway && session.isSelfBrandedAdd.not()) {
      binding?.mainView?.visible()
      binding?.selfBrandedKycAddView?.gone()
      binding?.paymentGatewayActivation?.visible()
      binding?.addOnNotActive?.gone()
    } else {
      binding?.mainView?.visible()
      binding?.selfBrandedKycAddView?.gone()
      binding?.paymentGatewayActivation?.gone()
      binding?.addOnNotActive?.visible()
    }
  }

  private fun changeUi(verified: String) {
    if (verified.toUpperCase(Locale.ROOT) == DataKyc.Verify.YES.name) binding?.verifyTxt?.text = resources.getString(R.string.business_kyc_verify)
    binding?.selfBrandedKycAddView?.visible()
    binding?.paymentGatewayActivation?.gone()
    binding?.addOnNotActive?.gone()
    initLottieAnimation()
  }

  private fun checkInstaMojo() {
    showProgress()
    viewModel?.getKycData(session.auth_1, getQuery())?.observeOnce(viewLifecycleOwner, Observer {
      if ((it.error is NoNetworkException).not()) {
        val resp = it as? PaymentKycDataResponse
        if (it.isSuccess() && resp?.data.isNullOrEmpty().not()) {
          dataKyc = resp?.data?.get(0)
          if ((dataKyc?.hasexisistinginstamojoaccount?.toUpperCase(Locale.ROOT) == DataKyc.HasInginstaMojo.YES.name ||
                dataKyc?.hasexisistinginstamojoaccount?.toUpperCase(Locale.ROOT) == DataKyc.HasInginstaMojo.NO.name).not()
          ) {
            binding?.selfBrandedKycAddView?.gone()
            binding?.paymentGatewayActivation?.visible()
            binding?.addOnNotActive?.gone()
            binding?.activePaymentBottomButton?.text = resources.getString(R.string.update_your_kyc)
          } else changeUi(dataKyc?.isVerified ?: "")
        } else changeUi("")
      } else changeUi("")
      binding?.mainView?.visible()
      hideProgress()
    })
  }

  private fun getQuery(): String? {
    val json = JSONObject()
    json.put("fpTag", session.fpTag)
    return json.toString()
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

  override fun getLayout(): Int {
    return R.layout.fragment_payment_active
  }

  override fun getViewModelClass(): Class<WebBoostKitViewModel> {
    return WebBoostKitViewModel::class.java
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.paymentGatewayTermsToggle -> bottomSheetWhy()
      binding?.activePaymentBottomButton -> {
        if (binding?.activePaymentBottomButton?.text == resources.getString(R.string.update_your_kyc) && dataKyc != null) {
          updateKycData()
        } else {
          val bundle = Bundle()
          bundle.putBoolean("isInstaMojoAccount", isInstaMojoAccount)
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          startFragmentPaymentActivity(FragmentType.SCAN_PAN_CARD, bundle)
        }
      }
      binding?.btnViewStore -> startStorePage()
      binding?.btnViewDetails -> {
        if (dataKyc?.isVerified != DataKyc.Verify.YES.name) {
          val bundle = Bundle()
          bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
          startFragmentPaymentActivity(FragmentType.KYC_STATUS, bundle)
        } else startDetailPage()
      }
    }
  }

  private fun updateKycData() {
    val updateRequest = getUpdateRequest(dataKyc)
    showProgress()
    viewModel?.updateKycData(session.auth_1, updateRequest)
      ?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if ((it.error is NoNetworkException).not()) {
          if (it.status == 200 || it.status == 201 || it.status == 202) {
            checkInstaMojo()
          } else showError(resources.getString(R.string.update_kyc_error))
        } else showError(resources.getString(R.string.internet_connection_not_available))
      })
  }

  private fun showError(errorTxt: String) {
    hideProgress()
    showLongToast(errorTxt)
  }

  private fun getUpdateRequest(request: DataKyc?): UpdatePaymentKycRequest {
    val requestUpdate = UpdatePaymentKycRequest(query = getQueryId(request?.id))
    val hasInstamojo =
      if (isInstaMojoAccount) DataKyc.HasInginstaMojo.YES.name else DataKyc.HasInginstaMojo.NO.name
    val kycSet = KycSet(
      additionalDocument = request?.additionalDocument,
      bankAccountNumber = request?.bankAccountNumber,
      bankAccountStatement = request?.bankAccountStatement,
      bankBranchName = request?.bankBranchName,
      hasexisistinginstamojoaccount = hasInstamojo,
      ifsc = request?.ifsc,
      instamojoEmail = request?.instamojoEmail,
      instamojoPassword = request?.instamojoPassword,
      isArchived = dataKyc?.isArchived,
      nameOfBank = request?.nameOfBank,
      nameOfBankAccountHolder = request?.nameOfBankAccountHolder,
      nameOfPanHolder = request?.nameOfPanHolder,
      panCardDocument = request?.panCardDocument,
      panNumber = request?.panNumber
    )
    val value = UpdateKycValue(set = kycSet)
    requestUpdate.setUpdateValueKyc(value)
    return requestUpdate
  }

  private fun getQueryId(id: String?): String? {
    val jsonObject = JSONObject()
    jsonObject.put("_id", id)
    return jsonObject.toString()
  }


  private fun startDetailPage() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
    startFragmentPaymentActivity(FragmentType.DETAIL_KYC_VIEW, bundle)
  }

  private fun bottomSheetWhy() {
    WhyPaymentBottomSheet().show(
      this@PaymentGatewayFragment.parentFragmentManager,
      WhyBottomSheet::class.java.name
    )
  }

  private fun radioButtonToggle() {
    binding?.rbNoInstamojoAccount?.isChecked = true
    binding?.paymentGatewayToggleGroup?.setOnCheckedChangeListener { _, id ->
      val radio = binding?.paymentGatewayToggleGroup?.findViewById<RadioButton>(id)
      if (radio?.id == binding?.rbHaveInstaMojoAccount?.id) {
        isInstaMojoAccount = true
      } else if (radio?.id == binding?.rbNoInstamojoAccount?.id) {
        isInstaMojoAccount = false
      }
    }
  }

  fun startStorePage() {
    try {
      showProgress("Loading. Please wait...")
      val intent = Intent(baseActivity, Class.forName("com.boost.upgrades.UpgradeActivity"))
      intent.putExtra("expCode", session.experienceCode)
      intent.putExtra("fpName", session.fpTag)
      intent.putExtra("fpid", session.fpId)
      intent.putExtra("fpTag", session.fpTag)
      intent.putExtra("accountType", sessionLocal.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
      intent.putStringArrayListExtra("userPurchsedWidgets", ArrayList(sessionLocal.getStoreWidgets() ?: ArrayList()))
      intent.putExtra("email", sessionLocal.userProfileEmail ?: "ria@nowfloats.com")
      intent.putExtra("mobileNo", sessionLocal.userPrimaryMobile?: "9160004303")
      intent.putExtra("profileUrl", session.fpLogo)
      intent.putExtra("buyItemKey", StatusKyc.CUSTOM_PAYMENTGATEWAY.name)
      baseActivity.startActivity(intent)
      Handler().postDelayed({ hideProgress() }, 1000)
    } catch (e: Exception) {
      showLongToast("Unable to start upgrade activity.")
    }
  }
}