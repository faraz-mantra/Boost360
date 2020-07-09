package com.boost.upgrades

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.splash.SplashFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.DETAILS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.HOME_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.ORDER_CONFIRMATION_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.RAZORPAY_KEY
import com.boost.upgrades.utils.Constants.Companion.SPLASH_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class UpgradeActivity : AppCompatActivity() {

  private val splashFragment = SplashFragment()

  lateinit var razorpay: Razorpay

  lateinit var prefs: SharedPrefs

  var experienceCode: String? = null
  var fpName: String? = null
  var fpid: String? = null
  var loginid: String? = null
  var email: String? = null
  var mobileNo: String? = null
  var profileUrl: String? = null
  var isFirebaseDeepLink: Boolean = false
  var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
  private var buyItemWidgetkey: String? = null

  private var initialLoadUpgradeActivity: Int = 0
  lateinit var progressDialog: ProgressDialog
  private var loadingStatus: Boolean = true

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_upgrade)

    isFirebaseDeepLink = intent.getBooleanExtra("isFirebaseDeepLink", false)
    experienceCode = intent.getStringExtra("expCode")
    fpName = intent.getStringExtra("fpName")
    fpid = intent.getStringExtra("fpid")
    loginid = intent.getStringExtra("loginid")
    email = intent.getStringExtra("email")
    mobileNo = intent.getStringExtra("mobileNo")
    profileUrl = intent.getStringExtra("profileUrl")
    //user buying item directly
    buyItemWidgetkey = intent.getStringExtra("buyItemKey")
    progressDialog = ProgressDialog(this)
    prefs = SharedPrefs(this)
    initRazorPay()
    initView()
  }

  fun initView() {
    if (fpid != null) {
      if (isFirebaseDeepLink == true) {
        addFragment(CartFragment.newInstance(), CART_FRAGMENT)
      } else {
        addFragment(HomeFragment.newInstance(), HOME_FRAGMENT)
        //update userdetails and buyitem
        showingPopUp()
        supportFragmentManager.addOnBackStackChangedListener {
          val currentFragment = supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
          if (currentFragment != null) {
            val tag = currentFragment.tag
            Log.e("Add tag", ">>>$tag")
            tellFragments()
          } else finish()

        }
      }
    } else {
      Toasty.error(this, "Invalid Business Profile ID. Please restart the marketplace.", Toast.LENGTH_LONG).show()
      finish()
    }
  }

  private fun initRazorPay() {
    razorpay = Razorpay(this, RAZORPAY_KEY)
  }

  override fun onBackPressed() {
    performBackPressed()
  }

  private fun goHomeActivity() {
    try {
      val i = Intent(this, Class.forName("com.nowfloats.NavigationDrawer.HomeActivity"))
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(i)
      overridePendingTransition(0, 0)
      finish()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun performBackPressed() {
    try {
      Utils.hideSoftKeyboard(this)
      if (supportFragmentManager.backStackEntryCount > 0) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
        val tag = currentFragment?.tag
        Log.e("back pressed tag", ">>>$tag")
        if (tag != null) {
          if (tag == ORDER_CONFIRMATION_FRAGMENT) {
            goToHomeFragment()
          } else if (isFirebaseDeepLink == true && tag == CART_FRAGMENT) {
            goHomeActivity()
          } else fragmentManager!!.popBackStack()
        }
      } else {
        if (isFirebaseDeepLink == false) super.onBackPressed()
        else goHomeActivity()
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
//        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        fragmentTransaction!!.commit()
  }

  fun popFragmentFromBackStack() {
    fragmentManager!!.popBackStack()
  }

  fun goToHomeFragment() {
    val viewAllFragment = fragmentManager!!.findFragmentByTag(VIEW_ALL_FEATURE)
    val detailsFragment = fragmentManager!!.findFragmentByTag(DETAILS_FRAGMENT)
    when {
      viewAllFragment != null -> fragmentManager?.popBackStack(VIEW_ALL_FEATURE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
      detailsFragment != null -> fragmentManager?.popBackStack(DETAILS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
      else -> fragmentManager?.popBackStack(CART_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
  }

  fun goBackToMyAddonsScreen() {
    goToHomeFragment()
    addFragment(MyAddonsFragment.newInstance(), MYADDONS_FRAGMENT)
  }

  fun goBackToRecommentedScreen() {
    goToHomeFragment()
    addFragment(ViewAllFeaturesFragment.newInstance(), VIEW_ALL_FEATURE)
  }


  private fun tellFragments() {
    val fragments = supportFragmentManager.fragments
    for (f in fragments) {
      if (f != null && f is BaseFragment) f.onBackPressed()
    }
  }

  fun getRazorpayObject(): Razorpay {
    return razorpay
  }

  fun adjustScreenForKeyboard() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }

  fun showingPopUp() {
    if (loadingStatus && initialLoadUpgradeActivity == 0) {
      loaderStatus(true)
    }
    CompositeDisposable().add(
        AppDatabase.getInstance(application)!!
            .featuresDao()
            .checkEmptyFeatureTable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
              if (it == 1) {
                loaderStatus(false)
                if (buyItemWidgetkey != null) {
//                Handler().postDelayed({
                  val details = DetailsFragment.newInstance()
                  val args = Bundle()
                  args.putString("itemId", buyItemWidgetkey)
                  details.arguments = args
                  addFragment(details, Constants.DETAILS_FRAGMENT)
//                }, 1000)
                }
                //turn this on when you want to show Welcome Market Screen all the time
                //prefs.storeInitialLoadMarketPlace(true)
                else if (prefs.getInitialLoadMarketPlace()) {
//                Handler().postDelayed({
                  splashFragment.show(
                      supportFragmentManager,
                      SPLASH_FRAGMENT
                  )
//                }, 1000)
                }
              } else {
                //recall after 1 second
                Handler().postDelayed({
                  if (initialLoadUpgradeActivity < 3) {
                    initialLoadUpgradeActivity += 1
                    showingPopUp()
                  } else {
                    loaderStatus(false)
                    Toasty.error(this, "Not able to Fetch data from database. Try Later..", Toast.LENGTH_LONG).show()
                  }
                }, 1000)
              }
            }, {
              loaderStatus(false)
              Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
            })
    )

  }

  fun loaderStatus(status: Boolean) {
    if (status) {
      loadingStatus = true
      val status = "Loading. Please wait..."
      progressDialog.setMessage(status)
      progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
      progressDialog.show()
    } else {
      loadingStatus = false
      progressDialog.dismiss()
    }
  }

}
