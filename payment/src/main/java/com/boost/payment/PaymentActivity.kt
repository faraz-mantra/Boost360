package com.boost.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.boost.payment.base_class.BaseFragment
import com.boost.payment.ui.confirmation.OrderConfirmationFragment
import com.boost.payment.ui.payment.PaymentFragment
import com.boost.payment.utils.Constants
import com.boost.payment.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.payment.utils.SharedPrefs
import com.framework.analytics.SentryController
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import java.lang.IllegalStateException
import java.util.ArrayList

class PaymentActivity : AppCompatActivity() {

    private var session: UserSessionManager?=null
    lateinit var prefs: SharedPrefs
    var screenType: String = ""
    var buyItemKey: String? = ""
    lateinit var razorpay: Razorpay
    var fpid: String? = null
    var fpName: String? = null
    var customerId: String = ""
    var order_id: String = ""
    var transaction_id: String = ""
    var email: String = ""
    var contact: String = ""
    var amount: Double = 0.0

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        session = UserSessionManager(this)

        fpid = intent.getStringExtra("fpid")
        fpName = intent.getStringExtra("fpName")
        order_id = intent.getStringExtra("order_id")!!
        transaction_id = intent.getStringExtra("transaction_id")!!
        email = intent.getStringExtra("email")!!
        contact = intent.getStringExtra("contact")!!
        amount = intent.getDoubleExtra("amount",0.0)

        prefs = SharedPrefs(this)

        initView()
        initRazorPay()
    }

    private fun initView() {

        supportFragmentManager.addOnBackStackChangedListener {
            currentFragment =
                supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
            if (currentFragment != null) {
                val tag = currentFragment?.tag
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
        args.putString("order_id", order_id)
        args.putString("transaction_id", transaction_id)
        args.putString("email", email)
        args.putString("currency", "INR")
        args.putString("contact", contact)
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

    override fun onBackPressed() {
        //super.onBackPressed()
        goToHomeFragment()

    }
    fun goToHomeFragment() {
        if (currentFragment is OrderConfirmationFragment) {

            try {
                val originData =
                    intent.getBundleExtra(com.framework.constants.Constants.MARKET_PLACE_ORIGIN_NAV_DATA)
                if (originData != null) {
                    val intent = Intent(
                        this,
                        Class.forName(originData?.getString(com.framework.constants.Constants.MARKET_PLACE_ORIGIN_ACTIVITY))
                    )
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra(
                        com.framework.constants.Constants.MARKET_PLACE_ORIGIN_NAV_DATA,
                        originData
                    )
                    startActivity(intent)

                } else {
                    val intent = Intent(this, Class.forName("com.boost.upgrades.UpgradeActivity"))
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("isComingFromOrderConfirm", true)
                    intent.putExtra("expCode", session?.fP_AppExperienceCode)
                    intent.putExtra("fpName", session?.fPName)
                    intent.putExtra("fpid", session?.fPID)
                    intent.putExtra("fpTag", session?.fpTag)
                    intent.putExtra("screenType", screenType)
                    intent.putExtra(
                        "accountType",
                        session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)
                    )
                    intent.putExtra("boost_widget_key", "TESTIMONIALS")
                    intent.putExtra("feature_code", "TESTIMONIALS")

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
                    if (buyItemKey != null && buyItemKey!!.isNotEmpty()) intent.putExtra(
                        "buyItemKey",
                        buyItemKey
                    )
                    intent.putExtra("profileUrl", session?.fPLogo)
                    startActivity(intent)
                }

            } catch (e: Exception) {
                SentryController.captureException(e)
                e.printStackTrace()
            }
        }else{
            super.onBackPressed()
        }
    }

    private fun initRazorPay() {
        try {
            razorpay = Razorpay(this, RAZORPAY_KEY)
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    fun getRazorpayObject(): Razorpay {
        return razorpay
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
}