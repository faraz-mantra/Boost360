package com.boost.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.boost.dbcenterapi.data.api_model.Razorpay.PaymentErrorModule
import com.boost.payment.base_class.BaseFragment
import com.boost.payment.ui.confirmation.FailedTransactionFragment
import com.boost.payment.ui.confirmation.OrderConfirmationFragment
import com.boost.payment.ui.payment.PaymentFragment
import com.boost.payment.utils.Constants
import com.boost.payment.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.payment.utils.SharedPrefs
import com.boost.payment.utils.Utils
import com.boost.payment.utils.WebEngageController
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.framework.webengageconstant.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
//import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import org.json.JSONObject
import java.lang.IllegalStateException

class PaymentActivity : AppCompatActivity(), PaymentResultWithDataListener {

    lateinit var prefs: SharedPrefs

//    lateinit var razorpay: Razorpay
    lateinit var razorpay: Checkout
    var fpid: String? = null
    var fpName: String? = null
    var customerId: String = ""
    var order_id: String = ""
    var transaction_id: String = ""
    var email: String = ""
    var contact: String = ""
    var couponTitle: String = ""
    var couponAmount: Double = 0.0
    var amount: Double = 0.0
    var netPrice: Double = 0.0
    var months:Int = -1
     var items : Int = 0
    var appState:String = ""
    var razorPayId:String = ""
    var paymentFailure:String? = null
    var paymentData = JSONObject()

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        fpid = intent.getStringExtra("fpid")
        fpName = intent.getStringExtra("fpName")
        order_id = intent.getStringExtra("order_id")!!
        transaction_id = intent.getStringExtra("transaction_id")!!
        email = intent.getStringExtra("email")!!.replace(" ","")
        contact = intent.getStringExtra("contact")!!
        amount = intent.getDoubleExtra("amount",0.0)
        netPrice = intent.getDoubleExtra("netPrice",0.0)
        if(intent.hasExtra("couponTitle")) {
            couponTitle = intent.getStringExtra("couponTitle")!!
            couponAmount = intent.getDoubleExtra("couponAmount",0.0)
        }
        months = intent.getIntExtra("months",0)
        items = intent.getIntExtra("cartItems",0)!!

        prefs = SharedPrefs(this)


