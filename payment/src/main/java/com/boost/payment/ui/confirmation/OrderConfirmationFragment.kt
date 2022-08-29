package com.boost.payment.ui.confirmation

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.boost.payment.PaymentActivity
import com.boost.payment.R
import com.boost.payment.base_class.BaseFragment
import com.boost.payment.utils.SharedPrefs
import com.boost.payment.utils.Utils
import com.boost.payment.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.utils.InAppReviewUtils
import com.framework.webengageconstant.*
import kotlinx.android.synthetic.main.payment_success_v3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class OrderConfirmationFragment : BaseFragment() {

  val TAG = this::class.java.simpleName
  lateinit var prefs: SharedPrefs
  var data = JSONObject()
  var session:UserSessionManager? = null
  var screenType: String = ""
  var buyItemKey: String? = ""
  var default_validity_months = 1
  lateinit var progressDialog: ProgressDialog

  companion object {
    fun newInstance() = OrderConfirmationFragment()
  }

  private lateinit var viewModel: OrderConfirmationViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
//    if(requireArguments().getBoolean("payViaLink")){
//      try {
//        val jsonString = requireArguments().getString("data")
//        data = JSONObject(jsonString!!)
//        val revenue = data["amount"] as Int
//
//        val event_attributes: HashMap<String, Any> = HashMap()
//        event_attributes.put("revenue",(revenue / 100))
//        event_attributes.put("rev",(revenue / 100))
//        event_attributes.put("cartIds", Utils.filterBraces(prefs.getCardIds().toString()))
//        event_attributes.put("couponIds", Utils.filterQuotes(prefs.getCouponIds().toString()))
//        event_attributes.put("validity",prefs.getValidityMonths().toString())
//
//        WebEngageController.trackEvent(
//          EVENT_NAME_ADDONS_MARKETPLACE_ORDER_CONFIRM,
//          PAGE_VIEW,
//          event_attributes
//        )
//      } catch (e: Exception) {
//      }
//    }
    WebEngageController.trackEvent(
            ADDONS_MARKETPLACE_ORDER_CONFIRMATION_LOADED,
            Order_Confirmation,
            NO_EVENT_VALUE
    )
    session = UserSessionManager(activity as PaymentActivity)
    prefs = SharedPrefs(activity as PaymentActivity)
    return inflater.inflate(R.layout.payment_success_v3, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    initMvvm()
    progressDialog = ProgressDialog(requireContext())

    viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)
    viewModel.emptyCurrentCartWithDomainActivate((activity as PaymentActivity).application, requireActivity())

    //firsttimepurchase logic for app review
    if(prefs.getFirstTimePurchase()){
      InAppReviewUtils.showInAppReview(requireActivity(), InAppReviewUtils.Events.in_app_review_first_purchase)
      prefs.storeFirstTimePurchase(false)
    }


    try {
      if (arguments != null) {
        if (requireArguments().containsKey("payment_type") && requireArguments().getString("payment_type")
            .equals("External_Link")
        ) {
          external_link_payment_status.visibility = View.VISIBLE
        } else {
          external_link_payment_status.visibility = View.GONE
        }
      }
      val months = if(prefs.getValidityMonths().isNullOrEmpty()) 0 else prefs.getValidityMonths()!!.toInt()
      order_details_feature_count.text =
        prefs.getFeaturesCountInLastOrder().toString() //+ " features for ₹" + prefs.getLatestPurchaseOrderTotalPrice() +"/"+ if(months>1) months else "" + Utils.yearOrMonthText(months,requireActivity(), false)//"/month."
      order_details_feature_count1.text = "₹" + prefs.getLatestPurchaseOrderTotalPrice() // +"/"+ if(months>1) months else "" + Utils.yearOrMonthText(months,requireActivity(), false)//"/month."
      //  paymentBanner.text = "Order #" + prefs.getLatestPurchaseOrderId()
      val date = Calendar.getInstance().time
      val formatter = SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm aaa")
      order_id_details.text = //"Order placed on " + formatter.format(date) +
           prefs.getLatestPurchaseOrderId() // + "\nTransaction ID #" + prefs.getTransactionIdFromCart()
      order_id_details1.text = prefs.getTransactionIdFromCart()

      validity.text=prefs.getValidityMonths()+ prefs.getValidityMonths()?.toInt()
        ?.let { Utils.yearOrMonthText(it,requireActivity(),true) }

      val oneMonthFromNow = Calendar.getInstance()
      oneMonthFromNow.add(
        Calendar.MONTH,
        if (prefs.getYearPricing()) default_validity_months * 12 else default_validity_months
      )
      val nowFormat = SimpleDateFormat("dd MMM yy")
      nowFormat.setTimeZone(Calendar.getInstance().getTimeZone())
      val oneMonthFormat = SimpleDateFormat("dd MMM yy")
      oneMonthFormat.setTimeZone(oneMonthFromNow.getTimeZone())
      validity_period_value.setText(
        nowFormat.format(Calendar.getInstance().time) + " - " + nowFormat.format(
          oneMonthFromNow.time
        )
      )


      //clear CartRelatedInfo
      prefs.storeOrderSuccessFlag(true)
      prefs.storeCartOrderInfo(null)
      prefs.storeApplyedCouponDetails(null)
      prefs.storeValidityMonths(null)
      UserSessionManager(requireActivity()).storeIntDetails(Key_Preferences.KEY_FP_CART_COUNT,0)
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.e("Error", e.message ?: "")
    }

//    back_button.setOnClickListener {
//      goToHomeFragment()
//      (activity as PaymentActivity).finish()
//      Toast.makeText(requireContext(), "Redirect based on needs...", Toast.LENGTH_SHORT).show()
//    }

    check_activation_status.setOnClickListener {
      WebEngageController.trackEvent(
        ADDONS_MARKETPLACE_CHECK_ACTIVATION_STATUS_CLICKED,
        Check_Activation_Status,
        NO_EVENT_VALUE
      )
      goToAddOnsFragment()
      (activity as PaymentActivity).finish()
    }

//    order_needs_help.setOnClickListener {
//      Toasty.info(
//        requireContext(),
//        "In case of any concerns, you can write to ria@nowfloats.com. Boost care Team is available during business hours."
//      ).show()
//    }

  }

  private fun initMvvm() {
    viewModel.getLoaderStatus().observe(this, androidx.lifecycle.Observer {
      if(it.length>0){
        showProgress(it)
      }else{
        hideProgress()
      }
    })
  }

  private fun showProgress(message: String = "Please wait...") {
    try {
      if (!progressDialog.isShowing) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
      }
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

  private fun hideProgress() {
    try {
//      if (progressDialog.isShowing) progressDialog.hide()
      if (progressDialog.isShowing) progressDialog.cancel()
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

  override fun onResume() {
    super.onResume()
    Log.e(TAG, ">>> onResume")
  }

  override fun onPause() {
    super.onPause()
    Log.e(TAG, ">>> onPause")
  }

  override fun onStop() {
    super.onStop()
    Log.e(TAG, ">>> onStop")
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.e(TAG, ">>> onDestroy")
  }

  private fun goToHomeFragment() {
    try {
      val intent = Intent(context, Class.forName("com.boost.marketplace.ui.home.MarketPlaceActivity"))
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra("isComingFromOrderConfirm",true)
      intent.putExtra("expCode", session?.fP_AppExperienceCode)
      intent.putExtra("fpName", session?.fPName)
      intent.putExtra("fpid", session?.fPID)
      intent.putExtra("fpTag", session?.fpTag)
      intent.putExtra("screenType", screenType)
      intent.putExtra("accountType", session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
      intent.putExtra("boost_widget_key","TESTIMONIALS")
      intent.putExtra("feature_code","TESTIMONIALS")

      intent.putStringArrayListExtra(
        "userPurchsedWidgets",
        session?.getStoreWidgets() as ArrayList<String>
      )
      if (session?.userProfileEmail != null) {
        intent.putExtra("email", session?.userProfileEmail)
      } else {
        intent.putExtra("email", "ria@nowfloats.com")
      }
      if (session?.userPrimaryMobile != null) {
        intent.putExtra("mobileNo", session?.userPrimaryMobile)
      } else {
        intent.putExtra("mobileNo", "9160004303")
      }
      if (buyItemKey != null && buyItemKey!!.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
      intent.putExtra("profileUrl", session?.fPLogo)
      startActivity(intent)
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

  private fun goToAddOnsFragment() {
    try {
      val intent = Intent(context, Class.forName("com.boost.marketplace.ui.My_Plan.MyCurrentPlanActivity"))
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      intent.putExtra("isComingFromOrderConfirmActivation",true)
      intent.putExtra("expCode", session?.fP_AppExperienceCode)
      intent.putExtra("fpName", session?.fPName)
      intent.putExtra("fpid", session?.fPID)
      intent.putExtra("fpTag", session?.fpTag)
      intent.putExtra("screenType", screenType)
      intent.putExtra("accountType", session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
      intent.putExtra("boost_widget_key","TESTIMONIALS")
      intent.putExtra("feature_code","TESTIMONIALS")

      intent.putStringArrayListExtra(
        "userPurchsedWidgets",
        session?.getStoreWidgets() as ArrayList<String>
      )
      if (session?.userProfileEmail != null) {
        intent.putExtra("email", session?.userProfileEmail)
      } else {
        intent.putExtra("email", "ria@nowfloats.com")
      }
      if (session?.userPrimaryMobile != null) {
        intent.putExtra("mobileNo", session?.userPrimaryMobile)
      } else {
        intent.putExtra("mobileNo", "9160004303")
      }
      if (buyItemKey != null && buyItemKey!!.isNotEmpty()) intent.putExtra("buyItemKey", buyItemKey)
      intent.putExtra("profileUrl", session?.fPLogo)
      startActivity(intent)
      requireActivity().finish()
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }


}
