package com.boost.upgrades.ui.cart

//import com.boost.upgrades.data.api_model.PurchaseOrder.request.*
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.CartAddonsAdaptor
import com.boost.upgrades.adapter.CartPackageAdaptor
import com.boost.upgrades.adapter.CartRenewalAdaptor
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.ExtendedProperty
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.upgrades.data.api_model.PurchaseOrder.requestV2.*
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.upgrades.data.model.BundlesModel
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.renewalcart.CreateCartStateRequest
import com.boost.upgrades.data.renewalcart.RenewalPurchasedRequest
import com.boost.upgrades.data.renewalcart.RenewalResult
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.CartFragmentListener
import com.boost.upgrades.ui.autorenew.AutoRenewSubsFragment
import com.boost.upgrades.ui.checkoutkyc.CheckoutKycFragment
import com.boost.upgrades.ui.packages.PackageFragment
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.CouponPopUpFragment
import com.boost.upgrades.ui.popup.GSTINPopUpFragment
import com.boost.upgrades.ui.popup.RenewalPopUpFragment
import com.boost.upgrades.ui.popup.TANPopUpFragment
import com.boost.upgrades.ui.splash.SplashFragment
import com.boost.upgrades.utils.*
import com.boost.upgrades.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.GSTIN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.TAN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.DateUtils.parseDate
import com.dashboard.model.live.coupon.CouponServiceModel
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.cart_fragment.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.content.Intent
import android.net.Uri
import java.lang.NumberFormatException
import android.text.InputFilter
import android.text.TextUtils
import com.boost.upgrades.data.api_model.paymentprofile.LastPaymentMethodDetails
import com.boost.upgrades.ui.compare.ComparePackageFragment
import com.framework.analytics.SentryController
import kotlinx.android.synthetic.main.cart_fragment.coupon_discount_title
import kotlinx.android.synthetic.main.cart_fragment.coupon_discount_value
import kotlinx.android.synthetic.main.cart_fragment.igst_value
import kotlinx.android.synthetic.main.cart_fragment.package_layout


class CartFragment : BaseFragment(), CartFragmentListener {

  lateinit var root: View

  lateinit var localStorage: LocalStorage

  var customerId: String = ""

  lateinit var cartList: ArrayList<CartModel>

  lateinit var featuresList: List<FeaturesModel>

  lateinit var bundlesList: List<BundlesModel>

  lateinit var renewalList: List<RenewalResult>

  var bundles_in_cart = false
  var default_validity_months = 1
  var package_validity_months = 1

  var total = 0.0

  var coupontotal = 0.0

  var grandTotal = 0.0

  var GSTINNumber: String? = null
  var TANNumber: String? = null

  var proceedRenewPopup: Boolean? = null

  var taxValue = 0.0

  var validCouponCode: CouponsModel? = null

  var couponServiceModel: CouponServiceModel? = null

  var couponDiscountAmount = 0.0

  lateinit var progressDialog: ProgressDialog

  //    private var cartAdapter = CartAdapter(ArrayList())
  lateinit var cartPackageAdaptor: CartPackageAdaptor
  lateinit var cartAddonsAdaptor: CartAddonsAdaptor
  lateinit var cartRenewalAdaptor: CartRenewalAdaptor

  val couponPopUpFragment = CouponPopUpFragment()

  val gstinPopUpFragment = GSTINPopUpFragment()

  val tanPopUpFragment = TANPopUpFragment()

  val renewPopUpFragment = RenewalPopUpFragment()

  val splashFragment = SplashFragment()

  //    var couponDiwaliRedundant : MutableList<String?> = java.util.ArrayList()
  var couponDiwaliRedundant: HashMap<String?, String?> = HashMap<String?, String?>()

  lateinit var prefs: SharedPrefs
  var totalValidityDays = 0
  val checkoutKycFragment = CheckoutKycFragment()

  var proceedCheckoutPopup: Boolean? = false

  var couponCode: String = ""

  private var cartItems = ArrayList<String>()

  private var cartFullItems = ArrayList<String>()

  private var event_attributes: HashMap<String, Any> = HashMap()



  companion object {
    fun newInstance() = CartFragment()
  }

  private lateinit var viewModel: CartViewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    root = inflater.inflate(R.layout.cart_fragment, container, false)
    localStorage = LocalStorage.getInstance(requireContext())!!

    progressDialog = ProgressDialog(requireContext())

    cartPackageAdaptor = CartPackageAdaptor(ArrayList(), this)
    cartAddonsAdaptor = CartAddonsAdaptor(ArrayList(), this)
    cartRenewalAdaptor = CartRenewalAdaptor(ArrayList(), this)
    prefs = SharedPrefs(activity as UpgradeActivity)
    WebEngageController.trackEvent(ADDONS_MARKETPLACE_CART, PAGE_VIEW, NO_EVENT_VALUE)

    return root
  }

  override fun onResume() {
    super.onResume()
    Log.e("onResume", "onResume of LoginFragment")
    viewModel.updateRenewValue("")
//        initMvvM()
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.e("onDestroy", "onDestroy of LoginFragment")
    viewModel.updateRenewValue("")
    requireActivity().viewModelStore.clear()
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)
    Constants.COMPARE_BACK_VALUE = 1
    prefs.storeCompareState(1)
//        showpopup()
    loadLastUsedPayData()
    initializePackageRecycler()
    initializeAddonsRecycler()
    initializeRenewalRecycler()
    initializeErrorObserver()
    initMvvM()
    observeLastPaymentDetails()
    checkRenewalItemDeepLinkClick()
    gst_layout.visibility = View.GONE
    //show applyed coupon code
    if (prefs.getApplyedCouponDetails() != null) {
      validCouponCode = prefs.getApplyedCouponDetails()
      discount_coupon_title.text = validCouponCode!!.coupon_key
      cart_apply_coupon.visibility = View.GONE
      discount_coupon_remove.visibility = View.VISIBLE
    } else {
      validCouponCode = null
      discount_coupon_remove.visibility = View.GONE
      cart_apply_coupon.visibility = View.VISIBLE
      discount_coupon_title.text = "Discount coupon"
    }

    discount_coupon_remove.setOnClickListener {
      discount_coupon_remove.visibility = View.GONE
      cart_apply_coupon.visibility = View.VISIBLE
      discount_coupon_title.text = "Discount coupon"
      discount_coupon_message.visibility = View.GONE
      //clear coupon
      validCouponCode = null

      //remove saved orderdetails and coupondetails from prefs
      prefs.storeCartOrderInfo(null)
      prefs.storeApplyedCouponDetails(null)

//            totalCalculation()
      couponCode = ""
      couponServiceModel = null

      totalCalculationAfterCoupon()
    }

    cart_continue_submit.setOnClickListener {

//            if (prefs.getInitialLoadMarketPlace() && proceedCheckoutPopup == false) {
//
//                checkoutKycFragment.show(
//                        (activity as UpgradeActivity).supportFragmentManager,
//                        CHECKOUT_KYC_FRAGMENT
//                )
//            }else{
      /*       renewPopUpFragment.show(
             (activity as UpgradeActivity).supportFragmentManager,
             RENEW_POPUP_FRAGEMENT
     )*/
      if (TextUtils.isEmpty(months_validity.text.toString())) {
        months_validity.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Validity is empty", Toast.LENGTH_SHORT).show()
      } else if (bundles_in_cart && months_validity.text.toString().toInt() < package_validity_months) {
        months_validity.setBackgroundResource(R.drawable.et_validity_error)
        Toasty.error(requireContext(), "Validity is not valid", Toast.LENGTH_SHORT).show()
      } else {
        months_validity.setBackgroundResource(R.drawable.et_validity)
        if (prefs.getCartOrderInfo() != null) {
          proceedToPayment(prefs.getCartOrderInfo()!!)
        } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
          val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
          if (renewalItems.isNullOrEmpty().not()) {
            createCartStateRenewal(renewalItems)
          } else createPurchaseOrder(null)
        } else {
          Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
        }
      }


//            }

