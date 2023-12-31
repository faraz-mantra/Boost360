package com.boost.payment.ui.razorpay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.boost.dbcenterapi.data.api_model.Razorpay.PaymentErrorModule
import com.boost.payment.PaymentActivity
import com.boost.payment.R
import com.boost.payment.ui.confirmation.FailedTransactionFragment
import com.boost.payment.ui.confirmation.OrderConfirmationFragment
import com.boost.payment.ui.payment.PaymentViewModel
import com.boost.payment.utils.Constants
import com.boost.payment.utils.SharedPrefs
import com.boost.payment.utils.Utils
import com.boost.payment.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.webengageconstant.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.razorpay.PaymentResultListener
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.razor_pay_web_view_fragment.*
import org.json.JSONObject


class RazorPayWebView : DialogFragment() {

    lateinit var root: View

    lateinit var razorpay: Razorpay

    val failedTransactionFragment = FailedTransactionFragment()

    var data = JSONObject()
    lateinit var prefs: SharedPrefs
    var appState:String = ""
    private var isOnSaveInstanceStateCalled:Boolean = false
    var razorPayId:String = ""
    var paymentFailure:String? = null

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
        isOnSaveInstanceStateCalled = true

        razorpay = (activity as PaymentActivity).getRazorpayObject()

        val jsonString = requireArguments().getString("data")
        data = JSONObject(jsonString!!)
        prefs = SharedPrefs(activity as PaymentActivity)
//        val cartids = data["cartIds"] as LazyStringArrayList
//        cartids.forEach {
            Log.v("onPaymentSuccess", " cartids "+ data)
//        }
        return root
    }

    override fun onPause() {
        super.onPause()
        appState = "onPause"
    }

    override fun onResume() {
        super.onResume()
        if(appState.equals("onPause") && !razorPayId.isNullOrEmpty()){
            onPaymentSuccessfull()
        }else if(appState.equals("onPause") && !paymentFailure.isNullOrEmpty()){
            onPaymentFailure()
        }
        appState = "onResume"
    }
    fun onPaymentSuccessfull(){

        val revenue = data["amount"] as Int

        val event_attributes: HashMap<String, Any> = HashMap()
        event_attributes.put("revenue",(revenue / 100))
        event_attributes.put("rev",(revenue / 100))
        event_attributes.put("cartIds", Utils.filterBraces(prefs.getCardIds().toString()))
        event_attributes.put("couponIds",Utils.filterQuotes(prefs.getCouponIds().toString()))
        event_attributes.put("validity",prefs.getValidityMonths().toString())
        WebEngageController.trackEvent(ADDONS_MARKETPLACE_PAYMENT_SUCCESS, ADDONS_MARKETPLACE, event_attributes)

//                        WebEngageController.trackEvent("ADDONS_MARKETPLACE Payment Success",
//                                "rev", (revenue / 100).toString())

        var firebaseAnalytics = Firebase.analytics
        val bundle = Bundle()
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, (revenue / 100).toDouble())
        bundle.putString(FirebaseAnalytics.Param.TRANSACTION_ID, razorPayId)
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "INR")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)

        dialog!!.dismiss()
        redirectOrderConfirmation(razorPayId)
    }

    fun onPaymentFailure(){
        try {
            dialog!!.dismiss()
            Log.e("onPaymentError", "p1 >>>" + paymentFailure)

            val rev = data["amount"] as Int
            val eventAttributes: HashMap<String,Any> = HashMap()

            eventAttributes.put("revenue",(rev / 100))
            eventAttributes.put("rev",(rev / 100))
            eventAttributes.put("cartIds", Utils.filterBraces(prefs.getCardIds().toString()))
            eventAttributes.put("couponIds",Utils.filterQuotes(prefs.getCouponIds().toString()))
            eventAttributes.put("validity",prefs.getValidityMonths().toString())

            WebEngageController.trackEvent(ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOAD, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
            val listPersonType = object : TypeToken<PaymentErrorModule>() {}.type
            val errorBody: PaymentErrorModule = Gson().fromJson(paymentFailure, listPersonType)
            Toasty.error(requireContext(), errorBody.error.description, Toast.LENGTH_LONG).show()
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_PAYMENT_FAILED, NO_EVENT_LABLE, eventAttributes)
            redirectTransactionFailure(data.toString())
        } catch (e: Exception) {
            Log.e("onPayError",paymentFailure.toString())
            SentryController.captureException(e)
            e.printStackTrace()
        }
        catch (e:IllegalStateException){
            Log.e("onPayError",paymentFailure.toString())
            SentryController.captureException(e)
            e.printStackTrace()
        }
        catch (e:JsonSyntaxException){
            Log.e("onPayError",paymentFailure.toString())
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        appState = null.toString()
        paymentFailure = null

        if(isOnSaveInstanceStateCalled && savedInstanceState == null) {

            try {
                // Make webview visible before submitting payment details
                razorpay.setWebView(razorpay_webview)
                razorpay.submit(data, object : PaymentResultListener {

                    override fun onPaymentSuccess(razorpayPaymentId: String) {
                        // Razorpay payment ID is passed here after a successful payment
                        razorPayId = razorpayPaymentId
                        Log.i("onPaymentSuccess", razorpayPaymentId)
                        if(!appState.equals("onPause")){
                            onPaymentSuccessfull()
                        }
                    }

                    override fun onPaymentError(p0: Int, p1: String?) {
                        // Error code and description is passed here
                        paymentFailure = p1
                        Log.e("onPaymentFailure",p1.toString())
                        if(!appState.equals("onPause")){
                            onPaymentFailure()
                        }
                    }


                })
            } catch (e: Exception) {
                e.printStackTrace()
                SentryController.captureException(e)
            }

            WebEngageController.trackEvent(ADDONS_MARKETPLACE_RAZOR_PAY_VIEW_LOADED, RAZOR_PAY_VIEW, NO_EVENT_VALUE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) { //Razorpay.UPI_INTENT_REQUEST_CODE = 101
            razorpay.onActivityResult(requestCode, resultCode, data);
        }
    }


    fun redirectOrderConfirmation(paymentTransactionId: String) {
        var prefs = SharedPrefs(activity as PaymentActivity)
        prefs.storeLatestOrderStatus(1)
        prefs.storeCardIds(null)
        prefs.storeCouponIds(null)
        //RAZORPAY payment ID IS NOT BEEN USED  ---> renamed to prefs.storeTransactionIdFromCart()
//        prefs.storeLatestPaymentIdFromPG(paymentTransactionId)

        (activity as PaymentActivity).replaceFragment(
            OrderConfirmationFragment.newInstance(),
            Constants.ORDER_CONFIRMATION_FRAGMENT
        )
    }

    fun redirectTransactionFailure(data: String){
        val args = Bundle()
        args.putString("data", data)
        failedTransactionFragment.arguments = args
//        failedTransactionPopUpFragment.show((activity as PaymentActivity).supportFragmentManager,
//            Constants.FAILED_TRANSACTION_FRAGMENT
//        )
        (activity as PaymentActivity).replaceFragment(
            failedTransactionFragment,
            Constants.FAILED_TRANSACTION_FRAGMENT
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        razorpay.reset()
    }

}
