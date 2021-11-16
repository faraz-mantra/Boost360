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
import com.boost.cart.utils.Constants.Companion.CART_FRAGMENT
import com.boost.cart.utils.SharedPrefs
import com.framework.analytics.SentryController
import es.dmoral.toasty.Toasty
import java.lang.IllegalStateException

class CartActivity : AppCompatActivity() {
    private var cartFragment: CartFragment? = null

    lateinit var prefs: SharedPrefs

    var fpid: String? = null
    var isDeepLink: Boolean = false
    var isOpenCardFragment: Boolean = false
    var compareBackListener: CompareBackListener? = null

    var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


        isDeepLink = intent.getBooleanExtra("isDeepLink", false)
        fpid = intent.getStringExtra("fpid")
        isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)

        prefs = SharedPrefs(this)

        initView()
    }

    fun initView() {

        if (fpid != null) {
            supportFragmentManager.addOnBackStackChangedListener {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
                if (currentFragment != null) {
                    val tag = currentFragment.tag
                    Log.e("Add tag", ">>>$tag")
                    tellFragments()
                } else finish()
            }
            if (isDeepLink || isOpenCardFragment) {
                cartFragment = CartFragment.newInstance()
                cartFragment?.let { addFragment(it, CART_FRAGMENT) }
            }
        } else {
            Toasty.error(
                this,
                "Invalid Business Profile ID. Please restart the marketplace.",
                Toast.LENGTH_LONG
            ).show()
            finish()
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
        } catch (e: IllegalStateException){
            SentryController.captureException(e)
            //ignore
        }
    }
}