/*            renewPopUpFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    RENEW_POPUP_FRAGEMENT
            )*/

      /*viewModel.getRenewValue().observeOnce(this, Observer {
          if (it != null) {
              Log.i("getRenewValuesamp >> ", it )
              if(it.equals("REMIND_ME")){
//                if(it.equals("REMIND_ME")  && proceedRenewPopup!!){
                  if (prefs.getCartOrderInfo() != null) {
                      proceedToPayment(prefs.getCartOrderInfo()!!)
                  } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
                      val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
                      if (renewalItems.isNullOrEmpty().not()) {
                          createCartStateRenewal(renewalItems)
                      } else createPurchaseOrder(null)
                  } else {
                      Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
                  }
              }else if(it.equals("AUTO_RENEW")){
//                }else if(it.equals("AUTO_RENEW") && proceedRenewPopup!!){
                  viewModel.updateProceedClick(false)
                  if (prefs.getCartOrderInfo() != null) {
//                        proceedToAutoRenewPayment(prefs.getCartOrderInfo()!!)
                      createPurchaseAutoSubscriptionOrder(null)
                      Log.i("getRenewValuesamp1 >> ", it )
                  } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
                      val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
                      if (renewalItems.isNullOrEmpty().not()) {
                          Log.i("getRenewValuesamp2 >> ", it )
                          createCartStateRenewal(renewalItems)
                      } else {createPurchaseAutoSubscriptionOrder(null)
                          Log.i("getRenewValuesamp3 >> ", it )
                      }
                  } else {
                      Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
                  }
              }
          }
      })*/
      /*if (prefs.getCartOrderInfo() != null) {
          proceedToPayment(prefs.getCartOrderInfo()!!)
      } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
          val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
          if (renewalItems.isNullOrEmpty().not()) {
              createCartStateRenewal(renewalItems)
          } else createPurchaseOrder(null)
      } else {
          Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
      }*/


    }

    back_button12.setOnClickListener {
      WebEngageController.trackEvent(ADDONS_MARKETPLACE_CART_BACK, NO_EVENT_LABLE, event_attributes)
      (activity as UpgradeActivity).onBackPressed()
    }

    cart_view_details.setOnClickListener {
      cart_main_scroller.post {
        cart_main_scroller.fullScroll(View.FOCUS_DOWN)
      }
    }


/*        cart_apply_coupon.setOnClickListener {
            if(couponDiwaliRedundant.contains("WILDFIRE_FB_LEAD_ADS") ){
                Log.v("couponDiwaliRedundant1", " "+ couponDiwaliRedundant)
            }else if(couponDiwaliRedundant.contains("WILDFIRE") ){
                Log.v("couponDiwaliRedundant2", " "+ couponDiwaliRedundant)
            }else if(couponDiwaliRedundant.contains("DICTATE") ){
                Log.v("couponDiwaliRedundant3", " "+ couponDiwaliRedundant)
            }else{
            couponPopUpFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    COUPON_POPUP_FRAGEMENT
            )
            }
        }*/

    cart_apply_coupon.setOnClickListener {
      if (couponDiwaliRedundant.contains("WILDFIRE_FB_LEAD_ADS")) {
        Toasty.error(requireContext(), "In order to apply coupon remove the item " + couponDiwaliRedundant.get("WILDFIRE_FB_LEAD_ADS"), Toast.LENGTH_SHORT, true).show()
      } else if (couponDiwaliRedundant.contains("WILDFIRE")) {
        Toasty.error(requireContext(), "In order to apply coupon remove the item " + couponDiwaliRedundant.get("WILDFIRE"), Toast.LENGTH_SHORT, true).show()
      } else if (couponDiwaliRedundant.contains("DICTATE")) {
        Toasty.error(requireContext(), "In order to apply coupon remove the item " + couponDiwaliRedundant.get("DICTATE"), Toast.LENGTH_SHORT, true).show()
      } else {
        /*couponPopUpFragment.show(
                (activity as UpgradeActivity).supportFragmentManager,
                COUPON_POPUP_FRAGEMENT
        )*/
        val args = Bundle()
//                args.putDouble("cartValue", grandTotal)
        args.putDouble("cartValue", total)
        couponPopUpFragment.arguments = args
        couponPopUpFragment.show(
          (activity as UpgradeActivity).supportFragmentManager,
          COUPON_POPUP_FRAGEMENT
        )
      }
      /*couponPopUpFragment.show(
              (activity as UpgradeActivity).supportFragmentManager,
              COUPON_POPUP_FRAGEMENT
      )*/
    }

    mp_cart_compare_packs.setOnClickListener {
      val args = Bundle()
      args.putStringArrayList(
        "userPurchsedWidgets",
        arguments?.getStringArrayList("userPurchsedWidgets")
      )
      (activity as UpgradeActivity).addFragmentHome(
        ComparePackageFragment.newInstance(),
        Constants.COMPARE_FRAGMENT, args
      )
    }
    cart_spk_to_expert.setOnClickListener {
      speakToExpert(prefs.getExpertContact())
    }
    enter_gst_number.setOnClickListener {
      gstinPopUpFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        GSTIN_POPUP_FRAGEMENT
      )
    }

    enter_gstin_number.setOnClickListener {
      gstinPopUpFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        GSTIN_POPUP_FRAGEMENT
      )
    }

    gstin_remove.setOnClickListener {
      GSTINNumber = null
      gstin_title.text = "GSTIN (optional)"
      entered_gstin_number.visibility = View.GONE
      gstin_remove.visibility = View.GONE
      enter_gstin_number.visibility = View.VISIBLE
    }

    tan_remove.setOnClickListener {
      TANNumber = null
      tan_title.text = "TAN (optional)"
      entered_tan_number.visibility = View.GONE
      tan_remove.visibility = View.GONE
      enter_tan_number.visibility = View.VISIBLE
    }

    remove_gstin_number.setOnClickListener {
      GSTINNumber = null
      gstin_layout1.visibility = View.VISIBLE
      gstin_layout2.visibility = View.GONE
      fill_in_gstin_value.text = ""
    }

    enter_tan_number.setOnClickListener {
      tanPopUpFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        TAN_POPUP_FRAGEMENT
      )
    }
    entered_tan_number.setOnClickListener {
      tanPopUpFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        TAN_POPUP_FRAGEMENT
      )
    }

