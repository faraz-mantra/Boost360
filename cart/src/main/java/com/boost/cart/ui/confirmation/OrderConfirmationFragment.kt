package com.boost.cart.ui.confirmation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.base_class.BaseFragment
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.Utils
import com.boost.cart.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.order_confirmation_fragment.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class OrderConfirmationFragment : BaseFragment() {

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
    prefs = SharedPrefs(activity as CartActivity)
    return inflater.inflate(R.layout.order_confirmation_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)
    viewModel.emptyCurrentCart((activity as CartActivity).application);

    //clear CartRelatedInfo
    prefs.storeOrderSuccessFlag(true)
    prefs.storeCartOrderInfo(null)
    prefs.storeApplyedCouponDetails(null)

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
      order_details_feature_count.text =
        "Your have ordered " + prefs.getFeaturesCountInLastOrder() + " features for ₹" + prefs.getLatestPurchaseOrderTotalPrice() + "/month."
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
      (activity as CartActivity).goToHomeFragment()
    }

    check_activation_status.setOnClickListener {
      WebEngageController.trackEvent(
        ADDONS_MARKETPLACE_CHECK_ACTIVATION_STATUS_CLICKED,
        Check_Activation_Status,
        NO_EVENT_VALUE
      )
      (activity as CartActivity).goBackToMyAddonsScreen()
    }

    order_needs_help.setOnClickListener {
      Toasty.info(
        requireContext(),
        "In case of any concerns, you can write to ria@nowfloats.com. Boost care Team is available during business hours."
      ).show()
    }



  }

}
