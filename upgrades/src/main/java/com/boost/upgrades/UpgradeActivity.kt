package com.boost.upgrades

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.upgrades.interfaces.CompareBackListener
import com.boost.upgrades.ui.cart.CartFragment
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.ui.features.ViewAllFeaturesFragment
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.myaddons.MyAddonsFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.packages.PackageFragmentNew
import com.boost.upgrades.ui.splash.SplashFragment
import com.boost.upgrades.utils.*
import com.boost.upgrades.utils.Constants.Companion.CART_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.DETAILS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.HOME_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.MYADDONS_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.ORDER_CONFIRMATION_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.PAYMENT_FRAGMENT
import com.boost.upgrades.utils.Constants.Companion.VIEW_ALL_FEATURE
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
import com.framework.webengageconstant.*
import com.boost.upgrades.utils.NetworkConnectivitySpeed.checkNetworkType
import com.framework.analytics.SentryController
import com.framework.pref.*
import com.framework.utils.BuildConfigUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.razorpay.Razorpay
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.IllegalStateException


class UpgradeActivity : AppCompatActivity() {

  private val splashFragment = SplashFragment()

  private var cartFragment: CartFragment? = null

  lateinit var razorpay: Razorpay

  lateinit var prefs: SharedPrefs

  var experienceCode: String? = null
  var fpName: String? = null
  var fpid: String? = null
  var fpTag: String? = null
  var email: String? = null
  var mobileNo: String? = null
  var profileUrl: String? = null
  var accountType: String? = null
  var isDeepLink: Boolean = false
  var isOpenCardFragment: Boolean = false
  var isBackCart: Boolean = false
  var isOpenPackageWithID: String? = null

  var deepLinkViewType: String = ""
  var deepLinkDay: Int = 7
  var compareBackListener: CompareBackListener? = null

  var clientid: String = clientId
  private var widgetFeatureCode: String? = null

