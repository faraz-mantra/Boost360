package com.boost.presignin.ui.registration

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentRegistrationSuccessBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.activatepurchase.ConsumptionConstraint
import com.boost.presignin.model.activatepurchase.PurchasedExpiry
import com.boost.presignin.model.activatepurchase.PurchasedWidget
import com.boost.presignin.model.authToken.AccessTokenResponse
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.authToken.saveAccessTokenAuth
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.WebPreviewActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.extensions.observeOnce
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences.GET_FP_DETAILS_TAG
import com.framework.pref.Key_Preferences.GET_FP_EXPERIENCE_CODE
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.ACTIVATE_FREE_PURCHASE_PLAN
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.SIGNUP_SUCCESS
import com.onboarding.nowfloats.model.plan.Plan15DaysResponseItem
import java.util.ArrayList

private const val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.

private var mBackPressed: Long = 0

class RegistrationSuccessFragment : AppBaseFragment<FragmentRegistrationSuccessBinding, LoginSignUpViewModel>() {

  private var floatsRequest: CategoryFloatsRequest? = null
  private var authToken: AuthTokenDataItem? = null
  private var session: UserSessionManager? = null

  companion object {
    const val CATEGORY_REQUEST = "CATEGORY_REQUEST"
    const val AUTH_TOKEN = "AUTH_TOKEN"

    @JvmStatic
    fun newInstance(registerRequest: CategoryFloatsRequest, authToken: AuthTokenDataItem?) = RegistrationSuccessFragment().apply {
      arguments = Bundle().apply {
        putSerializable(CATEGORY_REQUEST, registerRequest)
        putSerializable(AUTH_TOKEN, authToken)
      }
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_registration_success
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    floatsRequest = arguments?.getSerializable(CATEGORY_REQUEST) as? CategoryFloatsRequest
    authToken = arguments?.getSerializable(AUTH_TOKEN) as? AuthTokenDataItem
    session = UserSessionManager(baseActivity)
    val businessName = floatsRequest?.businessName
    val name = floatsRequest?.requestProfile?.ProfileProperties?.userName
    val websiteUrl = floatsRequest?.webSiteUrl

    binding?.headingTv?.text = String.format(getString(R.string.congratulations_n_s), name)
    binding?.businessNameTv?.text = businessName;

    saveSessionData()
    val amountSpannableString = SpannableString(" $businessName ").apply {
      setSpan(ForegroundColorSpan(Color.rgb(0, 0, 0)), 0, length, 0)
      setSpan(StyleSpan(Typeface.BOLD), 0, length, 0)

    }


    val your = getString(R.string.you);
    binding?.subheading?.text = SpannableStringBuilder().apply {
      append(your)
      append(amountSpannableString)
      append(getString(R.string.registration_complete_subheading))
    }


    val underLineSpan = SpannableString(websiteUrl).apply {
      setSpan(UnderlineSpan(), 0, length, 0)
    }
    binding?.websiteTv?.text = SpannableStringBuilder().apply { append(underLineSpan) }

    binding?.lottieAnimation?.setAnimation(R.raw.lottie_anim_congratulation)
    binding?.lottieAnimation?.repeatCount = 0
    binding?.lottieAnimation?.playAnimation()

    binding?.previewAccountBt?.setOnClickListener {
      val bundle = Bundle()
      bundle.putSerializable("request", floatsRequest)
      navigator?.startActivity(WebPreviewActivity::class.java, bundle)
    }
    binding?.dashboardBt?.setOnClickListener { createAccessTokenAuth() }
    onBackPressed()
  }

  private fun createAccessTokenAuth() {
    showProgress()
    val request = AccessTokenRequest(authToken = authToken?.authenticationToken, clientId = clientId, fpId = authToken?.floatingPointId)
    viewModel?.createAccessToken(request)?.observeOnce(viewLifecycleOwner, {
      val result = it as? AccessTokenResponse
      if (it?.isSuccess() == true && result?.result != null) {
        this.session?.saveAccessTokenAuth(result.result!!)
        apiBusinessActivatePlan()
      } else {
        showLongToast(getString(R.string.access_token_create_error))
        hideProgress()
      }
    })
  }

  private fun apiBusinessActivatePlan() {
    val request = getRequestPurchasedOrder(authToken?.floatingPointId!!)
    viewModel?.postActivatePurchasedOrder(clientId, request)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        WebEngageController.trackEvent(ACTIVATE_FREE_PURCHASE_PLAN, SIGNUP_SUCCESS, NO_EVENT_VALUE)
      } else showLongToast(getString(R.string.unable_to_activate_business_plan))
      try {
        startService()
        val intent = Intent(baseActivity, Class.forName("com.nowfloats.PreSignUp.SplashScreen_Activity"))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        baseActivity.startActivity(intent)
        baseActivity.finish()
      } catch (e: ClassNotFoundException) {
        e.printStackTrace()
      }
      hideProgress()
    })
  }

  private fun getRequestPurchasedOrder(floatingPointId: String): ActivatePurchasedOrderRequest {
    val widList = ArrayList<PurchasedWidget>()
    floatsRequest?.categoryDataModel?.sections?.forEach {
      it.getWidList().forEach { key ->
        val widget = PurchasedWidget(widgetKey = key, name = it.title, quantity = 1, desc = it.desc, recurringPaymentFrequency = "MONTHLY",
            isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
            consumptionConstraint = ConsumptionConstraint("DAYS", 30), images = ArrayList(),
            expiry = PurchasedExpiry("YEARS", 10))
        widList.add(widget)
      }
    }

    viewModel?.getCategoriesPlan(baseActivity)?.observeOnce(viewLifecycleOwner, { res ->
      val response = res as? Plan15DaysResponseItem
      if (response?.isSuccess() == true) {
        response.widgetKeys?.forEach { key ->
          val widgetN = widList.find { it.widgetKey.equals(key) }
          if (widgetN != null) {
            widgetN.consumptionConstraint?.metricValue = 15
            widgetN.expiry?.key = "DAYS"
            widgetN.expiry?.value = 15
          } else {
            widList.add(
                PurchasedWidget(widgetKey = key, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
                    isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
                    consumptionConstraint = ConsumptionConstraint("DAYS", 15), images = ArrayList(),
                    expiry = PurchasedExpiry("DAYS", 15))
            )
          }
        }
        response.extraProperties?.forEach { keyValue ->
          val widgetN2 = widList.find { it.widgetKey.equals(keyValue.widget) }
          if (widgetN2 != null) {
            widgetN2.consumptionConstraint?.metricValue = 15
            widgetN2.expiry?.key = "DAYS"
            widgetN2.expiry?.value = keyValue.value
          } else {
            widList.add(
                PurchasedWidget(widgetKey = keyValue.widget, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
                    isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
                    consumptionConstraint = ConsumptionConstraint("DAYS", 15), images = ArrayList(),
                    expiry = PurchasedExpiry("DAYS", keyValue.value))
            )
          }
        }
      }
    })
    return ActivatePurchasedOrderRequest(clientId, floatingPointId, "EXTENSION", widList)
  }

  private fun startService() {
    baseActivity.startService(Intent(baseActivity, APIService::class.java))
  }

  private fun onBackPressed() {
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
          baseActivity.finishAffinity()
          baseActivity.finish()
        } else {
          showShortToast(getString(R.string.press_again_exit))
        }
        mBackPressed = System.currentTimeMillis();
      }
    })
  }

  private fun saveSessionData() {
    session?.storeFpTag(floatsRequest?.fpTag)
    session?.storeFPID(floatsRequest?.floatingPointId)
    session?.storeFPDetails(GET_FP_DETAILS_TAG, floatsRequest?.getWebSiteId())
    session?.storeFPDetails(GET_FP_EXPERIENCE_CODE, floatsRequest?.categoryDataModel?.experience_code)
    session?.userProfileId = floatsRequest?.requestProfile?.profileId
    session?.setAccountSave(true)
    session?.setUserLogin(true)
  }
}