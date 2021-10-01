package com.boost.upgrades.ui.razorpay

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.Razorpay.PaymentErrorModule
import com.boost.upgrades.ui.confirmation.OrderConfirmationFragment
import com.boost.upgrades.ui.payment.PaymentViewModel
import com.boost.upgrades.ui.popup.FailedTransactionPopUpFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
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
import java.lang.IllegalStateException
import kotlin.collections.HashMap


class RazorPayWebView : androidx.fragment.app.DialogFragment() {

    lateinit var root: View

    lateinit var razorpay: Razorpay

    val failedTransactionPopUpFragment = FailedTransactionPopUpFragment()

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

        razorpay = (activity as UpgradeActivity).getRazorpayObject()

        val jsonString = requireArguments().getString("data")
        data = JSONObject(jsonString!!)
        prefs = SharedPrefs(activity as UpgradeActivity)
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

        if(isOnSaveInstanceStateCalled && savedInstanceState == null) {

            try {
                // Make webview visible before submitting payment details
                razorpay.setWebView(razorpay_webview);
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


    fun redirectOrderConfirmation(paymentTransactionId: String) {
        var prefs = SharedPrefs(activity as UpgradeActivity)
        prefs.storeLatestOrderStatus(1)
        prefs.storeCardIds(null)
        prefs.storeCouponIds(null)
        prefs.storeValidityMonths(null)
        //RAZORPAY payment ID IS NOT BEEN USED  ---> renamed to prefs.storeTransactionIdFromCart()
//        prefs.storeLatestPaymentIdFromPG(paymentTransactionId)

        (activity as UpgradeActivity).replaceFragment(
            OrderConfirmationFragment.newInstance(),
            Constants.ORDER_CONFIRMATION_FRAGMENT
        )
    }

    fun redirectTransactionFailure(data: String){
        val args = Bundle()
        args.putString("data", data)
        failedTransactionPopUpFragment.arguments = args
        failedTransactionPopUpFragment.show((activity as UpgradeActivity).supportFragmentManager,
            Constants.FAILED_TRANSACTION_FRAGMENT
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        razorpay.reset()
    }

}
