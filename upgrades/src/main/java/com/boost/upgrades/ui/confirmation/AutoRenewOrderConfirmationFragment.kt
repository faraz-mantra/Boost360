package com.boost.upgrades.ui.confirmation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.webengageconstant.*
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.auto_renew_order_confirmation_fragment.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AutoRenewOrderConfirmationFragment : BaseFragment() {

  lateinit var prefs: SharedPrefs

  companion object {
    fun newInstance() = AutoRenewOrderConfirmationFragment()
  }

  private lateinit var viewModel: OrderConfirmationViewModel

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    prefs = SharedPrefs(activity as UpgradeActivity)
    return inflater.inflate(R.layout.auto_renew_order_confirmation_fragment, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)
    viewModel.emptyCurrentCart((activity as UpgradeActivity).application);

    //clear CartRelatedInfo
    prefs.storeOrderSuccessFlag(true)
    prefs.storeCartOrderInfo(null)
    prefs.storeApplyedCouponDetails(null)
    prefs.storeMonthsValidity(0)

    try {
      if (arguments != null) {
        if (requireArguments().containsKey("payment_type") && requireArguments().getString("payment_type")
            .equals("External_Link")
        ) {
//                    external_link_payment_status.visibility = View.VISIBLE
        } else {
//                    external_link_payment_status.visibility = View.GONE
        }
      }
      external_link_payment_status.text =
        "Your have ordered " + prefs.getFeaturesCountInLastOrder() + " features for ₹" + prefs.getLatestPurchaseOrderTotalPrice() + "/month."
//            paymentBanner.setText("Order #"+prefs.getLatestPurchaseOrderId())
      val date = Calendar.getInstance().time
      val formatter = SimpleDateFormat("EEE, MMM d, yyyy 'at' hh:mm aaa")
      val calue = getNextRenewalDate()
      val strRenewFormat = resources.getString(R.string.auto_next_renewal_date, calue)
      auto_renew_details.text = strRenewFormat
      order_details_feature_count.text = "Order placed on " + formatter.format(date) +
          "\nTransaction ID #" + prefs.getTransactionIdFromCart() //"\nOrder ID #" + prefs.getLatestPurchaseOrderId() +
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
      Toasty.info(
        requireContext(),
        "In case of any concerns, you can write to ria@nowfloats.com. Boost care team is available during business hours."
      ).show()
    }

    WebEngageController.trackEvent(
      ADDONS_MARKETPLACE_ORDER_CONFIRMATION_LOADED,
      Auto_Renew_Order_Confirmation,
      NO_EVENT_VALUE
    )

  }

  fun getNextRenewalDate(): String {
    val c = Calendar.getInstance().time

    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = df.format(c)  // Today's date
//        val dt = "2012-01-04"

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val todayDate: Calendar = Calendar.getInstance()
    try {
      todayDate.time = sdf.parse(formattedDate)
    } catch (e: ParseException) {
      e.printStackTrace()
      SentryController.captureException(e)
    }
    todayDate.add(
      Calendar.DATE,
      365
    ) // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

    val sdf1 = SimpleDateFormat("dd")
    val nextDate: String = sdf1.format(todayDate.time)
    val sdf2 = SimpleDateFormat("MMMM yyyy")
    val nextDate2: String = sdf2.format(todayDate.time)
    Log.v("SimpleDateFormat", " $nextDate")
    Log.v("SimpleDateFormat", " $nextDate2")
    return nextDate + "th " + nextDate2
  }

}
