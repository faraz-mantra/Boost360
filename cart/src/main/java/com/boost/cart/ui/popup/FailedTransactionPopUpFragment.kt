package com.boost.cart.ui.popup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.cart.ui.razorpay.RazorPayWebView
import com.boost.cart.utils.Constants.Companion.RAZORPAY_WEBVIEW_POPUP_FRAGMENT
import com.boost.cart.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOADED
import com.framework.webengageconstant.FAILED_PAYMENT_TRANSACTION
import com.framework.webengageconstant.NO_EVENT_VALUE
import kotlinx.android.synthetic.main.failed_transaction_fragment.*

class FailedTransactionPopUpFragment : DialogFragment() {

  lateinit var root: View

  lateinit var razorPayWebView: RazorPayWebView

  var data: String? = null

  override fun onStart() {
    super.onStart()
    val width = ViewGroup.LayoutParams.MATCH_PARENT
    val height = ViewGroup.LayoutParams.MATCH_PARENT
    dialog!!.window!!.setLayout(width, height)
    dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.failed_transaction_fragment, container, false)
    razorPayWebView = RazorPayWebView.newInstance()
    data = arguments?.getString("data")
    return root

  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    failed_outer_layout.setOnClickListener {
      dialog!!.dismiss()
    }

    transaction_failed_layout.setOnClickListener {}

    check_activation_status.setOnClickListener {
      dialog!!.dismiss()
    }

    transaction_failed_retry.setOnClickListener {
      val args = Bundle()
      args.putString("data", data)
      razorPayWebView.arguments = args

      //RazorPay web
      razorPayWebView.show(
        (activity as CartActivity).supportFragmentManager,
        RAZORPAY_WEBVIEW_POPUP_FRAGMENT
      )

      //close the popup
      dialog!!.dismiss()
    }

    WebEngageController.trackEvent(
      ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOADED,
      FAILED_PAYMENT_TRANSACTION,
      NO_EVENT_VALUE
    )
  }

}