//    all_recommended_addons.setOnClickListener {
//      (activity as UpgradeActivity).goBackToRecommentedScreen()
//    }

    totalValidityDays = 30 * 1
    prefs.storeMonthsValidity(totalValidityDays)
    months_validity_edit_inc.setOnClickListener {
      if (!bundles_in_cart) {
//                if (default_validity_months < 12){
//                    default_validity_months++
        if (default_validity_months == 1) {
          default_validity_months = default_validity_months + 2
        } else if (default_validity_months >= 12 && default_validity_months < 60) {
          if (default_validity_months % 12 == 0) {
            default_validity_months = default_validity_months + 12
          } else {
            default_validity_months = default_validity_months + ((12 - default_validity_months % 12))
          }
//        default_validity_months = default_validity_months+ 12
        } else {
          if (default_validity_months < 60) {
            if (default_validity_months % 3 == 0) {
              default_validity_months = default_validity_months + 3
            } else if (default_validity_months % 3 == 1) {
              default_validity_months = default_validity_months + 2
            } else if (default_validity_months % 3 == 2) {
              default_validity_months = default_validity_months + 1
            }
          }
//                        default_validity_months = default_validity_months+ 3
        }
//                months_validity.text = default_validity_months.toString() + " months"
        months_validity.setText(default_validity_months.toString())
        prefs.storeCartValidityMonths(default_validity_months.toString())
        totalValidityDays = 30 * default_validity_months
        prefs.storeMonthsValidity(totalValidityDays)
        prefs.storeCartOrderInfo(null)
//                totalCalculation()
        totalCalculationAfterCoupon()
        Log.v("cart_amount_value1", " " + total)
        if (couponCode.isNotEmpty())
          viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
        else
          totalCalculationAfterCoupon()
//                Toasty.success(requireContext(), "Validity increased by 1 month.", Toast.LENGTH_SHORT, true).show()
//            }
      } else if (bundles_in_cart) {
//                if (default_validity_months < 12){
        if (default_validity_months == 1) {
          default_validity_months = default_validity_months + 2
//    }else if(default_validity_months % 12 == 0 && default_validity_months < 60){
        } else if (default_validity_months >= 12 && default_validity_months < 60) {
          if (default_validity_months % 12 == 0) {
            default_validity_months = default_validity_months + 12
          } else {
//            Log.v("validity_calculator", " "+ (default_validity_months - default_validity_months % 12)  + " "+default_validity_months + " "+ (default_validity_months % 12) )
            default_validity_months = default_validity_months + ((12 - default_validity_months % 12))
          }
//        default_validity_months = default_validity_months+ 12
        } else {
//        if(default_validity_months < 60)
          if (default_validity_months < 60 && default_validity_months < 12) {
            if (default_validity_months % 3 == 0) {
              default_validity_months = default_validity_months + 3
            } else if (default_validity_months % 3 == 1) {
              default_validity_months = default_validity_months + 2
            } else if (default_validity_months % 3 == 2) {
              default_validity_months = default_validity_months + 1
            }
          }
//          default_validity_months = default_validity_months+ 3
        }

//            default_validity_months++
//            months_validity.text = default_validity_months.toString() + " months"
        months_validity.setText(default_validity_months.toString())
        prefs.storeCartValidityMonths(default_validity_months.toString())
        totalValidityDays = 30 * default_validity_months
        prefs.storeMonthsValidity(totalValidityDays)
        prefs.storeCartOrderInfo(null)
//                totalCalculation()
        totalCalculationAfterCoupon()
        Log.v("cart_amount_value1", " " + total)
        if (couponCode.isNotEmpty())
          viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
        else
          totalCalculationAfterCoupon()
//            Toasty.success(requireContext(), "Validity increased by 3 month(s).", Toast.LENGTH_SHORT, true).show()
//            }
      }
    }

    months_validity_edit_dsc.setOnClickListener {
      if (!bundles_in_cart) {
        if (default_validity_months > 1) {
//                    default_validity_months--
//                    if(default_validity_months > 12 && default_validity_months % 12 == 0){
          if (default_validity_months > 12) {
//                        default_validity_months = default_validity_months - 12
            if (default_validity_months % 12 == 0) {
              default_validity_months = default_validity_months - 12
            } else {
              default_validity_months = default_validity_months - ((default_validity_months % 12))
            }
          } else {
//                        default_validity_months = default_validity_months - 3
            if (default_validity_months <= 12) {
              if (default_validity_months % 3 == 0) {
                default_validity_months = default_validity_months - 3
              } else if (default_validity_months % 3 == 1) {
                default_validity_months = default_validity_months - 1
              } else if (default_validity_months % 3 == 2) {
                default_validity_months = default_validity_months - 2
              }
            }
          }
          if (default_validity_months < 1) {
            default_validity_months = 1
          }

          totalValidityDays = 30 * default_validity_months
          prefs.storeMonthsValidity(totalValidityDays)
          prefs.storeCartOrderInfo(null)
//                    totalCalculation()
          totalCalculationAfterCoupon()
          if (couponCode.isNotEmpty())
            viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
//                    Toasty.warning(requireContext(), "Validity reduced by 1 month.", Toast.LENGTH_SHORT, true).show()
        }
        if (default_validity_months > 1){
          months_validity.setText(default_validity_months.toString())
          prefs.storeCartValidityMonths(default_validity_months.toString())

        }
//                    months_validity.text = default_validity_months.toString() + " months"
        else{
          months_validity.setText(default_validity_months.toString())
          prefs.storeCartValidityMonths(default_validity_months.toString())
        }
//                    months_validity.text = default_validity_months.toString() + " month"
      } else if (bundles_in_cart) {
        if (default_validity_months > package_validity_months) {
          if (default_validity_months > 12) {
//                        default_validity_months = default_validity_months - 12
            if (default_validity_months % 12 == 0) {
              default_validity_months = default_validity_months - 12
            } else {
              default_validity_months = default_validity_months - ((default_validity_months % 12))
            }
          } else {
//                    default_validity_months = default_validity_months - 3
            if (default_validity_months <= 12) {
              if (default_validity_months % 3 == 0) {
                default_validity_months = default_validity_months - 3
              } else if (default_validity_months % 3 == 1) {
                default_validity_months = default_validity_months - 1
              } else if (default_validity_months % 3 == 2) {
                default_validity_months = default_validity_months - 2
              }
            }
          }

          if (default_validity_months < 1) {
            default_validity_months = 1
          }
//                default_validity_months--
          totalValidityDays = 30 * default_validity_months
          prefs.storeMonthsValidity(totalValidityDays)
          prefs.storeCartOrderInfo(null)
//                    totalCalculation()
          totalCalculationAfterCoupon()
          if (couponCode.isNotEmpty())
            viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
//                Toasty.warning(requireContext(), "Validity reduced by 3 month(s).", Toast.LENGTH_SHORT, true).show()
        }
        if (default_validity_months > 1){
          months_validity.setText(default_validity_months.toString())
          prefs.storeCartValidityMonths(default_validity_months.toString())
        }
//                months_validity.text = default_validity_months.toString() + " months"
        else{
          months_validity.setText(default_validity_months.toString())
          prefs.storeCartValidityMonths(default_validity_months.toString())
        }
//                months_validity.text = default_validity_months.toString() + " month"
      }
    }
    Log.v("package_validity_months", " " + package_validity_months)
    months_validity.setFilters(arrayOf<InputFilter>(MinMaxFilter(package_validity_months, 60)))
    months_validity.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
      }

      override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                Log.v("months_validity", " "+ p0)
      }

      override fun afterTextChanged(editable: Editable?) {
        val validity = editable.toString()
        var n = 0
        try {
          n = validity.toInt()
          if (n <= 60) {
            default_validity_months = n
          } else if (n > 60) {
            default_validity_months = 60
//                        months_validity.setText(default_validity_months)
          } else if (n < package_validity_months) {
            n = package_validity_months
            default_validity_months = n
//                        months_validity.setText(package_validity_months)
          }

//                        months_validity.setText(n)
          if (!bundles_in_cart) {
//                            months_validity.setText(default_validity_months.toString())
            totalValidityDays = 30 * default_validity_months
            prefs.storeMonthsValidity(totalValidityDays)
            prefs.storeCartOrderInfo(null)
            totalCalculationAfterCoupon()
            if (couponCode.isNotEmpty())
              viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
            else
              totalCalculationAfterCoupon()
          } else if (bundles_in_cart) {
//                            months_validity.setText(default_validity_months.toString())
            totalValidityDays = 30 * default_validity_months
            prefs.storeMonthsValidity(totalValidityDays)
            prefs.storeCartOrderInfo(null)
            totalCalculationAfterCoupon()
            if (couponCode.isNotEmpty())
              viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as UpgradeActivity).fpid!!), couponCode)
            else
              totalCalculationAfterCoupon()
          }

        } catch (nfe: NumberFormatException) {
          SentryController.captureException(nfe)
        }

      }
    })

  }



  fun speakToExpert(phone: String?) {
    Log.d("callExpertContact", " " + phone)
    if (phone != null) {
      val callIntent = Intent(Intent.ACTION_DIAL)
      callIntent.data = Uri.parse("tel:" + phone)
      startActivity(Intent.createChooser(callIntent, "Call by:"))
    } else {
      Toasty.error(
        requireContext(),
        "Expert will be available tomorrow from 10AM",
        Toast.LENGTH_LONG
      ).show()
    }
  }

  fun showpopup() {
    if (prefs.getInitialLoadMarketPlace()) {

      splashFragment.show(
        (activity as UpgradeActivity).supportFragmentManager,
        Constants.SPLASH_FRAGMENT
      )
    }
  }

  private fun createCartStateRenewal(renewalItems: List<CartModel>?) {
    Log.v("createPurchaseOrder2", " " + renewalItems.toString())
    val widgetList = arrayListOf<com.boost.upgrades.data.renewalcart.Widget>()
    renewalItems?.forEach { widgetList.add(com.boost.upgrades.data.renewalcart.Widget(it.item_id, it.boost_widget_key)) }

    val request = CreateCartStateRequest((activity as UpgradeActivity).clientid, (activity as UpgradeActivity).fpid, "RENEWAL", widgetList)
    viewModel.createCartStateRenewal((activity as? UpgradeActivity)?.getAccessToken()?:"",request)
    viewModel.createCartRenewalResult().observeOnce(Observer { createPurchaseOrder(it.cartStateId) })
  }

  private fun checkRenewalItemDeepLinkClick() {
    val request1 =
      RenewalPurchasedRequest(
        floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
        widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
        dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_MM_DD_YYYY), endDate = DateUtils.getAmountDate((activity as UpgradeActivity).deepLinkDay).parseDate(DateUtils.FORMAT_MM_DD_YYYY)
      )
    Log.v("allPurchasedWidgets", " " + request1)
    val ac = (activity as UpgradeActivity)
    if (ac.isBackCart.not() && (ac.isDeepLink || ac.isOpenCardFragment)) {
      val currentDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_MM_DD_YYYY)
      val deepLinkDay = (activity as UpgradeActivity).deepLinkDay
      val dateAmount = DateUtils.getAmountDate(deepLinkDay).parseDate(DateUtils.FORMAT_MM_DD_YYYY)
      val request = if (deepLinkDay <= -1) {
        RenewalPurchasedRequest(
          floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
          widgetStatus = RenewalPurchasedRequest.WidgetStatus.EXPIRED.name, dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name,
          startDate = dateAmount, endDate = currentDate
        )
      } else {
        RenewalPurchasedRequest(
          floatingPointId = (activity as UpgradeActivity).fpid, clientId = (activity as UpgradeActivity).clientid,
          widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
          dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = currentDate, endDate = dateAmount
        )
      }

      viewModel.allPurchasedWidgets((activity as? UpgradeActivity)?.getAccessToken()?:"",request)
      viewModel.renewalResult().observeOnce(Observer { result ->
        renewalList = result?.filter { it.renewalStatus() == RenewalResult.RenewalStatus.PENDING.name }
          ?: ArrayList()
        renewalList = emptyList()
        if (renewalList.isNotEmpty()) {
          val list = arrayListOf<CartModel>()
          renewalList.forEach { renewal -> list.add(saveRenewalData(renewal)) }
          cartList = list
          total_months_layout.visibility = View.GONE
          renewal_layout.visibility = View.VISIBLE
          addons_layout.visibility = View.GONE
          package_layout.visibility = View.GONE
          updateRenewal(cartList)
//                    totalCalculation()
          totalCalculationAfterCoupon()
        } else {
//                    Toasty.warning(requireContext(), "Renewal order not found").show()
          loadData()
//                    ac.isBackCart = true
//                    (activity as UpgradeActivity).onBackPressed()
        }
      })
    } else loadData()
  }

  private fun saveRenewalData(renewal: RenewalResult): CartModel {
    var minMonth = 1
    if (renewal.expiry?.key == "DAYS") minMonth = renewal.expiry?.valueDays()?.div(30) ?: 1
    return CartModel(
      renewal.widgetId ?: "", renewal.widgetKey ?: "", "",
      renewal.name ?: "", renewal.desc ?: "", renewal.images?.firstOrNull() ?: "",
      renewal.netPrice ?: 0.0, renewal.price ?: 0.0, (renewal.discount ?: 0.0).toInt(),
      1, minMonth, "renewals", null
    )
  }

  fun updateRenewal(renewal: List<CartModel>) {
    cartRenewalAdaptor.renewalNotify(renewal)
    cartRenewalAdaptor.notifyDataSetChanged()
  }

  private fun createPurchaseOrder(cartStateId: String?) {
    Log.v("createPurchaseOrder1", " " + "createPurchaseOrder");
    var couponCode: String? = null
    var couponDiscountPercentage: Int = 0
    if (validCouponCode != null) {
      couponCode = validCouponCode!!.coupon_key
      couponDiscountPercentage = validCouponCode!!.discount_percent
    }
    if (couponServiceModel != null) {
      couponCode = couponServiceModel!!.coupon_key
      couponDiscountPercentage = couponServiceModel!!.couponDiscountAmt!!.toInt()
    }
    val purchaseOrders = ArrayList<PurchaseOrder>()
    val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
    if (renewalItems.isNullOrEmpty().not()) {
      val widgetList = ArrayList<Widget>()
      var netAmount = 0.0
      renewalItems?.forEach { item ->
        val data = renewalList.firstOrNull { it.widgetId == item.item_id }
        netAmount += item.price

        var extendPropsRenew: List<ExtendedProperty>? = null
        var outputExtendedPropsRenew = ArrayList<Property>()

        if (item.extended_properties != null && item.extended_properties!!.length > 0) {
          try {
            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
            extendPropsRenew = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

            if (extendPropsRenew != null) {
              for (prop in extendPropsRenew) {
                if (prop.key != null && prop.value != null) {
                  outputExtendedPropsRenew.add(
                    Property(
                      Key = prop.key,
                      Value = prop.value
                    )
                  )
                }
              }

            }
          } catch (ex: Exception) {
            SentryController.captureException(ex)
            ex.printStackTrace()
            SentryController.captureException(ex)
          }
        }
        val widget = Widget(
          data?.category
            ?: "", ConsumptionConstraint("DAYS", 30), "", item.description_title,
          item.discount, Expiry("MONTHS", default_validity_months), listOf(), true, true, item.item_name
            ?: "",
          item.price, item.MRPPrice,if (outputExtendedPropsRenew.size > 0) outputExtendedPropsRenew else null , 1, "MONTHLY", item.boost_widget_key
            ?: "", item.item_id
        )
        widgetList.add(widget)
      }
      purchaseOrders.add(PurchaseOrder(couponCode, couponDiscountPercentage, null, netAmount, widgetList))
    } else {
      val featureWidgetList = ArrayList<Widget>()
      var featureNetPrice = 0.0
      for (item in cartList) {
//        val widgetList = ArrayList<Widget>()
        val bundleWidgetList = ArrayList<Widget>()
        var extendProps: List<ExtendedProperty>? = null
        var outputExtendedProps = ArrayList<Property>()
        var extraPurchaseOrderDetails: ExtraPurchaseOrderDetails? = null
        var bundleNetPrice = 0.0
        var bundleDiscount = 0

        if (item.extended_properties != null && item.extended_properties!!.length > 0) {
          try {
            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
            extendProps = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

            if (extendProps != null) {
              for (prop in extendProps) {
                if (prop.key != null && prop.value != null) {
                  outputExtendedProps.add(
                    Property(
                      Key = prop.key,
                      Value = prop.value
                    )
                  )
                }
              }

            }
          } catch (ex: Exception) {
            SentryController.captureException(ex)
            ex.printStackTrace()
            SentryController.captureException(ex)
          }
        }

        totalValidityDays = 30 * default_validity_months
        prefs.storeMonthsValidity(totalValidityDays)
        if (item.item_type.equals("features")) {
          var mrp_price = item.MRPPrice
          val discount = 100 - item.discount
          var netPrice = (discount * mrp_price) / 100

          var validity_days = 30
          var net_quantity = 1
//                    if(outputExtendedProps.isNullOrEmpty().not()){
//                        val actualQuantity = outputExtendedProps.find { it.Key =="LIMIT" }?.Value
//                        if (actualQuantity.isNullOrEmpty().not()){
//                            net_quantity = actualQuantity?.toInt()!!
//                        }
//                        else{
//                            net_quantity = 1
//                        }
//                    }
//                    else{
//                        net_quantity = 1
//                    }


          if (!bundles_in_cart && default_validity_months > 1) {
            validity_days = 30 * default_validity_months
            totalValidityDays = validity_days
            Log.v("totalValidityDays", " " + totalValidityDays)
            netPrice = netPrice * default_validity_months
            net_quantity = default_validity_months
            mrp_price = mrp_price * default_validity_months
          }

          //adding widget netprice to featureNetprice to get GrandTotal In netPrice.
          featureNetPrice += netPrice

          featureWidgetList.add(
            Widget(
              "",
              ConsumptionConstraint(
                "DAYS",
                30
              ),
              "",
              item.description_title,
              item.discount,
              Expiry(
                "MONTHS",
                default_validity_months
              ),
              listOf(),
              true,
              true,
              item.item_name!!,
              netPrice,
              mrp_price,
              if (outputExtendedProps.size > 0) outputExtendedProps else null,
              net_quantity,
              "MONTHLY",
              item.boost_widget_key!!,
              item.item_id
            )
          )
        } else if (item.item_type.equals("bundles")) {
          if (::bundlesList.isInitialized && bundlesList.size > 0) {
            for (singleBundle in bundlesList) {
              if (singleBundle.bundle_id.equals(item.item_id)) {
                val outputBundleProps: ArrayList<Property> = arrayListOf()
                outputBundleProps.add(
                  Property(
                    Key = singleBundle.bundle_id,
                    Value = singleBundle.name!!
                  )
                )
                extraPurchaseOrderDetails = ExtraPurchaseOrderDetails(
                  null,
                  singleBundle.primary_image,
                  singleBundle.name,
                  outputBundleProps
                )
                bundleDiscount = singleBundle.overall_discount_percent
                val includedFeatures = Gson().fromJson<List<IncludedFeature>>(singleBundle.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                for (singleIndludedFeature in includedFeatures) {
                  for (singleFeature in featuresList) {
                    if (singleIndludedFeature.feature_code.equals(singleFeature.feature_code)) {

                      val netPrice = (singleFeature.price - ((singleFeature.price * singleIndludedFeature.feature_price_discount_percent) / 100))

                      //adding bundle netPrice
//                      bundleNetPrice += netPrice * singleBundle.min_purchase_months

                      var singleWidgetNetPrice = 0.0
                      singleWidgetNetPrice = (netPrice * singleBundle.min_purchase_months).toDouble()

                      //-----------------------//discount implementation
                      if (bundleDiscount > 0) {
                        singleWidgetNetPrice = Math.round(singleWidgetNetPrice - ((singleWidgetNetPrice * bundleDiscount) / 100)).toDouble()
                      }
                      featureNetPrice += singleWidgetNetPrice

//                      bundleWidgetList.add(Widget(
                      featureWidgetList.add(
                        Widget(
                          "",
                          ConsumptionConstraint(
                            "DAYS",
                            30 * singleBundle.min_purchase_months
                          ),
                          "",
                          singleFeature.description_title,
                          singleIndludedFeature.feature_price_discount_percent,
                          Expiry(
                            "MONTHS", default_validity_months
                          ),
                          listOf(),
                          true,
                          true,
                          singleFeature.name!!,
//                          netPrice.toDouble() * singleBundle.min_purchase_months,
                          singleWidgetNetPrice,
                          singleFeature.price.toDouble() * singleBundle.min_purchase_months,
                          if (outputExtendedProps.size > 0) outputExtendedProps else null,
                          1,
                          "MONTHLY",
                          singleFeature.boost_widget_key,
                          singleFeature.feature_id
                        )
                      )
                      break
                    }
                  }
                }
//                //bundle level discount
//                if (bundleDiscount > 0) {
//                  bundleNetPrice = Math.round(bundleNetPrice - ((bundleNetPrice * bundleDiscount) / 100)).toDouble()
//                }
                break
              }
            } //bundle forloop completion

//            purchaseOrders.add(
//                PurchaseOrder(
//                    couponCode,
//                    bundleDiscount, //Discount of the bundle/package/order without tax.
//                    null, //extraPurchaseOrderDetails,
//                    bundleNetPrice,
//                    bundleWidgetList
//                )
//            )

          }// bundle end
        }//bundle type if end


//        purchaseOrders.add(
//            PurchaseOrder(
//                couponCode,
//                bundleDiscount, //Discount of the bundle/package/order without tax.
//                extraPurchaseOrderDetails,
//                bundleNetPrice,
//                widgetList
//            )
//        )
      }// end of cart item for loop

      purchaseOrders.add(
        PurchaseOrder(
          couponCode,
          couponDiscountPercentage, //showing couponcode percentage
          null,
          featureNetPrice,
          featureWidgetList
        )
      )
    } // if end of new order

    var keysToBeActivated = ArrayList<String>()

    for (item in purchaseOrders) {
      if (item.Widgets != null) {
        for (widget in item.Widgets) {
          if (!keysToBeActivated.contains(widget.WidgetKey)) {
            keysToBeActivated.add(widget.WidgetKey)
          }
        }
      }
    }
    prefs.storeFeatureKeysInLastOrder(keysToBeActivated.toMutableSet())
    prefs.storeFeaturesCountInLastOrder(purchaseOrders.count())

    viewModel.InitiatePurchaseOrder((activity as? UpgradeActivity)?.getAccessToken()?:"",
      CreatePurchaseOrderV2(
        (activity as UpgradeActivity).clientid,
        (activity as UpgradeActivity).fpid!!,
        PaymentDetails(
          "INR",
          couponDiscountPercentage, //[Double] Discount Percentage of the the payment(Coupon code discount)
          "RAZORPAY",
          TaxDetails(
            GSTINNumber,
            0,
            null,
            18
          ),
          grandTotal
        ),
        (if (cartStateId.isNullOrEmpty()) "NEW" else "RENEWAL"),
        purchaseOrders,
        (cartStateId ?: "")
      )
    )
  }

  private fun createPurchaseAutoSubscriptionOrder(cartStateId: String?) {
    Log.v("createPurchaseAuto", " " + "createPurchaseAutoSubscriptionOrder")
    var couponCode: String? = null
    var couponDiscountPercentage: Int = 0
    if (validCouponCode != null) {
      couponCode = validCouponCode!!.coupon_key
      couponDiscountPercentage = validCouponCode!!.discount_percent
    }
    if (couponServiceModel != null) {
      couponCode = couponServiceModel!!.coupon_key
      couponDiscountPercentage = couponServiceModel!!.couponDiscountAmt!!.toInt()
    }
    val purchaseOrders = ArrayList<PurchaseOrder>()
    val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
    if (renewalItems.isNullOrEmpty().not()) {
      val widgetList = ArrayList<Widget>()
      var netAmount = 0.0
      renewalItems?.forEach { item ->
        val data = renewalList.firstOrNull { it.widgetId == item.item_id }
        netAmount += item.price

        var extendProps1: List<ExtendedProperty>? = null
        var outputExtendedProps1 = ArrayList<Property>()

        if (item.extended_properties != null && item.extended_properties!!.length > 0) {
          try {
            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
            extendProps1 = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

            if (extendProps1 != null) {
              for (prop in extendProps1) {
                if (prop.key != null && prop.value != null) {
                  outputExtendedProps1.add(
                    Property(
                      Key = prop.key,
                      Value = prop.value
                    )
                  )
                }
              }

            }
          } catch (ex: Exception) {
            ex.printStackTrace()
            SentryController.captureException(ex)
          }
        }

        val widget = Widget(
          data?.category
            ?: "", ConsumptionConstraint("DAYS", 30), "", item.description_title,
          item.discount, Expiry("MONTHS", default_validity_months), listOf(), true, true, item.item_name
            ?: "",
          item.price, item.MRPPrice,  if (outputExtendedProps1.size > 0) outputExtendedProps1 else null, 1, "MONTHLY", item.boost_widget_key
            ?: "", item.item_id
        )
        widgetList.add(widget)
      }
      purchaseOrders.add(PurchaseOrder(couponCode, couponDiscountPercentage, null, netAmount, widgetList))
    } else {
      val featureWidgetList = ArrayList<Widget>()
      var featureNetPrice = 0.0
      for (item in cartList) {
//        val widgetList = ArrayList<Widget>()
        val bundleWidgetList = ArrayList<Widget>()
        var extendProps: List<ExtendedProperty>? = null
        var outputExtendedProps = ArrayList<Property>()
        var extraPurchaseOrderDetails: ExtraPurchaseOrderDetails? = null
        var bundleNetPrice = 0.0
        var bundleDiscount = 0

        if (item.extended_properties != null && item.extended_properties!!.length > 0) {
          try {
            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
            extendProps = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

            if (extendProps != null) {
              for (prop in extendProps) {
                if (prop.key != null && prop.value != null) {
                  outputExtendedProps.add(
                    Property(
                      Key = prop.key,
                      Value = prop.value
                    )
                  )
                }
              }

            }
          } catch (ex: Exception) {
            SentryController.captureException(ex)
            ex.printStackTrace()
            SentryController.captureException(ex)
          }
        }


        if (item.item_type.equals("features")) {
          var mrp_price = item.MRPPrice
          val discount = 100 - item.discount
          var netPrice = (discount * mrp_price) / 100

          var validity_days = 30
          var net_quantity = 1
//                    if(outputExtendedProps.isNullOrEmpty().not()){
//                        val actualQuantity = outputExtendedProps.find { it.Key =="LIMIT" }?.Value
//                        if (actualQuantity.isNullOrEmpty().not()){
//                            net_quantity = actualQuantity?.toInt()!!
//                        }
//                        else{
//                            net_quantity = 1
//                        }
//                    }
//                    else{
//                        net_quantity = 1
//                    }

          totalValidityDays = 30 * default_validity_months
          prefs.storeMonthsValidity(totalValidityDays)
          if (!bundles_in_cart && default_validity_months > 1) {
            validity_days = 30 * default_validity_months
            totalValidityDays = validity_days
            Log.v("totalValidityDays1", " " + totalValidityDays)
            netPrice = netPrice * default_validity_months
            net_quantity = default_validity_months
            mrp_price = mrp_price * default_validity_months
          }

          //adding widget netprice to featureNetprice to get GrandTotal In netPrice.
          featureNetPrice += netPrice

          featureWidgetList.add(
            Widget(
              "",
              ConsumptionConstraint(
                "DAYS",
                30
              ),
              "",
              item.description_title,
              item.discount,
              Expiry(
                "MONTHS",
                default_validity_months
              ),
              listOf(),
              true,
              true,
              item.item_name!!,
              netPrice,
              mrp_price,
              if (outputExtendedProps.size > 0) outputExtendedProps else null,
              net_quantity,
              "MONTHLY",
              item.boost_widget_key!!,
              item.item_id
            )
          )
        } else if (item.item_type.equals("bundles")) {
          if (::bundlesList.isInitialized && bundlesList.size > 0) {
            for (singleBundle in bundlesList) {
              if (singleBundle.bundle_id.equals(item.item_id)) {
                val outputBundleProps: ArrayList<Property> = arrayListOf()
                outputBundleProps.add(
                  Property(
                    Key = singleBundle.bundle_id,
                    Value = singleBundle.name!!
                  )
                )
                extraPurchaseOrderDetails = ExtraPurchaseOrderDetails(
                  null,
                  singleBundle.primary_image,
                  singleBundle.name,
                  outputBundleProps
                )
                bundleDiscount = singleBundle.overall_discount_percent
                val includedFeatures = Gson().fromJson<List<IncludedFeature>>(singleBundle.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                for (singleIndludedFeature in includedFeatures) {
                  for (singleFeature in featuresList) {
                    if (singleIndludedFeature.feature_code.equals(singleFeature.feature_code)) {

                      val netPrice = (singleFeature.price - ((singleFeature.price * singleIndludedFeature.feature_price_discount_percent) / 100))

                      //adding bundle netPrice
//                      bundleNetPrice += netPrice * singleBundle.min_purchase_months

                      var singleWidgetNetPrice = 0.0
                      singleWidgetNetPrice = (netPrice * singleBundle.min_purchase_months).toDouble()

                      //-----------------------//discount implementation
                      if (bundleDiscount > 0) {
                        singleWidgetNetPrice = Math.round(singleWidgetNetPrice - ((singleWidgetNetPrice * bundleDiscount) / 100)).toDouble()
                      }
                      featureNetPrice += singleWidgetNetPrice

//                      bundleWidgetList.add(Widget(
                      featureWidgetList.add(
                        Widget(
                          "",
                          ConsumptionConstraint(
                            "DAYS",
                            30 * singleBundle.min_purchase_months
                          ),
                          "",
                          singleFeature.description_title,
                          singleIndludedFeature.feature_price_discount_percent,
                          Expiry(
                            "MONTHS", default_validity_months
                          ),
                          listOf(),
                          true,
                          true,
                          singleFeature.name!!,
//                          netPrice.toDouble() * singleBundle.min_purchase_months,
                          singleWidgetNetPrice,
                          singleFeature.price.toDouble() * singleBundle.min_purchase_months,
                          if (outputExtendedProps.size > 0) outputExtendedProps else null,
                          1,
                          "MONTHLY",
                          singleFeature.boost_widget_key,
                          singleFeature.feature_id
                        )
                      )
                      break
                    }
                  }
                }
//                //bundle level discount
//                if (bundleDiscount > 0) {
//                  bundleNetPrice = Math.round(bundleNetPrice - ((bundleNetPrice * bundleDiscount) / 100)).toDouble()
//                }
                break
              }
            } //bundle forloop completion

//            purchaseOrders.add(
//                PurchaseOrder(
//                    couponCode,
//                    bundleDiscount, //Discount of the bundle/package/order without tax.
//                    null, //extraPurchaseOrderDetails,
//                    bundleNetPrice,
//                    bundleWidgetList
//                )
//            )

          }// bundle end
        }//bundle type if end


//        purchaseOrders.add(
//            PurchaseOrder(
//                couponCode,
//                bundleDiscount, //Discount of the bundle/package/order without tax.
//                extraPurchaseOrderDetails,
//                bundleNetPrice,
//                widgetList
//            )
//        )
      }// end of cart item for loop

      purchaseOrders.add(
        PurchaseOrder(
          couponCode,
          couponDiscountPercentage, //showing couponcode percentage
          null,
          featureNetPrice,
          featureWidgetList
        )
      )
    } // if end of new order

    var keysToBeActivated = ArrayList<String>()

    for (item in purchaseOrders) {
      if (item.Widgets != null) {
        for (widget in item.Widgets) {
          if (!keysToBeActivated.contains(widget.WidgetKey)) {
            keysToBeActivated.add(widget.WidgetKey)
          }
        }
      }
    }
    prefs.storeFeatureKeysInLastOrder(keysToBeActivated.toMutableSet())
    prefs.storeFeaturesCountInLastOrder(purchaseOrders.count())

    viewModel.InitiatePurchaseAutoRenewOrder((activity as? UpgradeActivity)?.getAccessToken()?:"",
      CreatePurchaseOrderV2(
        (activity as UpgradeActivity).clientid,
        (activity as UpgradeActivity).fpid!!,
        PaymentDetails(
          "INR",
          couponDiscountPercentage, //[Double] Discount Percentage of the the payment(Coupon code discount)
          "RAZORPAY_SUBSCRIPTION_LINK",
          TaxDetails(
            GSTINNumber,
            0,
            null,
            18
          ),
          grandTotal
        ),
        (if (cartStateId.isNullOrEmpty()) "NEW" else "RENEWAL"),
        purchaseOrders,
        (cartStateId ?: "")
      )
    )
  }

  fun loadData() {
    viewModel.getCartItems()
    viewModel.getAllFeatures()
    viewModel.getAllBundles()
    //load customerID
//        viewModel.requestCustomerId(CreateCustomerInfoRequest(
//                AddressDetails(
//                        null,
//                        "india",
//                        null,
//                        null,
//                        null,
//                        null
//                ),
//                BusinessDetails(
//                        "+91",
//                        null,
//                        null
//                ),
//                (activity as UpgradeActivity).clientid,
//                "+91",
//                "ANDROID",
//                null,
//                (activity as UpgradeActivity).fpTag!!,
//                null,
//                null,
//                com.boost.upgrades.data.api_model.customerId.customerInfo.TaxDetails(
//                        null,
//                        null,
//                        null,
//                        null
//                )
//        ))
  }

  @SuppressLint("FragmentLiveDataObserve")
  fun initMvvM() {
//        viewModel.updateRenewValue("")
    viewModel.cartResult().observe(this, Observer {
      if (it.isNullOrEmpty().not()) {
        cartList = it as ArrayList<CartModel>
        empty_cart.visibility = View.GONE
        cart_main_layout.visibility = View.VISIBLE
        val features = arrayListOf<CartModel>()
        val bundles = arrayListOf<CartModel>()
        for (items in it) {
          if (items.item_type.equals("features")) {
            couponDiwaliRedundant.clear()
            if (items.feature_code.equals("WILDFIRE_FB_LEAD_ADS") || items.feature_code.equals("WILDFIRE") || items.feature_code.equals("DICTATE")) {
              Log.v("couponDiwaliRedundant", " " + items.item_id)
//                            couponDiwaliRedundant.add(items.feature_code)
              couponDiwaliRedundant.put(items.feature_code, items.item_name)
            }
            features.add(items)
          } else if (items.item_type.equals("bundles")) {
            bundles.add(items)
          }
        }
        if (features.size > 0) {
          updateAddons(features)
          addons_layout.visibility = View.VISIBLE
        } else {
          addons_layout.visibility = View.GONE
        }
        Constants.COMPARE_CART_COUNT = bundles.size
        if (bundles.size > 0) {
          bundles_in_cart = true
          updatePackage(bundles)
          for (bundle in bundles) {
            package_validity_months = bundle.min_purchase_months
            if (bundle.min_purchase_months > default_validity_months) {
              default_validity_months = bundle.min_purchase_months
            }
//                        if (bundle.min_purchase_months < default_validity_months)
//                            default_validity_months = default_validity_months
          }
          if (default_validity_months > 0){
            if(prefs.getCartValidityMonths().isNullOrEmpty().not()){
              months_validity.setText(prefs.getCartValidityMonths())
            }else{
              months_validity.setText(default_validity_months.toString())
            }
          }
//                        months_validity.text = default_validity_months.toString() + " months"
          else{
            if(prefs.getCartValidityMonths().isNullOrEmpty().not()){
              months_validity.setText(prefs.getCartValidityMonths())
            }else{
              months_validity.setText(default_validity_months.toString())
            }
          }
//                        months_validity.text = default_validity_months.toString() + " month"
//                    months_validity_edit_inc.visibility = View.GONE
//                    months_validity_edit_dsc.visibility = View.GONE
          package_layout.visibility = View.VISIBLE
        } else {
          bundles_in_cart = false
          default_validity_months = 1
//                    months_validity.text = default_validity_months.toString() + " month"
          if(prefs.getCartValidityMonths().isNullOrEmpty().not()){
            months_validity.setText(prefs.getCartValidityMonths())
          }else{
            months_validity.setText(default_validity_months.toString())
          }
//          months_validity.setText(default_validity_months.toString())
          months_validity_edit_inc.visibility = View.VISIBLE
          months_validity_edit_dsc.visibility = View.VISIBLE
          package_layout.visibility = View.GONE
        }
//                totalCalculation()
        totalCalculationAfterCoupon()
//                var event_attributes: HashMap<String, Double> = HashMap()

        event_attributes.put("cart size", it.size.toDouble())
        cartFullItems.clear()
        it.forEach {
          if (it.boost_widget_key != null) {
            cartFullItems!!.add(it.item_name!!)
          } else {
            cartFullItems!!.add(it.item_name!!)
          }

        }
        event_attributes.put("cart ids", Utils.filterBraces(cartFullItems.toString()))
        Log.v("events", event_attributes.toString())
//                WebEngageController.trackEvent("ADDONS_MARKETPLACE Full_Cart Loaded", event_attributes)
        WebEngageController.trackEvent(event_name = EVENT_NAME_ADDONS_MARKETPLACE_FULL_CART_LOADED, EVENT_LABEL_ADDONS_MARKETPLACE_FULL_CART_LOADED, event_attributes)

      } else {
        WebEngageController.trackEvent(EVENT_NAME_ADDONS_MARKETPLACE_EMPTY_CART_LOADED, EVENT_LABEL_ADDONS_MARKETPLACE_EMPTY_CART_LOADED, NO_EVENT_VALUE)
        empty_cart.visibility = View.VISIBLE
        cart_main_layout.visibility = View.GONE
        Constants.COMPARE_CART_COUNT = 0
        months_validity_edit_inc.visibility = View.GONE
        months_validity_edit_dsc.visibility = View.GONE
//                months_validity.text = "- -"
        months_validity.setText("- -")

        //clear coupon
        validCouponCode = null

        //remove saved orderdetails from prefs
        prefs.storeCartOrderInfo(null)
        prefs.storeApplyedCouponDetails(null)
        prefs.storeCartValidityMonths(null)

        //clear viewModel data
        viewModel.clearValidCouponResult()
      }
    })

    viewModel.getPurchaseOrderResponse().observe(this, Observer {
      if (it != null) {
        prefs.storeLatestPurchaseOrderId(it.Result.OrderId)
        prefs.storeLatestPurchaseOrderTotalPrice(it.Result.TotalPrice.toFloat())

        //original cart amount and coupon discount added to shareprefs
        prefs.storeCartOriginalAmount((total + couponDiscountAmount).toFloat())
        prefs.storeCouponDiscountPercentage(if (validCouponCode == null) 0 else validCouponCode!!.discount_percent)

        //saving cartOrderInfo
        prefs.storeCartOrderInfo(it)

        //store transaction id for cart
        prefs.storeTransactionIdFromCart(it.Result.TransactionId)

        proceedToPayment(it)
      }
    })

//        viewModel.getPurchaseOrderAutoRenewResponse().observe(getViewLifecycleOwner(),  -> {})
    viewModel.getPurchaseOrderAutoRenewResponse().observe(getViewLifecycleOwner(), Observer {
      Log.v("getPurchaseOrderAuto", " " + it)
      if (it != null) {
//                prefs.storeLatestPurchaseOrderId(it.Result.OrderId)
        prefs.storeLatestPurchaseOrderTotalPrice(it.Result.TotalPrice.toFloat())

        //original cart amount and coupon discount added to shareprefs
        prefs.storeCartOriginalAmount((total + couponDiscountAmount).toFloat())
        prefs.storeCouponDiscountPercentage(if (validCouponCode == null) 0 else validCouponCode!!.discount_percent)

        //saving cartOrderInfo
        prefs.storeCartOrderInfo(it)

        //store transaction id for cart
        prefs.storeTransactionIdFromCart(it.Result.TransactionId)

//                proceedToPayment(it)
        proceedToAutoRenewPayment(it)
      }
    })

    viewModel.getLoaderStatus().observe(this, Observer {
      if (it) {
        val status = viewModel.getAPIRequestStatus()
        showProgress(status ?: "Please wait...")
      } else {
        hideProgress()
      }
    })

    viewModel.getGSTIN().observe(this, Observer {
      if (it != null) {
        Log.i("getGSTIN >> ", it)
        GSTINNumber = it
        gstin_layout1.visibility = View.GONE
        gstin_layout2.visibility = View.VISIBLE
        fill_in_gstin_value.text = it

        gstin_remove.visibility = View.VISIBLE
        gstin_title.text = "GSTIN"
        entered_gstin_number.visibility = View.VISIBLE
        enter_gstin_number.visibility = View.GONE
        entered_gstin_number.text = it
      }
    })

    viewModel.getRenewValue().observe(this, Observer {
      if (it != null) {
        Log.i("getRenewValue >> ", it)
        if (it.equals("REMIND_ME")) {
//                if(it.equals("REMIND_ME")  && proceedRenewPopup!!){
          if (prefs.getCartOrderInfo() != null) {
            proceedToPayment(prefs.getCartOrderInfo()!!)
          } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
            val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
            if (renewalItems.isNullOrEmpty().not()) {
              createCartStateRenewal(renewalItems)
            } else createPurchaseOrder(null)
          } else {
            Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
          }
        } else if (it.equals("AUTO_RENEW")) {
//                }else if(it.equals("AUTO_RENEW") && proceedRenewPopup!!){
          viewModel.updateProceedClick(false)
          /* val autoRenewFragment: AutoRenewSubsFragment = AutoRenewSubsFragment.newInstance()
           val args = Bundle()
           args.putString("title", "Auto Renewal Subscription")
           args.putString("link", "https://razorpay.com/demo/")
           autoRenewFragment.arguments = args
           (activity as UpgradeActivity).addFragment(
                   autoRenewFragment,
                   Constants.AUTO_RENEW_FRAGEMENT
           )*/
          if (prefs.getCartOrderInfo() != null) {
//                        proceedToAutoRenewPayment(prefs.getCartOrderInfo()!!)
            createPurchaseAutoSubscriptionOrder(null)
            Log.i("getRenewValue1 >> ", it)
          } else if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized || ::renewalList.isInitialized) {
            val renewalItems = cartList.filter { it.item_type == "renewals" } as? List<CartModel>
            if (renewalItems.isNullOrEmpty().not()) {
              Log.i("getRenewValue2 >> ", it)
              createCartStateRenewal(renewalItems)
            } else {
              createPurchaseAutoSubscriptionOrder(null)
              Log.i("getRenewValue3 >> ", it)
            }
          } else {
            Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
          }
        }
      }
    })

/*        viewModel.getProceedClick().observe(this, Observer {
            if (it != null) {
                Log.i("getTAN >> ", it.toString())
                proceedRenewPopup = it
            }
        })*/
    viewModel.getRenewPopupClick().observe(this, Observer {
      if (it != null) {
        viewModel.updateRenewValue(it)
      }
    })

    viewModel.getTAN().observe(this, Observer {
      if (it != null) {
        Log.i("getTAN >> ", it)
        TANNumber = it
        enter_tan_number.visibility = View.GONE
        entered_tan_number.visibility = View.VISIBLE
        entered_tan_number.text = it

        tan_remove.visibility = View.VISIBLE
        tan_title.text = "TAN"
      }
    })

    //getting all features
    viewModel.updateAllFeaturesResult().observe(this, Observer {
      if (it.isNullOrEmpty().not()) featuresList = it
    })

    //getting all bunles
    viewModel.updateAllBundlesResult().observe(this, Observer {
      if (it.isNullOrEmpty().not()) bundlesList = it
    })

    //getting valid Coupon Code
    viewModel.updateValidCouponResult().observe(this, Observer {
      if (it != null) {
        //clear stored cartOrderInfo
        prefs.storeCartOrderInfo(null)

        //save coupon Details
        prefs.storeApplyedCouponDetails(it)

        validCouponCode = it
        discount_coupon_title.text = validCouponCode!!.coupon_key
        cart_apply_coupon.visibility = View.GONE
        discount_coupon_remove.visibility = View.VISIBLE
//                totalCalculation()
        totalCalculationAfterCoupon()
      } else {
        validCouponCode = null
      }
    })

    viewModel.getCheckoutKycClose().observe(this, Observer {
      proceedCheckoutPopup = it
    })

    viewModel.redeemCouponResult().observe(this, androidx.lifecycle.Observer {
      if (it != null) {
        Log.v("redeemCouponResult", " " + it.coupon_key + " " + it.couponDiscountAmt)
        if (it != null) {
          //clear stored cartOrderInfo
          prefs.storeCartOrderInfo(null)

          //save coupon Details
//                    prefs.storeApplyedCouponDetails(it)

//                    validCouponCode = it
          couponServiceModel = it
          couponCode = it!!.coupon_key!!
          discount_coupon_title.text = it!!.coupon_key
          cart_apply_coupon.visibility = View.GONE
          discount_coupon_remove.visibility = View.VISIBLE
          if (it.success!!) {
            discount_coupon_message.visibility = View.VISIBLE
            discount_coupon_message.text = it.message
          } else {
            discount_coupon_message.visibility = View.VISIBLE
            discount_coupon_message.text = it.message
          }
          totalCalculationAfterCoupon()
        } else {
          validCouponCode = null
        }
      }

    })

    //get customerId
//        viewModel.getCustomerId().observe(this, Observer {
//            if (it != null && it.isNotEmpty()) {
//                customerId = it
//            }
//        })
  }

  @SuppressLint("FragmentLiveDataObserve")
  private fun observeLastPaymentDetails() {
    viewModel.getLastPayDetails().observe(viewLifecycleOwner, {
      if (it != null) {
        prefs.storeLastUsedPaymentMode(it.result?.lastPaymentMethodDetails?.lastPaymentMethod.toString())
      }
    })
  }

  fun updatePackage(features: List<CartModel>) {
    cartPackageAdaptor.addupdates(features)
    cartPackageAdaptor.notifyDataSetChanged()
  }

  fun updateAddons(features: List<CartModel>) {
    cartAddonsAdaptor.addupdates(features)
    cartAddonsAdaptor.notifyDataSetChanged()
  }


  private fun initializePackageRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_package_recycler.apply {
      layoutManager = gridLayoutManager
      cart_package_recycler.adapter = cartPackageAdaptor

    }
  }

  private fun initializeAddonsRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_addons_recycler.apply {
      layoutManager = gridLayoutManager
      cart_addons_recycler.adapter = cartAddonsAdaptor
    }
  }

  private fun initializeRenewalRecycler() {
    val gridLayoutManager = GridLayoutManager(requireContext(), 1)
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    cart_renewal_recycler.apply {
      layoutManager = gridLayoutManager
      adapter = cartRenewalAdaptor
    }
  }

  private fun initializeErrorObserver() {
    viewModel.updatesError().observeOnce(Observer { Toasty.error(requireContext(), it, Toast.LENGTH_SHORT).show() })
  }


  fun totalCalculation() {
    if (::cartList.isInitialized) {
      total = 0.0
      var couponDisount = 0
      if (validCouponCode != null) {
        couponDisount = validCouponCode!!.discount_percent
        coupon_discount_title.text = "Discount(" + couponDisount.toString() + "%)"
      } else {
        coupon_discount_title.text = "Discount"
      }
      if (cartList != null && cartList.size > 0) {
        for (item in cartList) {
          if (!bundles_in_cart && item.item_type.equals("features"))
            total += (item.price * default_validity_months)
          else
            total += item.price
        }
        cart_amount_title.text = "Cart total (" + cartList.size + " items)"
        cart_amount_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total)
        couponDiscountAmount = total * couponDisount / 100
        coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
        total -= couponDiscountAmount
        val temp = (total * 18) / 100
        taxValue = Math.round(temp * 100) / 100.0
        grandTotal = (Math.round((total + taxValue) * 100) / 100.0)
        igst_value.text = "+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue)
//                order_total_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        cart_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        footer_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
      }
    }
  }

  fun totalCalculationAfterCoupon() {
    if (::cartList.isInitialized) {
      total = 0.0
      couponDiscountAmount = 0.0
      var couponDisount = 0
      if (validCouponCode != null) {
        couponDisount = validCouponCode!!.discount_percent
        coupon_discount_title.text = "Discount(" + couponDisount.toString() + "%)"
      } else {
        coupon_discount_title.text = "Discount"
      }
      if (cartList != null && cartList.size > 0) {
        for (item in cartList) {
          if (!bundles_in_cart && item.item_type.equals("features"))
            total += (item.price * default_validity_months)
          else
            total += ((item.price / package_validity_months) * default_validity_months)
        }
        cart_amount_title.text = "Cart total (" + cartList.size + " items)"
        cart_amount_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total)
        coupontotal = total

        if (couponServiceModel != null)
          couponDiscountAmount = couponServiceModel?.couponDiscountAmt!!
        else
          couponServiceModel = null
//                couponDiscountAmount = total * couponDisount / 100
//                couponDiscountAmount = couponServiceModel!!.couponDiscountAmt!!
        coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
        total -= couponDiscountAmount
        Log.v("cart_amount_value", " " + total)
        val temp = (total * 18) / 100
        taxValue = Math.round(temp * 100) / 100.0
        grandTotal = (Math.round((total + taxValue) * 100) / 100.0)
        igst_value.text = "+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue)
//                order_total_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        cart_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
        footer_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)

        val revenue = Math.round(grandTotal * 100).toInt() / 100
        event_attributes.put("total amount", revenue)
        event_attributes.put("cart validity months",default_validity_months)


      }
    }
  }

  override fun deleteCartAddonsItem(itemID: String) {
    viewModel.deleteCartItems(itemID)
    couponDiwaliRedundant.remove(itemID)
    //remove saved orderdetails from prefs
    prefs.storeCartOrderInfo(null)
    prefs.storeCartValidityMonths(null)
  }

  override fun showBundleDetails(itemID: String) {
    var selectedBundle: Bundles? = null
    for (item in bundlesList) {
      if (item.bundle_id == itemID) {
        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
        selectedBundle = Bundles(
          item.bundle_id,
          temp,
          item.min_purchase_months,
          item.name,
          item.overall_discount_percent,
          PrimaryImage(item.primary_image),
          item.target_business_usecase,
          Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
          null,
          item.desc
        )
        break
      }
    }
    if (selectedBundle != null) {
      val packageFragment = PackageFragment.newInstance()
      val args = Bundle()
      args.putString("bundleData", Gson().toJson(selectedBundle))
      args.putBoolean("showCartIcon", false)
      packageFragment.arguments = args
      (activity as UpgradeActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
    } else {
      Toasty.info(requireContext(), "Something went wrong!! Try Later...").show()
    }
  }

  override fun actionClickRenewal(position: Int, renewalResult: CartModel, action: Int) {
    when (action) {
      1 -> {
        if (cartList.size > position) {
          cartList.removeAt(position)
          cartRenewalAdaptor.renewalNotify(cartList)
          cartRenewalAdaptor.notifyDataSetChanged()
//                    totalCalculation()
          totalCalculationAfterCoupon()
          prefs.storeCartOrderInfo(null)
          if (cartList.isEmpty()) {
            empty_cart.visibility = View.VISIBLE
            cart_main_layout.visibility = View.GONE
          }
        }
      }
    }
  }

  fun proceedToPayment(result: CreatePurchaseOrderResponse) {
//        var cartItems: ArrayList<String>? =  null
    WebEngageController.trackEvent(event_name = EVENT_NAME_ADDONS_MARKETPLACE_CART_CONTINUE, NO_EVENT_LABLE, event_attributes)
    Log.v("createPurchaseOrder3", " " + result.toString())
    cartList.forEach {
//            if(it!!.item_id != null) it!!.item_id!! else it.boost_widget_key?.let { it1 -> cartItems?.add(it1) }

      if (it.boost_widget_key != null) {
        cartItems!!.add(it.item_name!!)
      } else {
        cartItems!!.add(it.item_name!!)
      }

//            Log.v("proceedToPayment " , "item_id "+ it.item_id  + " boost "+ it.boost_widget_key + " "+cartItems!!.size)
    }
    val paymentFragment = PaymentFragment.newInstance()
    val args = Bundle()
    args.putString("customerId", customerId)
    args.putDouble("amount", result.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
    args.putString("order_id", result.Result.OrderId)
    args.putString("transaction_id", result.Result.TransactionId)
    args.putString("email", (activity as UpgradeActivity).email)
    args.putString("currency", "INR")
    args.putString("contact", (activity as UpgradeActivity).mobileNo)
    prefs.storeCardIds(cartItems)
    prefs.storeCouponIds(couponCode)
    prefs.storeValidityMonths(default_validity_months.toString())
    paymentFragment.arguments = args
    (activity as UpgradeActivity).addFragment(
      paymentFragment,
      Constants.PAYMENT_FRAGMENT
    )
  }

  fun proceedToAutoRenewPayment(result: CreatePurchaseOrderResponse) {
    val autoRenewFragment: AutoRenewSubsFragment = AutoRenewSubsFragment.newInstance()
    val args = Bundle()
    args.putString("title", "Auto Renewal Subscription")
    args.putString("link", result.Result.TransactionRequestLink)
    Log.v("proceedToAutoRenew", " " + result.Result.TransactionRequestLink)
    autoRenewFragment.arguments = args
    (activity as UpgradeActivity).addFragment(
      autoRenewFragment,
      Constants.AUTO_RENEW_FRAGEMENT
    )
  }

  fun isRenewalListNotEmpty(): Boolean {
    return ::cartList.isInitialized && cartList.isNotEmpty() && ::renewalList.isInitialized && renewalList.isNotEmpty()
  }

  private fun showProgress(message: String = "Please wait...") {
    try {
      if (!progressDialog.isShowing) {
        progressDialog.setMessage(message)
        progressDialog.setCancelable(false)
        progressDialog.show()
      }
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
      SentryController.captureException(e)
    }
  }

  private fun hideProgress() {
    try {
//      if (progressDialog.isShowing) progressDialog.hide()
      if (progressDialog.isShowing) progressDialog.cancel()
    } catch (e: Exception) {
      SentryController.captureException(e)
      e.printStackTrace()
      SentryController.captureException(e)
    }
  }

  private fun loadLastUsedPayData(){
    viewModel.getLastUsedPaymentDetails(
      (activity as? UpgradeActivity)?.getAccessToken()?:"",
      (activity as UpgradeActivity).fpid!!,
      (activity as UpgradeActivity).clientid
    )
  }
}
