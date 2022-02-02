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
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.accessToken.AccessTokenRequest
import com.boost.presignin.model.activatepurchase.ActivatePurchasedOrderRequest
import com.boost.presignin.model.activatepurchase.ConsumptionConstraint
import com.boost.presignin.model.activatepurchase.PurchasedExpiry
import com.boost.presignin.model.activatepurchase.PurchasedWidget
import com.boost.presignin.model.authToken.AccessTokenResponse
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.authToken.getAuthTokenData
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.getCategoryRequest
import com.boost.presignin.model.plan.Plan15DaysResponse
import com.boost.presignin.service.APIService
import com.boost.presignin.ui.WebPreviewActivity
import com.boost.presignin.viewmodel.LoginSignUpViewModel
import com.framework.analytics.SentryController
import com.framework.analytics.UserExperiorController
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.pref.saveAccessTokenAuth
import com.framework.webengageconstant.*
import java.util.*
import kotlin.system.exitProcess

private const val TIME_INTERVAL = 2000 // # milliseconds, desired time passed between two back presses.

private var mBackPressed: Long = 0

class RegistrationSuccessFragment : AppBaseFragment<FragmentRegistrationSuccessBinding, LoginSignUpViewModel>() {

  private var floatsRequest: CategoryFloatsRequest? = null
  private var authToken: AuthTokenDataItem? = null
  private var session: UserSessionManager? = null

  companion object {
    @JvmStatic
    fun newInstance() = RegistrationSuccessFragment()
  }

  override fun getLayout(): Int {
    return R.layout.fragment_registration_success
  }

  override fun getViewModelClass(): Class<LoginSignUpViewModel> {
    return LoginSignUpViewModel::class.java
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_REGISTRATION_SUCCESS_PAGE_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    session = UserSessionManager(baseActivity)
    floatsRequest = session?.getCategoryRequest()
    authToken = session?.getAuthTokenData()
    if (floatsRequest != null || authToken != null) {
      val businessName = floatsRequest?.businessName
      val categoryName = floatsRequest?.categoryDataModel?.category_Name
      val name = floatsRequest?.requestProfile?.ProfileProperties?.userName
      val websiteUrl = floatsRequest?.webSiteUrl

      binding?.headingTv?.text = String.format(getString(R.string.congratulations_n_s), name)
      binding?.businessNameTv?.text = businessName;

      val amountSpannableString = SpannableString(" $categoryName ").apply {
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
        WebEngageController.trackEvent(PS_REGISTRATION_PREVIEW_CLICK, CLICK, NO_EVENT_VALUE)
        val bundle = Bundle()
        bundle.putSerializable("request", floatsRequest)
        navigator?.startActivity(WebPreviewActivity::class.java, bundle)
      }
      binding?.dashboardBt?.setOnClickListener { createAccessTokenAuth() }
      onBackPressed()
    } else logout()
  }

  private fun createAccessTokenAuth() {
    showProgress(getString(R.string.business_setup_process))
    WebEngageController.trackEvent(PS_REGISTRATION_DASHBOARD_CLICK, CLICK, NO_EVENT_VALUE)
    val request = AccessTokenRequest(
      authToken = authToken?.authenticationToken,
      clientId = clientId,
      fpId = authToken?.floatingPointId
    )
    viewModel?.createAccessToken(request)?.observeOnce(viewLifecycleOwner, {
      val result = it as? AccessTokenResponse
      if (it?.isSuccess() == true && result?.result != null) {
        this.session?.saveAccessTokenAuth(result.result!!)
        this.session?.setUserLogin(true)
        this.session?.setUserSignUpComplete(false)
        apiBusinessActivatePlan()
      } else {
        showLongToast(getString(R.string.access_token_create_error))
        hideProgress()
      }
    })
  }

  private fun apiBusinessActivatePlan() {
    viewModel?.getCategoriesPlan(baseActivity)?.observeOnce(viewLifecycleOwner, { res ->
      val responsePlan = res as? Plan15DaysResponse
      val request = getRequestPurchasedOrder(authToken?.floatingPointId!!, responsePlan)
      viewModel?.postActivatePurchasedOrder(clientId, request)?.observeOnce(viewLifecycleOwner, {
        if (it.isSuccess()) {
          WebEngageController.trackEvent(
            PS_ACTIVATE_FREE_PURCHASE_PLAN,
            SIGNUP_SUCCESS,
            NO_EVENT_VALUE
          )
        } else showLongToast(getString(R.string.unable_to_activate_business_plan))
        storeFpDetails()
      })
    })
  }

