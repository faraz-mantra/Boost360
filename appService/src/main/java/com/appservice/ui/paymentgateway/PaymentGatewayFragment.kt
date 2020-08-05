package com.appservice.ui.paymentgateway

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieDrawable
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentPaymentActiveBinding
import com.appservice.model.SessionData
import com.appservice.model.StatusKyc
import com.appservice.ui.bankaccount.WhyBottomSheet
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel

class PaymentGatewayFragment : AppBaseFragment<FragmentPaymentActiveBinding, BaseViewModel>() {

  private var isInstaMojoAccount: Boolean = false
  private var session: SessionData? = null
  private var isPaymentGateway: Boolean = false
  private var isSelfBrandedAdd: Boolean = false

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
    isPaymentGateway = arguments?.getBoolean(IntentConstant.CUSTOM_PAYMENT_GATEWAY.name) ?: false
    isSelfBrandedAdd = arguments?.getBoolean(IntentConstant.IS_SELF_BRANDED_KYC_ADDED.name) ?: false
    setOnClickListener(binding?.paymentGatewayTermsToggle, binding?.activePaymentBottomButton, binding?.btnViewStore, binding?.selfBrandedKycAddView)
    radioButtonToggle()
    changeUi()
  }

  private fun changeUi() {
    binding?.viewBac?.setBackgroundColor(ContextCompat.getColor(baseActivity, if (isPaymentGateway) R.color.colorPrimary else R.color.color_primary))
    if (isPaymentGateway) (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.colorPrimary, R.color.colorPrimaryDark)
    else (baseActivity as? PaymentGatewayContainerActivity)?.changeTheme(R.color.color_primary, R.color.color_primary_dark)

    if (isPaymentGateway && isSelfBrandedAdd) {
      binding?.selfBrandedKycAddView?.visible()
      binding?.paymentGatewayActivation?.gone()
      binding?.addOnNotActive?.gone()
      initLottieAnimation()
    } else if (isPaymentGateway && isSelfBrandedAdd.not()) {
      binding?.selfBrandedKycAddView?.gone()
      binding?.paymentGatewayActivation?.visible()
      binding?.addOnNotActive?.gone()
    } else {
      binding?.selfBrandedKycAddView?.gone()
      binding?.paymentGatewayActivation?.gone()
      binding?.addOnNotActive?.visible()
    }

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
      binding?.selfBrandedKycAddView -> startDetailPage()
    }
  }

  private fun startDetailPage() {
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.SESSION_DATA.name, session)
    startFragmentPaymentActivity(FragmentType.DETAIL_KYC_VIEW, bundle)
  }

  private fun bottomSheetWhy() {
    WhyPaymentBottomSheet().show(this@PaymentGatewayFragment.parentFragmentManager, WhyBottomSheet::class.java.name)
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