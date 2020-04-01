package com.boost.upgrades

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.DETAILS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.HOME_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.ORDER_CONFIRMATION_FRAGMENT
import com.boost.upgrades.utils.Utils
import com.razorpay.Razorpay


class UpgradeActivity : AppCompatActivity() {

    lateinit var razorpay: Razorpay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        initView()
        initRazorPay()
    }

    fun initView() {
        val homeFragment: HomeFragment = HomeFragment.newInstance()
        val args = Bundle()
        args.putString("data", "21")
        homeFragment.arguments = args
        addFragment(homeFragment, HOME_FRAGMENT)

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment =
                supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
            if (currentFragment != null) {
                val tag = currentFragment!!.tag
                Log.e("tag", ">>>$tag")
                tellFragments()
            } else {
                finish()
            }
        }
    }

    private fun initRazorPay() {
//        razorpay = Razorpay(this, "rzp_test_IhjurgOAcEKqJU")
        razorpay = Razorpay(this, "rzp_test_OlLpIGwhA7bATX")  //provided by tanmay
    }

    override fun onBackPressed() {
        performBackPressed()
    }

    private fun performBackPressed() {
        try {
            Utils.hideSoftKeyboard(this)
            if (supportFragmentManager.backStackEntryCount > 0) {
                val currentFragment =
                    supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
                val tag = currentFragment!!.tag
                Log.e("back pressed tag", ">>>$tag")
                if (tag != null) {
                    if(tag == ORDER_CONFIRMATION_FRAGMENT){
                        goToHomeFragment()
                    }else {
                        fragmentManager!!.popBackStack()
                    }
                }
            } else {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
        fragmentManager!!.popBackStack()
    }

    fun goToHomeFragment() {
        val detailsFragment = fragmentManager!!.findFragmentByTag(DETAILS_FRAGMENT)
        if(detailsFragment != null){
            fragmentManager!!.popBackStack(DETAILS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }else{
            fragmentManager!!.popBackStack(CART_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun goBackToMyAddonsScreen(){
        goToHomeFragment()
        addFragment(MyAddonsFragment.newInstance(), MYADDONS_FRAGMENT)
    }

    private fun tellFragments() {
        val fragments =
            supportFragmentManager.fragments
        for (f in fragments) {
            if (f != null && f is BaseFragment)
                f.onBackPressed()
        }
    }

    fun getRazorpayObject(): Razorpay {
        return razorpay
    }

    fun adjustScreenForKeyboard(){
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

}