        initView()
        initRazorPay()
    }

    private fun initView() {

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
            if (currentFragment != null) {
                val tag = currentFragment.tag
                Log.e("Add tag", ">>>$tag")
                tellFragments()
            } else {
                Log.e("Add tag", ">>> Finish PaymentActivity")
                finish()
            }
        }

        val paymentFragment = PaymentFragment.newInstance()
        val args = Bundle()
        args.putString("customerId", customerId)
        args.putDouble("amount", amount)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
        args.putDouble("netPrice", netPrice)
        args.putString("couponTitle", couponTitle)
        args.putDouble("couponAmount", couponAmount)
        args.putString("order_id", order_id)
        args.putString("transaction_id", transaction_id)
        args.putString("email", email)
        args.putString("currency", "INR")
        args.putString("contact", contact)
        args.putInt("monthValue",months)
        paymentFragment.arguments = args
        addFragment(
            paymentFragment,
            Constants.PAYMENT_FRAGMENT
        )
    }

    private fun tellFragments() {
        val fragments =
            supportFragmentManager.fragments
        for (f in fragments) {
            if (f != null && f is BaseFragment)
                f.onBackPressed()
        }
    }

    private fun initRazorPay() {
        try {
//            razorpay = Razorpay(this, RAZORPAY_KEY)
            Checkout.preload(applicationContext)
            val co = Checkout()
            co.setKeyID(RAZORPAY_KEY)
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    fun getRazorpayObject(): Checkout {
        return razorpay
    }

    fun setPayData(data: JSONObject) {
        this.paymentData = data
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken()?:""
    }

//    fun goToHomeFragment() {
//        val viewAllFragment = fragmentManager!!.findFragmentByTag(VIEW_ALL_FEATURE)
//        val detailsFragment = fragmentManager!!.findFragmentByTag(DETAILS_FRAGMENT)
//        if (viewAllFragment != null) {
//            fragmentManager!!.popBackStack(VIEW_ALL_FEATURE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        } else if (detailsFragment != null) {
//            fragmentManager!!.popBackStack(DETAILS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        } else {
//            fragmentManager!!.popBackStack(CART_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        razorpay.onActivityResult(requestCode, resultCode, data)
    }

    fun goBackToMyAddonsScreen() {
//        goToHomeFragment()
//        val args = Bundle()
//        args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
//        addFragmentHome(MyAddonsFragment.newInstance(), MYADDONS_FRAGMENT, args)
        Toast.makeText(this, "Redirect to MyAddonsFragment...", Toast.LENGTH_SHORT).show()
    }

    private var currentFragment: Fragment? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null

    fun addFragment(fragment: Fragment, fragmentTag: String?) {
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(R.id.ao_fragment_container, fragment, fragmentTag)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction!!.addToBackStack(fragmentTag)
        fragmentTransaction!!.commit()
    }

    fun addFragmentHome(fragment: Fragment, fragmentTag: String?, args: Bundle?) {
        fragment.setArguments(args)
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(R.id.ao_fragment_container, fragment, fragmentTag)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction!!.addToBackStack(fragmentTag)
        fragmentTransaction!!.commit()
    }

    fun replaceFragment(fragment: Fragment, fragmentTag: String?) {
//        popFragmentFromBackStack()
//        addFragment(fragment, fragmentTag)
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.ao_fragment_container, fragment, fragmentTag)
        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction!!.commit()
    }

    fun popFragmentFromBackStack() {
        try {
            fragmentManager!!.popBackStack()
        } catch (e: IllegalStateException){
            SentryController.captureException(e)
            //ignore
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        // Razorpay payment ID is passed here after a successful payment
        razorPayId = p1!!.paymentId
        Log.i("onPaymentSuccess", razorPayId)
        if(!appState.equals("onPause")){
            onPaymentSuccessfull()
        }
    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {
        // Error code and description is passed here
        paymentFailure = p1
        Log.e("onPaymentFailure",p1.toString())
        if(!appState.equals("onPause")){
            onPaymentFailure()
        }
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

    override fun onPause() {
        super.onPause()
        appState = "onPause"
    }


    fun onPaymentSuccessfull(){

        val revenue = amount.toInt()

        val event_attributes: HashMap<String, Any> = HashMap()
        event_attributes.put("revenue",(revenue / 100))
        event_attributes.put("rev",(revenue / 100))
        event_attributes.put("cartIds", Utils.filterBraces(prefs.getCardIds().toString()))
        event_attributes.put("couponIds", Utils.filterQuotes(prefs.getCouponIds().toString()))
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


        redirectOrderConfirmation(razorPayId)
    }

    fun onPaymentFailure(){
        try {
            Log.e("onPaymentError", "p1 >>>" + paymentFailure)

            val rev = amount.toInt()
            val eventAttributes: HashMap<String,Any> = HashMap()

            eventAttributes.put("revenue",(rev / 100))
            eventAttributes.put("rev",(rev / 100))
            eventAttributes.put("cartIds", Utils.filterBraces(prefs.getCardIds().toString()))
            eventAttributes.put("couponIds", Utils.filterQuotes(prefs.getCouponIds().toString()))
            eventAttributes.put("validity",prefs.getValidityMonths().toString())

            WebEngageController.trackEvent(ADDONS_MARKETPLACE_FAILED_PAYMENT_TRANSACTION_LOAD, ADDONS_MARKETPLACE, NO_EVENT_VALUE)
            val listPersonType = object : TypeToken<PaymentErrorModule>() {}.type
            val errorBody: PaymentErrorModule = Gson().fromJson(paymentFailure, listPersonType)
            Toasty.error(this, errorBody.error.description, Toast.LENGTH_LONG).show()
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_PAYMENT_FAILED, NO_EVENT_LABLE, eventAttributes)
            redirectTransactionFailure(paymentData.toString())
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
        catch (e: JsonSyntaxException){
            Log.e("onPayError",paymentFailure.toString())
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }


    fun redirectOrderConfirmation(paymentTransactionId: String) {
        var prefs = SharedPrefs(this)
        prefs.storeLatestOrderStatus(1)
        prefs.storeCardIds(null)
        prefs.storeCouponIds(null)
        //RAZORPAY payment ID IS NOT BEEN USED  ---> renamed to prefs.storeTransactionIdFromCart()
//        prefs.storeLatestPaymentIdFromPG(paymentTransactionId)

        replaceFragment(
            OrderConfirmationFragment.newInstance(),
            Constants.ORDER_CONFIRMATION_FRAGMENT
        )
    }

    fun redirectTransactionFailure(data: String){
        val failedTransactionFragment = FailedTransactionFragment()
        val args = Bundle()
        args.putString("data", data)
        failedTransactionFragment.arguments = args
//        failedTransactionPopUpFragment.show((activity as PaymentActivity).supportFragmentManager,
//            Constants.FAILED_TRANSACTION_FRAGMENT
//        )
        replaceFragment(
            failedTransactionFragment,
            Constants.FAILED_TRANSACTION_FRAGMENT
        )
    }
}