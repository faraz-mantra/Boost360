package com.boost.cart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.boost.cart.base_class.BaseFragment
import com.boost.cart.interfaces.CompareBackListener
import com.boost.cart.ui.home.CartFragment
import com.boost.cart.ui.myaddons.MyAddonsFragment
import com.boost.cart.utils.Constants.Companion.CART_FRAGMENT
import com.boost.cart.utils.Constants.Companion.DETAILS_FRAGMENT
import com.boost.cart.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.cart.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.cart.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.cart.utils.SharedPrefs
import com.framework.analytics.SentryController
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import java.lang.IllegalStateException

class CartActivity : AppCompatActivity() {
    private var cartFragment: CartFragment? = null

    lateinit var razorpay: Razorpay
    lateinit var prefs: SharedPrefs

    var fpName: String? = null
    var fpid: String? = null
    var email: String? = null
    var mobileNo: String? = null
    var profileUrl: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var compareBackListener: CompareBackListener? = null
    var experienceCode: String? = null
    var userPurchsedWidgets = ArrayList<String>()
    var isBackCart: Boolean = false
    var deepLinkDay: Int = 7

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        fpid = intent.getStringExtra("fpid")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
        experienceCode = intent.getStringExtra("expCode")
        userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
        email = intent.getStringExtra("email")
        mobileNo = intent.getStringExtra("mobileNo")
        profileUrl = intent.getStringExtra("profileUrl")
        fpName = intent.getStringExtra("fpName")

        prefs = SharedPrefs(this)

        initView()
        initRazorPay()
    }

    fun initView() {

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
            if (currentFragment != null) {
                val tag = currentFragment.tag
                Log.e("Add tag", ">>>$tag")
                tellFragments()
            } else finish()
        }

        cartFragment = CartFragment.newInstance()
        cartFragment?.let { addFragment(it, CART_FRAGMENT) }

    }

    private fun initRazorPay() {
        try {
            razorpay = Razorpay(this, RAZORPAY_KEY)
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    infix fun setBackListener(compareBackListener: CompareBackListener?) {
        this.compareBackListener = compareBackListener
    }


    private fun tellFragments() {
        val fragments =
            supportFragmentManager.fragments
        for (f in fragments) {
            if (f != null && f is BaseFragment)
                f.onBackPressed()
        }
    }

    fun getAccessToken(): String {
        return UserSessionManager(this).getAccessTokenAuth()?.barrierToken() ?: ""
    }


    fun goToHomeFragment() {
        val viewAllFragment = fragmentManager!!.findFragmentByTag(VIEW_ALL_FEATURE)
        val detailsFragment = fragmentManager!!.findFragmentByTag(DETAILS_FRAGMENT)
        if (viewAllFragment != null) {
            fragmentManager!!.popBackStack(
                VIEW_ALL_FEATURE,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else if (detailsFragment != null) {
            fragmentManager!!.popBackStack(
                DETAILS_FRAGMENT,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
        } else {
            fragmentManager!!.popBackStack(CART_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun goBackToMyAddonsScreen() {
        goToHomeFragment()
        val args = Bundle()
        args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
        addFragmentHome(MyAddonsFragment.newInstance(), MYADDONS_FRAGMENT, args)
    }

    fun getRazorpayObject(): Razorpay {
        return razorpay
    }

    private var currentFragment: Fragment? = null
    private var fragmentManager: FragmentManager? = null
    private var fragmentTransaction: FragmentTransaction? = null

    fun addFragment(fragment: Fragment, fragmentTag: String?) {
        currentFragment = fragment
        fragmentManager = supportFragmentManager
        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.add(com.boost.cart.R.id.ao_fragment_container, fragment, fragmentTag)
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
        popFragmentFromBackStack()
        addFragment(fragment, fragmentTag)
//        currentFragment = fragment
//        fragmentManager = supportFragmentManager
//        fragmentTransaction = fragmentManager!!.beginTransaction()
//        fragmentTransaction!!.replace(R.id.ao_fragment_container, fragment, fragmentTag)
////        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        fragmentTransaction!!.commit()
    }

    fun popFragmentFromBackStack() {
        try {
            fragmentManager!!.popBackStack()
        } catch (e: IllegalStateException) {
            SentryController.captureException(e)
            //ignore
        }
    }
}