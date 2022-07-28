package com.boost.upgrades.ui.confirmation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.presignin.helper.ProcessFPDetails
import com.boost.presignin.model.fpdetail.UserFpDetailsResponse

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.ui.popup.NeedHelpPopUpFragment
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.upgrades.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.extensions.observeOnce
import com.framework.pref.UserSessionManager
import com.framework.pref.clientId
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.order_confirmation_fragment.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class OrderConfirmationFragment : BaseFragment("MarketPlaceOrderConfirmationFragment") {

  lateinit var prefs: SharedPrefs
  var data = JSONObject()

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
    prefs = SharedPrefs(activity as UpgradeActivity)
    return inflater.inflate(R.layout.order_confirmation_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)
    viewModel.emptyCurrentCart((activity as UpgradeActivity).application);

    //clear CartRelatedInfo
    prefs.storeOrderSuccessFlag(true)
    prefs.storeCartOrderInfo(null)
    prefs.storeApplyedCouponDetails(null)

    updateFPDetails()

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
      val pluralFeaturesText = if (prefs.getFeaturesCountInLastOrder() == 1) "feature" else "features"
      val yearOrMonthTemp = yearlyOrMonthlyOrEmptyValidity("", requireActivity(),
        if(prefs.getCartValidityMonths()!=null && prefs.getCartValidityMonths()!!.toInt() > 1)
          prefs.getCartValidityMonths()!!.toInt() else 1 )
      order_details_feature_count.text =
        "You have ordered " + prefs.getFeaturesCountInLastOrder() + " $pluralFeaturesText for ₹" + prefs.getLatestPurchaseOrderTotalPrice() + yearOrMonthTemp
      paymentBanner.text = "Order #" + prefs.getLatestPurchaseOrderId()
      val date = Calendar.getInstance().time
      val formatter = SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm aaa")
      order_id_details.text = "Order placed on " + formatter.format(date) +
          "\nOrder ID #" + prefs.getLatestPurchaseOrderId() + "\nTransaction ID #" + prefs.getTransactionIdFromCart()
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.e("Error", e.message ?: "")
    }

    back_button.setOnClickListener {
      (activity as UpgradeActivity).goToHomeFragment()
    }

    check_activation_status.setOnClickListener {
      WebEngageController.trackEvent(
        ADDONS_MARKETPLACE_CHECK_ACTIVATION_STATUS_CLICKED,
        Check_Activation_Status,
        NO_EVENT_VALUE
      )
      (activity as UpgradeActivity).goBackToMyAddonsScreen()
    }

    order_needs_help.setOnClickListener {
//      Toasty.info(requireContext(), getString(R.string.in_case_of_any_concerns)).show()
      val needHelpPopUpFragment = NeedHelpPopUpFragment()
      needHelpPopUpFragment.show(requireActivity().supportFragmentManager, "NEED_HELP_POPUP")
    }



  }

  private fun updateFPDetails() {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    viewModel?.getFpDetails((activity as UpgradeActivity).fpid!! ?: "", map)?.observeOnce(viewLifecycleOwner, {
      val response = it as? UserFpDetailsResponse
      if (it.isSuccess() && response != null) {
        ProcessFPDetails(UserSessionManager(requireActivity())).storeFPDetails(response)
      }
    })
  }


}
