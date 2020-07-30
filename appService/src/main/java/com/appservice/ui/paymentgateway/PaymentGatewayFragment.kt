package com.appservice.ui.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentPaymentActiveBinding
import com.appservice.model.SessionData
import com.appservice.model.StatusKyc
import com.appservice.ui.bankaccount.WhyBottomSheet
import com.framework.models.BaseViewModel

class PaymentGatewayFragment : AppBaseFragment<FragmentPaymentActiveBinding, BaseViewModel>() {

  private var isInstaMojoAccount: Boolean = false
  private var session: SessionData? = null
  private var isPaymentGateway: Boolean = false

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
    val fpId = arguments?.getString(IntentConstant.FP_ID.name)
    val clientId = arguments?.getString(IntentConstant.CLIENT_ID.name)
    val fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    val userProfileId = arguments?.getString(IntentConstant.USER_PROFILE_ID.name)
    val expCode = arguments?.getString(IntentConstant.EXPERIENCE_CODE.name)
    val fpEmail = arguments?.getString(IntentConstant.FP_USER_EMAIL.name)
    val fpNumber = arguments?.getString(IntentConstant.FP_USER_NUMBER.name)
    val fpLogo = arguments?.getString(IntentConstant.FP_LOGO.name)

    if (fpId.isNullOrEmpty() || fpTag.isNullOrEmpty() || clientId.isNullOrEmpty()) return

    session = SessionData(fpId = fpId, clientId = clientId, fpTag = fpTag, userProfileId = userProfileId,
        experienceCode = expCode, fpEmail = fpEmail, fpNumber = fpNumber, fpLogo = fpLogo)
    isPaymentGateway = arguments?.getBoolean(IntentConstant.CUSTOM_PAYMENT_GATEWAY.name) ?: false
    changeUi(isPaymentGateway)
    setOnClickListener(binding?.paymentGatewayTermsToggle, binding?.activePaymentBottomButton, binding?.btnViewStore)
    radioButtonToggle()
  }

  private fun changeUi(isPaymentGateway: Boolean) {
    binding?.mainView?.setBackgroundColor(ContextCompat.getColor(baseActivity, if (isPaymentGateway) R.color.white else R.color.white_grey))
    binding?.viewBac?.setBackgroundColor(ContextCompat.getColor(baseActivity, if (isPaymentGateway) R.color.colorPrimary else R.color.color_primary))
    binding?.paymentGatewayActivation?.visibility = if (isPaymentGateway) View.VISIBLE else View.GONE
    binding?.addOnNotActive?.visibility = if (isPaymentGateway) View.GONE else View.VISIBLE
    if (isPaymentGateway) (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.colorPrimary, R.color.colorPrimaryDark)
    else (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.color_primary, R.color.color_primary_dark)
  }

  override fun getLayout(): Int {
    return R.layout.fragment_payment_active
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.paymentGatewayTermsToggle -> bottomSheetWhy()
      binding?.activePaymentBottomButton -> {
        val bundle = Bundle()
        bundle.putBoolean("isInstaMojoAccount", isInstaMojoAccount)
        bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
        startFragmentPaymentActivity(FragmentType.SCAN_PAN_CARD, bundle)
      }
      binding?.btnViewStore -> startStorePage()
    }
  }

  private fun bottomSheetWhy() {
    WhyPaymentBottomSheet().show(this@PaymentGatewayFragment.parentFragmentManager, WhyBottomSheet::class.java.name)
  }

  private fun radioButtonToggle() {
    binding?.rbNoInstamojoAccount?.isChecked = true
    binding?.paymentGatewayToggleGroup?.setOnCheckedChangeListener { _, id ->
      val radio = binding?.paymentGatewayToggleGroup?.findViewById<RadioButton>(id)
      if (radio?.id == binding?.rbHaveInstaMojoAccount?.id) {
        binding?.activePaymentBottomButton?.text = getString(R.string.get_started)
        binding?.tvKeepDocsReady?.visibility = View.GONE
        isInstaMojoAccount = true

      } else if (radio?.id == binding?.rbNoInstamojoAccount?.id) {
        binding?.activePaymentBottomButton?.text = getString(R.string.start_with_pan_card)
        binding?.tvKeepDocsReady?.visibility = View.VISIBLE
        isInstaMojoAccount = false
      }
    }
  }

  fun startStorePage() {
    try {
      val intent = Intent(baseActivity, Class.forName("com.boost.upgrades.UpgradeActivity"))
      intent.putExtra("expCode", session?.experienceCode)
      intent.putExtra("fpName", session?.fpTag)
      intent.putExtra("fpid", session?.fpId)
      intent.putExtra("loginid", session?.userProfileId)
      intent.putExtra("email", session?.fpEmail ?: "ria@nowfloats.com")
      intent.putExtra("mobileNo", session?.fpNumber ?: "9160004303")
      intent.putExtra("profileUrl", session?.fpLogo)
      intent.putExtra("buyItemKey", StatusKyc.CUSTOM_PAYMENTGATEWAY.name)
      baseActivity.startActivity(intent)
    } catch (e: Exception) {
      showLongToast("Unable to start upgrade activity.")
    }
  }
}