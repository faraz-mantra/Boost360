package com.boost.upgrades.ui.razorpay

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.ui.popup.FailedTransactionPopUpFragment
import com.boost.upgrades.utils.Constants
import com.razorpay.PaymentData
import com.razorpay.PaymentResultListener
import com.razorpay.PaymentResultWithDataListener
import com.razorpay.Razorpay
import kotlinx.android.synthetic.main.payment_fragment.*
import kotlinx.android.synthetic.main.razor_pay_web_view_fragment.*
import org.json.JSONObject

class RazorPayWebView : DialogFragment() {

    lateinit var root: View

    lateinit var razorpay: Razorpay

    val failedTransactionPopUpFragment = FailedTransactionPopUpFragment()

    var data = JSONObject()

    companion object {
        fun newInstance() = RazorPayWebView()
    }

    private lateinit var viewModel: PaymentViewModel

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.razor_pay_web_view_fragment, container, false)

        razorpay = (activity as UpgradeActivity).getRazorpayObject()

        val jsonString = arguments!!.getString("data")
        data = JSONObject(jsonString!!)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(PaymentViewModel::class.java)


        try {
            // Make webview visible before submitting payment details
            razorpay.setWebView(razorpay_webview);
            razorpay.submit(data, object : PaymentResultListener {

                override fun onPaymentSuccess(razorpayPaymentId: String) {
                    // Razorpay payment ID is passed here after a successful payment
                    Log.i("onPaymentSuccess", razorpayPaymentId)
                    redirectOrderConfirmation()
                    dialog!!.dismiss()
                }

                override fun onPaymentError(p0: Int, p1: String?) {
                    // Error code and description is passed here
                    Log.e("onPaymentError", "p1 >>>"+ p1)
                    redirectTransactionFailure()
                    dialog!!.dismiss()
                }

            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    fun redirectOrderConfirmation() {
//        (activity as UpgradeActivity).addFragment(
//            OrderConfirmationFragment.newInstance(),
//            Constants.ORDER_CONFIRMATION_FRAGMENT
//        )
        (activity as UpgradeActivity).replaceFragment(
            OrderConfirmationFragment.newInstance(),
            Constants.ORDER_CONFIRMATION_FRAGMENT
        )
    }

    fun redirectTransactionFailure(){
        failedTransactionPopUpFragment.show((activity as UpgradeActivity).supportFragmentManager,
            Constants.FAILED_TRANSACTION_FRAGMENT
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        razorpay.reset()
    }

}