  private var initialLoadUpgradeActivity: Int = 0
  lateinit var progressDialog: ProgressDialog
  private var loadingStatus: Boolean = true
  var userPurchsedWidgets = ArrayList<String>()
  var timerCallbackConst: Long = 1000


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_upgrade)
    if (packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) {
      Toast.makeText(this, "Coming soon...", Toast.LENGTH_LONG).show()
      this.finish()
    } else {
      isDeepLink = intent.getBooleanExtra("isDeepLink", false)
      deepLinkViewType = intent.getStringExtra("deepLinkViewType") ?: ""
      deepLinkDay = intent.getStringExtra("deepLinkDay")?.toIntOrNull() ?: 7

    experienceCode = intent.getStringExtra("expCode")
    fpName = intent.getStringExtra("fpName")
    fpid = intent.getStringExtra("fpid")
    fpTag = intent.getStringExtra("fpTag")
    email = intent.getStringExtra("email")
    mobileNo = intent.getStringExtra("mobileNo")
    profileUrl = intent.getStringExtra("profileUrl")
    accountType = intent.getStringExtra("accountType")
    isOpenCardFragment = intent.getBooleanExtra("isOpenCardFragment", false)
    //user buying item directly
    widgetFeatureCode = intent.getStringExtra("buyItemKey")
    userPurchsedWidgets = intent.getStringArrayListExtra("userPurchsedWidgets") ?: ArrayList()
      //festive poster purchase
      isOpenPackageWithID = intent.getStringExtra("isOpenPackageWithID")

      progressDialog = ProgressDialog(this)

      prefs = SharedPrefs(this)
//    WebEngageController.trackEvent(EVENT_NAME_ADDONS_MARKETPLACE, PAGE_VIEW, NO_EVENT_VALUE)
      initView()
      initRazorPay()
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    Log.e("UpgradeActivity", "testing")
    val bundle = Bundle()
    bundle.putString("screenType", intent!!.getStringExtra("screenType"))
    bundle.putStringArrayList(
      "userPurchsedWidgets",
      intent.getStringArrayListExtra("userPurchsedWidgets")
    )
    bundle.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
    bundle.putString("buyItemKey", intent.getStringExtra("buyItemKey"))
    val homeFragment = HomeFragment.newInstance()
    homeFragment.arguments = bundle

        fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction!!.replace(R.id.ao_fragment_container, homeFragment, HOME_FRAGMENT)
//        fragmentTransaction!!.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmentTransaction!!.commit()
//    replaceFragment(homeFragment, HOME_FRAGMENT)
  }

  infix fun setBackListener(compareBackListener: CompareBackListener?) {
    this.compareBackListener = compareBackListener
  }


  fun initView() {

    if (fpid != null) {
      val bundle = Bundle()
      bundle.putString("screenType", intent.getStringExtra("screenType"))
      bundle.putStringArrayList(
        "userPurchsedWidgets",
        intent.getStringArrayListExtra("userPurchsedWidgets")
      )
      bundle.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
      bundle.putString("buyItemKey", intent.getStringExtra("buyItemKey"))
//      addFragment(HomeFragment.newInstance(), HOME_FRAGMENT)
      addFragmentHome(HomeFragment.newInstance(), HOME_FRAGMENT, bundle)
      //update userdetails and buyitem
      showingPopUp(checkNetworkType(applicationContext))
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
      if(!isOpenPackageWithID.isNullOrEmpty()){
        goToSinglePackage(isOpenPackageWithID!!)
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

  private fun initRazorPay() {
    try {
      val razorPayKey: String = BuildConfig.RAZORPAY_KEY
      razorpay = Razorpay(this, razorPayKey)
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

//  public fun initYoutube(){
//    youTubePlayerFragment = getSupportFragmentManager()
//            .findFragmentById(R.id.youtube_fragment) as YouTubePlayerFragment
//  }

  override fun onBackPressed() {
    performBackPressed()
  }

  private fun goHomeActivity() {
    try {
      val i = Intent(this, Class.forName("com.dashboard.controller.DashboardActivity"))
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
      startActivity(i)
      overridePendingTransition(0, 0)
      finish()
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }
  fun getAccessToken(): String {
   return UserSessionManager(this).getAccessTokenAuth()?.barrierToken()?:""
  }

  private fun performBackPressed() {
    try {
      Utils.hideSoftKeyboard(this)
      if (supportFragmentManager.backStackEntryCount > 0) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
        val tag = currentFragment?.tag
        Log.e("back pressed tag", ">>>$tag")
        if (tag != null) {
          if (tag == CART_FRAGMENT) {
            WebEngageController.trackEvent(
              ADDONS_MARKETPLACE_CLICKED_BACK_BUTTON_CART_SCREEN,
              ADDONS_MARKETPLACE,
              NO_EVENT_VALUE
            )
            supportFragmentManager.addOnBackStackChangedListener {
              val currentFragment =
                supportFragmentManager.findFragmentById(R.id.ao_fragment_container)
              if (currentFragment != null) {
                val tag = currentFragment.tag
                Log.e("Add tagu", ">>>$tag")
                if (tag == Constants.COMPARE_FRAGMENT) {
                  Log.e("Add tags", ">>>$tag")
                  compareBackListener!!.backComparePress()
                } else if (tag == HOME_FRAGMENT) {
                  compareBackListener!!.backComparePress()
                }
              }
            }
          }
          if (tag == PAYMENT_FRAGMENT)
            WebEngageController.trackEvent(
              ADDONS_MARKETPLACE_CLICKED_BACK_BUTTON_PAYMENTSCREEN,
              ADDONS_MARKETPLACE,
              NO_EVENT_VALUE
            )
          if (tag == ORDER_CONFIRMATION_FRAGMENT) {
            if (isDeepLink) goHomeActivity()
            else goToHomeFragment()
          } else if ((isDeepLink || isOpenCardFragment) && (tag == HOME_FRAGMENT)) {
            if (cartFragment != null && cartFragment?.isRenewalListNotEmpty() == true) alertDialog()
            else goHomeActivity()
          } else fragmentManager!!.popBackStack()
        }
      } else {
        if ((isDeepLink || isOpenCardFragment)) goHomeActivity()
        else super.onBackPressed()
      }
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
    }
  }

  private fun alertDialog() {
    val builder: AlertDialog.Builder = AlertDialog.Builder(this@UpgradeActivity)
    val viewGroup = findViewById<ViewGroup>(android.R.id.content)
    val dialogView: View = LayoutInflater.from(this).inflate(R.layout.alert_view, viewGroup, false)
    builder.setView(dialogView)
    val alertDialog: AlertDialog = builder.create()
    dialogView.findViewById<TextView>(R.id.no_btn).setOnClickListener { alertDialog.dismiss() }
    dialogView.findViewById<TextView>(R.id.yes_btn).setOnClickListener {
      prefs.storeCartOrderInfo(null)
      goHomeActivity()
      alertDialog.dismiss()
    }
    alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.show()
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

  fun goToHomeFragment() {
    val viewAllFragment = fragmentManager!!.findFragmentByTag(VIEW_ALL_FEATURE)
    val detailsFragment = fragmentManager!!.findFragmentByTag(DETAILS_FRAGMENT)
    if (viewAllFragment != null) {
      fragmentManager!!.popBackStack(VIEW_ALL_FEATURE, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    } else if (detailsFragment != null) {
      fragmentManager!!.popBackStack(DETAILS_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    } else {
      fragmentManager!!.popBackStack(CART_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE)
      compareBackListener!!.backComparePress()
    }
  }

  fun goBackToMyAddonsScreen() {
    goToHomeFragment()
    val args = Bundle()
    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
    addFragmentHome(MyAddonsFragment.newInstance(), MYADDONS_FRAGMENT, args)
  }

  fun goBackToRecommentedScreen() {
    goToHomeFragment()
    val args = Bundle()
    args.putStringArrayList("userPurchsedWidgets", userPurchsedWidgets)
    addFragmentHome(ViewAllFeaturesFragment.newInstance(), VIEW_ALL_FEATURE, args)
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

  fun adjustScreenForKeyboard() {
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
  }

  fun showingPopUp(networkRecallTimer: Long) {
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
            if (widgetFeatureCode != null) {
              CompositeDisposable().add(
                AppDatabase.getInstance(application)!!
                  .featuresDao()
                  .checkFeatureTableKeyExist(widgetFeatureCode!!)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe({
                    if (it == 1) {
                      val details = DetailsFragment.newInstance()
                      val args = Bundle()
                      args.putString("itemId", widgetFeatureCode)
                      details.arguments = args
                      addFragment(details, Constants.DETAILS_FRAGMENT)
                    } else {
//                                              Toasty.error(this, "This Add-ons Not Available to Your Account.", Toast.LENGTH_LONG).show()
                    }
                  }, {
                    Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG)
                      .show()
                  })
              )
            }
            //turn this on when you want to show Welcome Market Screen all the time
            //prefs.storeInitialLoadMarketPlace(true)
            else if (prefs.getInitialLoadMarketPlace()) {
              Log.v("getInitialLoadM", " getInitialLoadM")
//                Handler().postDelayed({
              /*splashFragment.show(
          supportFragmentManager,
          SPLASH_FRAGMENT
      )*/
//                }, 1000)
            }
          } else {
            //recall after 1 second
            Handler().postDelayed({
              if (initialLoadUpgradeActivity < 3) {
                initialLoadUpgradeActivity += 1
                showingPopUp(checkNetworkType(applicationContext))
              } else {
                loaderStatus(false)
//                Toasty.error(
//                  this,
//                  "Critical error occurred while loading the Addon Marketplace. Please close the app and try again.\n\nIf the issue persists, please get in touch with the Support Team.",
//                  Toast.LENGTH_LONG
//                ).show()
              }
            }, networkRecallTimer) // networkRecallTimer works based on the network speed
          }
        }, {
          loaderStatus(false)
          Toasty.error(this, "Something went wrong. Try Later..", Toast.LENGTH_LONG).show()
        })
    )

  }

  fun goToSinglePackage(bundleID: String){
    CompositeDisposable().add(
      AppDatabase.getInstance(application)!!
        .bundlesDao()
        .checkBundleKeyExist(bundleID)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          if (it == 1) {
            CompositeDisposable().add(
              AppDatabase.getInstance(application)!!
                .bundlesDao()
                .getBundleItemById(bundleID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                  var selectedBundle: Bundles? = null
                  var item = it
                  val temp =
                    Gson().fromJson<List<IncludedFeature>>(
                      item.included_features,
                      object :
                        TypeToken<List<IncludedFeature>>() {}.type
                    )
                  selectedBundle = Bundles(
                    bundleID,
                    temp,
                    item.min_purchase_months,
                    item.name,
                    item.overall_discount_percent,
                    PrimaryImage(item.primary_image),
                    item.target_business_usecase,
                    Gson().fromJson<List<String>>(
                      item.exclusive_to_categories,
                      object :
                        TypeToken<List<String>>() {}.type
                    ),
                    null,
                    item.desc
                  )
                  val packageFragment =
                    PackageFragment.newInstance()
                  val args = Bundle()
                  args.putString(
                    "bundleData",
                    Gson().toJson(selectedBundle)
                  )
                  packageFragment.arguments = args
                  addFragment(
                    packageFragment,
                    Constants.PACKAGE_FRAGMENT
                  )

                }, {
                  it.printStackTrace()
                })
            )
          } else {
            Toasty.error(
              this,
              "Bundle Not Available To This Account",
              Toast.LENGTH_LONG
            ).show()
          }
        }, {
          it.printStackTrace()
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