  private fun storeFpDetails() {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails(authToken?.floatingPointId ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        ProcessFPDetails(session!!).storeFPDetails(response)
        SentryController.setUser(UserSessionManager(baseActivity))
        UserExperiorController.setUserAttr(UserSessionManager(baseActivity))
        startService()
        startDashboard()
      } else {
        hideProgress()
        logout()
      }
    })
  }

  private fun startDashboard() {
    try {
      val dashboardIntent =
        Intent(baseActivity, Class.forName("com.dashboard.controller.DashboardActivity"))
      dashboardIntent.putExtras(requireActivity().intent)
      val bundle = Bundle()
      bundle.putParcelableArrayList("message", ArrayList())
      dashboardIntent.putExtras(bundle)
      dashboardIntent.flags =
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
      startActivity(dashboardIntent)
      baseActivity.finish()
      hideProgress()
    } catch (e: Exception) {
      SentryController.captureException(e)

      e.printStackTrace()
    }
  }

  private fun getRequestPurchasedOrder(floatingPointId: String, responsePlan: Plan15DaysResponse?): ActivatePurchasedOrderRequest {
    val widList = ArrayList<PurchasedWidget>()
    floatsRequest?.categoryDataModel?.sections?.forEach {
      it.getWidList().forEach { key ->
        val widget = PurchasedWidget(
          widgetKey = key, name = it.title, quantity = 1, desc = it.desc,
          recurringPaymentFrequency = "MONTHLY", isCancellable = true, isRecurringPayment = true,
          discount = 0.0, price = 0.0, netPrice = 0.0, consumptionConstraint = ConsumptionConstraint("DAYS", 30),
          images = ArrayList(), expiry = PurchasedExpiry("YEARS", 10)
        )
        widList.add(widget)
      }
    }
    if (responsePlan?.data != null) {
      responsePlan.data.widgetKeys?.forEach { key ->
        val widgetN = widList.find { it.widgetKey.equals(key) }
        if (widgetN != null) {
          widgetN.consumptionConstraint?.metricValue = 15
          widgetN.expiry?.key = "DAYS"
          widgetN.expiry?.value = 15
        } else {
          widList.add(
            PurchasedWidget(
              widgetKey = key, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
              isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0,
              netPrice = 0.0, consumptionConstraint = ConsumptionConstraint("DAYS", 15),
              images = ArrayList(), expiry = PurchasedExpiry("DAYS", 15)
            )
          )
        }
      }
      responsePlan.data.extraProperties?.forEach { keyValue ->
        val widgetN2 = widList.find { it.widgetKey.equals(keyValue.widget) }
        if (widgetN2 != null) {
          widgetN2.consumptionConstraint?.metricValue = 15
          widgetN2.expiry?.key = "DAYS"
          widgetN2.expiry?.value = keyValue.value
        } else {
          widList.add(
            PurchasedWidget(
              widgetKey = keyValue.widget, name = "", quantity = 1, desc = "", recurringPaymentFrequency = "MONTHLY",
              isCancellable = true, isRecurringPayment = true, discount = 0.0, price = 0.0, netPrice = 0.0,
              consumptionConstraint = ConsumptionConstraint("DAYS", 15), images = ArrayList(),
              expiry = PurchasedExpiry("DAYS", keyValue.value)
            )
          )
        }
      }
    }
    return ActivatePurchasedOrderRequest(clientId, floatingPointId, "EXTENSION", widList)
  }

  private fun startService() {
    baseActivity.startService(Intent(baseActivity, APIService::class.java))
  }

  private fun onBackPressed() {
    activity?.onBackPressedDispatcher?.addCallback(
      viewLifecycleOwner,
      object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
          if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            exitProcess(0)
          } else {
            showShortToast(getString(R.string.press_again_exit))
          }
          mBackPressed = System.currentTimeMillis();
        }
      })
  }
}