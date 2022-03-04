package com.boost.cart.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.InputFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.cart.adapter.*
import com.boost.cart.base_class.BaseFragment
import com.boost.cart.interfaces.ApplyCouponListener
import com.boost.cart.interfaces.CartFragmentListener
import com.boost.cart.ui.autorenew.AutoRenewSubsFragment
import com.boost.cart.ui.checkoutkyc.CheckoutKycFragment
import com.boost.cart.ui.compare.ComparePackageFragment
import com.boost.cart.ui.packages.PackageFragment
import com.boost.cart.ui.popup.CouponPopUpFragment
import com.boost.cart.ui.popup.GSTINPopUpFragment
import com.boost.cart.ui.popup.RenewalPopUpFragment
import com.boost.cart.ui.popup.TANPopUpFragment
import com.boost.cart.ui.splash.SplashFragment
import com.boost.cart.ui.webview.WebViewFragment
import com.boost.cart.utils.*
import com.boost.cart.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import com.boost.cart.utils.Constants.Companion.SCHEMA_ID
import com.boost.cart.utils.Constants.Companion.WEBSITE_ID
import com.boost.cart.utils.DateUtils.parseDate
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.ExtendedProperty
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.requestV2.*
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.dbcenterapi.data.api_model.cart.RecommendedAddonsRequest
import com.boost.dbcenterapi.data.api_model.couponRequest.BulkPropertySegment
import com.boost.dbcenterapi.data.api_model.couponRequest.CouponRequest
import com.boost.dbcenterapi.data.api_model.couponRequest.ObjectKeys
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.AddressDetails
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.BusinessDetails
import com.boost.dbcenterapi.data.api_model.customerId.customerInfo.CreateCustomerInfoRequest
import com.boost.dbcenterapi.data.api_model.customerId.get.Result
import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data
import com.boost.dbcenterapi.data.model.coupon.CouponServiceModel
import com.boost.dbcenterapi.data.renewalcart.CreateCartStateRequest
import com.boost.dbcenterapi.data.renewalcart.RenewalPurchasedRequest
import com.boost.dbcenterapi.data.renewalcart.RenewalResult
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.CouponsModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.payment.PaymentActivity
import com.boost.payment.utils.observeOnce
import com.framework.analytics.SentryController
import com.framework.extensions.underlineText
import com.framework.firebaseUtils.firestore.marketplaceCart.CartFirestoreManager
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.webengageconstant.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.billing_details_layout.*
import kotlinx.android.synthetic.main.cart_applied_coupon_layout.*
import kotlinx.android.synthetic.main.cart_v2_fragment.*
import java.text.NumberFormat
import java.util.*


class CartFragment : BaseFragment(), CartFragmentListener, ApplyCouponListener {

    lateinit var root: View

//  lateinit var localStorage: LocalStorage

    private var couponData = ArrayList<Data>()
    private var couponDataNo = ArrayList<Data>()


    var customerId: String = ""

    lateinit var cartList: ArrayList<CartModel>

    lateinit var featuresList: List<FeaturesModel>

    lateinit var bundlesList: List<BundlesModel>

    lateinit var renewalList: List<RenewalResult>

    var bundles_in_cart = false
    var default_validity_months = 1
    var package_validity_months = 1

    var total = 0.0
    var overalltotal = 0.0

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
    lateinit var packageViewPagerAdapter: PackageViewPagerAdapter

    lateinit var upgradeAdapter: UpgradeAdapter

    val couponPopUpFragment = CouponPopUpFragment()

    val gstinPopUpFragment = GSTINPopUpFragment()

    val tanPopUpFragment = TANPopUpFragment()

    val renewPopUpFragment = RenewalPopUpFragment()

    val splashFragment = SplashFragment()

    val WebViewFragment = WebViewFragment()

    val recommendedAddonsWidgetKey: ArrayList<String> = arrayListOf()

    //    var couponDiwaliRedundant : MutableList<String?> = java.util.ArrayList()
    var couponDiwaliRedundant: HashMap<String?, String?> = HashMap<String?, String?>()

    lateinit var prefs: SharedPrefs
    var totalValidityDays = 0
    val checkoutKycFragment = CheckoutKycFragment()

    var proceedCheckoutPopup: Boolean? = false

    var couponCode: String = ""
//    var couponCodeForApplyCoupon: String = ""


    private var cartItems = ArrayList<String>()

    private var cartFullItems = ArrayList<String>()

    private var event_attributes: HashMap<String, Any> = HashMap()

    var upgradeList = arrayListOf<Bundles>()


    private var setStates: String? = null
    private var setStateTin: String? = null
    val stateFragment = com.boost.cart.ui.popup.StateListPopFragment()

    private var session: UserSessionManager? = null

    private var gstInfoResult: com.boost.dbcenterapi.data.api_model.gst.Result? = null

    private var isGstApiCalled: Boolean = false

    var paymentProceedFlag = true

    var link: String? = null


    companion object {
        fun newInstance() = CartFragment()
    }

    private lateinit var viewModel: CartViewModel
    var createCustomerInfoRequest: Result? = null
    var customerInfoState = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.cart_v2_fragment, container, false)
//    localStorage = LocalStorage.getInstance(requireContext())!!

        progressDialog = ProgressDialog(requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.setStatusBarColor(getResources().getColor(R.color.common_text_color))
        }


        cartPackageAdaptor = CartPackageAdaptor(ArrayList(), this, ArrayList(), requireActivity().application)
        cartAddonsAdaptor = CartAddonsAdaptor(ArrayList(), this)
        cartRenewalAdaptor = CartRenewalAdaptor(ArrayList(), this)
        packageViewPagerAdapter = PackageViewPagerAdapter(ArrayList())
        upgradeAdapter = UpgradeAdapter(ArrayList())
        prefs = SharedPrefs(activity as CartActivity)
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
//    val list = arrayListOf<Bundles>()
        prefs.storeCompareState(1)
//        showpopup()
        loadLastUsedPayData()
        initializePackageRecycler()
        initializeAddonsRecycler()
        initializeRenewalRecycler()
        initializeErrorObserver()
        initializePackageViewPager()
        loadOfferCoupons()
        //updatePackageViewPager(list)
        initMvvM()
        observeLastPaymentDetails()
        checkRenewalItemDeepLinkClick()
        gst_layout.visibility = View.VISIBLE
        discount_banner.visibility = View.GONE
        //show applyed coupon code
        if (prefs.getApplyedCouponDetails() != null) {
            validCouponCode = prefs.getApplyedCouponDetails()
            // discount_coupon_title.text = validCouponCode!!.coupon_key
            cart_coupon_discount_title.text = validCouponCode!!.coupon_key
            cart_apply_coupon.visibility = View.GONE
            cart_coupon_code_rv.visibility = View.GONE
            tv_Show_less.visibility = View.GONE
            tv_Show_more.visibility = GONE
            cart_discount_coupon_remove.visibility = View.VISIBLE
        } else {
            validCouponCode = null
//      discount_coupon_remove.visibility = View.GONE
            cart_apply_coupon.visibility = View.VISIBLE
            cart_coupon_code_rv.visibility = View.VISIBLE

            if (tv_Show_less.visibility == VISIBLE) {
                tv_Show_more.visibility = GONE
            } else {
                tv_Show_more.visibility = VISIBLE

            }
            coupon_discount_title.text = "Discount coupon"
        }
        cart_applied_coupon_full_layout.visibility = View.GONE

        feature_validity.text = "1 Month"

        session = UserSessionManager(requireActivity())
        loadCustomerInfo()
        initMvvm1()
        viewModel.getCitiesFromAssetJson(requireActivity())
        viewModel.getStatesFromAssetJson(requireActivity())

////
//      if(gstcheck1.visibility == View.VISIBLE){
//        updateVisibility()
//      }else if(gstcheck.visibility == View.VISIBLE){
//        reverseVisibility()
//      }

        refund_policy.underlineText(refund_policy.text.length - 12, refund_policy.text.length - 1)
        view_details.underlineText(0, view_details.text.length - 1)

        gst_info.setOnClickListener {
            showPopupWindow(it)
        }

        business_gstin_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (business_gstin_number.hasFocus()) {
                    var enteredText: String = s.toString()
                    if (enteredText.length == 15) {
                        showProgress()
                        callGSTApi(enteredText)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })



        cart_payment_details_ll.setOnClickListener {
            //  bill_details.visibility=View.GONE
            if (bill_details.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(cart_payment_details_ll, AutoTransition())
                bill_details.visibility = View.VISIBLE
                arrow1?.animate()?.rotation(0f)?.start()
                cart_main_scroller.post {
                    cart_main_scroller.fullScroll(View.FOCUS_DOWN)
                }
            } else {
                TransitionManager.beginDelayedTransition(cart_payment_details_ll, AutoTransition())
                bill_details?.visibility = View.GONE
                arrow1?.animate()?.rotation(180f)?.start()
                cart_main_scroller.post {
                    cart_main_scroller.fullScroll(View.FOCUS_DOWN)
                }
            }
        }


        edit.setOnClickListener {
            cart_billing_details_edit_layout.visibility = View.VISIBLE
            billing_details.visibility = View.VISIBLE
            edit.visibility = View.GONE
            billing_details.visibility = View.GONE
            prefs.storeCartOrderInfo(null)
        }
        cart_place_of_supply_cl.setOnClickListener {
            val args = Bundle()
            args.putString("state", setStates)
            args.putString("stateTin", setStateTin)
            stateFragment.arguments = args
            stateFragment.show(
                    (activity as CartActivity).supportFragmentManager,
                    com.boost.cart.utils.Constants.STATE_LIST_FRAGMENT1
            )
        }


        gstcheck.setOnClickListener {
            if (gstcheck.isChecked) {
                gstll.visibility = View.VISIBLE
                business_gstin_number.visibility = View.VISIBLE
                gst_info_tv.visibility = View.VISIBLE
                cart_business_address.visibility = View.GONE
                cart_business_address1.visibility = View.VISIBLE
                cart_state_of_supply.visibility = View.GONE
                cart_place_of_supply_cl.visibility = View.GONE
                cart_place_of_supply_cl1.visibility = View.GONE
                prefs.storeGstRegistered(true)
            } else {
                gstll.visibility = View.GONE
                business_gstin_number.visibility = View.GONE
                gst_info_tv.visibility = View.GONE
                cart_business_address.visibility = View.VISIBLE
                cart_business_address1.visibility = View.GONE
                cart_state_of_supply.visibility = View.VISIBLE
                cart_place_of_supply_cl.visibility = View.VISIBLE
                cart_place_of_supply_cl1.visibility = View.GONE
                prefs.storeGstRegistered(false)

            }
        }

//    if (!prefs.getGstRegistered()) {
//      gstFlag = false
//      gstcheck1.visibility = View.GONE
//      gstcheck.visibility = View.VISIBLE
//      gst_info_tv.visibility = View.GONE
//      business_gstin_number.visibility = View.GONE
//      reverseVisibility()
//    } else {
//      gstFlag = true
//      gstcheck1.visibility = View.VISIBLE
//      gstcheck.visibility = View.GONE
//      gst_info_tv.visibility = View.VISIBLE
//      business_gstin_number.visibility = View.VISIBLE
//      updateVisibility()
//    }


//    gstcheck1.setOnClickListener {
////      if (gstcheck1.isChecked) {
//      gstcheck1.visibility=View.GONE
//      gstcheck.visibility=View.VISIBLE
//        gstll.visibility=View.GONE
//        business_gstin_number.visibility=View.GONE
//        gst_info_tv.visibility=View.GONE
//       // prefs.storeGstRegistered(true)
//        reverseVisibility()
//      }
//      else
//      {
//        gstll.visibility=View.GONE
//        business_gstin_number.visibility=View.GONE
//        gst_info_tv.visibility=View.GONE
//        prefs.storeGstRegistered(false)
//
//      }
        // }

//    gstcheck.setOnClickListener {
//      if (gstcheck.isChecked==false){
//        gstcheck.visibility=View.GONE
//        gstcheck1.visibility=View.VISIBLE
//        gstll.visibility=View.VISIBLE
//        business_gstin_number.visibility=View.VISIBLE
//        gst_info_tv.visibility=View.VISIBLE
//        // prefs.storeGstRegistered(true)
//        reverseVisibility()
//      }
//      else
//      {
//        gstcheck1.visibility=View.GONE
//        gstcheck.visibility=View.VISIBLE
//        gstll.visibility=View.GONE
//        business_gstin_number.visibility=View.GONE
//        gst_info_tv.visibility=View.GONE
//        updateVisibility()
//
//
//       }
//    }
//
//    gstcheck1.setOnClickListener {
//      if (gstcheck1.isChecked==true){
//        gstcheck.visibility=View.VISIBLE
//        gstcheck1.visibility=View.GONE
//        gstll.visibility=View.GONE
//        business_gstin_number.visibility=View.GONE
//        gst_info_tv.visibility=View.GONE
//        // prefs.storeGstRegistered(true)
//        updateVisibility()
//      }
//      else
//      {
//        gstcheck1.visibility=View.VISIBLE
//        gstcheck.visibility=View.GONE
//        gstll.visibility=View.VISIBLE
//        business_gstin_number.visibility=View.VISIBLE
//        gst_info_tv.visibility=View.VISIBLE
//        reverseVisibility()
//
//
//      }
//    }


//    gstcheck.setOnClickListener {
////      prefs.storeGstRegistered(true)
////      gstFlag = true
//      gstcheck.visibility = View.GONE
//      gstcheck1.visibility = View.VISIBLE
//      gstll.visibility=View.VISIBLE
//      business_gstin_number.visibility=View.VISIBLE
//      gst_info_tv.visibility=View.VISIBLE
//
////      gst_info_tv.visibility = View.VISIBLE
//      updateVisibility()
//    }


        cart_discount_coupon_remove.setOnClickListener {
            cart_applied_coupon_full_layout.visibility = View.GONE
            cart_coupon_code_rv.visibility = View.VISIBLE
            if (tv_Show_less.visibility == VISIBLE) {
                tv_Show_more.visibility = View.GONE

            } else {
                tv_Show_more.visibility = View.VISIBLE

            }
            discount_banner.visibility = View.GONE
            cart_apply_coupon.visibility = View.VISIBLE
            //    discount_coupon_title.text = "Discount coupon"
            //     discount_coupon_message.visibility = View.GONE
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

        cart_continue_submit1.setOnClickListener {

//            if (prefs.getInitialLoadMarketPlace() && proceedCheckoutPopup == false) {
//
//                checkoutKycFragment.show(
//                        (activity as CartActivity).supportFragmentManager,
//                        CHECKOUT_KYC_FRAGMENT
//                )
//            }else{
            /*       renewPopUpFragment.show(
                   (activity as CartActivity).supportFragmentManager,
                   RENEW_POPUP_FRAGEMENT
           )*/
            if (TextUtils.isEmpty(months_validity.text.toString())) {
                months_validity.setBackgroundResource(R.drawable.et_validity_error)
                Toasty.error(requireContext(), "Validity is empty", Toast.LENGTH_SHORT).show()
            } else if (bundles_in_cart && months_validity.text.toString().split(" ").get(0).toInt() < package_validity_months) {
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
                    } else {
                        if (validateAgreement()) {
                            createPurchaseOrder(null)
                        }
                    }
                } else {
                    Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
                }
            }

//            }

/*            renewPopUpFragment.show(
                    (activity as CartActivity).supportFragmentManager,
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


//      if(gstcheck.visibility == View.VISIBLE && gstcheck1.visibility == View.GONE){
//        prefs.storeGstRegistered(true)
//      }else{
//        prefs.storeGstRegistered(false)
//      }
            if (progress_bar.visibility == View.GONE) {
                if (validateAgreement()) {
                    if (!customerInfoState) { //no customer available
                        //create customer payment profile
                        viewModel.createCustomerInfo(
                                (activity as? CartActivity)?.getAccessToken() ?: "",
                                CreateCustomerInfoRequest(
                                        AddressDetails(
                                                if (cart_business_city_name.text.isEmpty() == true) null else cart_business_city_name.text.toString(),
                                                "india",
                                                if (cart_business_address.text?.isEmpty() == true) null else cart_business_address.text.toString(),
                                                null,
                                                if (cart_business_city_name.text.isEmpty() == true) null else cart_business_city_name.text.toString(),
                                                null
                                        ),
                                        BusinessDetails(
                                                null,
                                                null,
                                                null
                                        ),

                                        (activity as CartActivity).clientid,
                                        "+91",
                                        "ANDROID",
                                        "",
                                        (activity as CartActivity).fpid!!,
                                        null,
                                        null,
                                        com.boost.dbcenterapi.data.api_model.customerId.customerInfo.TaxDetails(
                                                if (business_gstin_number.text?.isEmpty() == true) null else business_gstin_number.text.toString(),
                                                null,
                                                null,
                                                null
                                        )

                                )
                        )
                    } else {
                        //update customer payment profile
                        if (cart_place_of_supply_cl1.visibility == View.GONE && cart_business_address1.visibility == View.GONE) {
                            viewModel.updateCustomerInfo(
                                    (activity as? CartActivity)?.getAccessToken() ?: "",
                                    CreateCustomerInfoRequest(
                                            AddressDetails(
                                                    if (cart_business_city_name.text.isEmpty()) null else cart_business_city_name.text.toString(),
                                                    "india",
                                                    if (cart_business_address.text?.isEmpty() == true) null else cart_business_address.text.toString(),
                                                    null,
                                                    if (cart_business_city_name.text.isEmpty()) null else cart_business_city_name.text.toString(),
                                                    null
                                            ),
                                            BusinessDetails(
                                                    null,
                                                    null,
                                                    null
                                            ),
                                            (activity as CartActivity).clientid,
                                            null,
                                            null,
                                            null,
                                            (activity as CartActivity).fpid,
                                            null,
                                            null,
                                            com.boost.dbcenterapi.data.api_model.customerId.customerInfo.TaxDetails(
                                                    if (business_gstin_number.text?.isEmpty() == true) null else business_gstin_number.text.toString(),
                                                    null,
                                                    null,
                                                    null
                                            )

                                    )
                            )
                        } else if (isGstApiCalled) {
                            viewModel.updateCustomerInfo(
                                    (activity as? CartActivity)?.getAccessToken() ?: "",
                                    CreateCustomerInfoRequest(
                                            AddressDetails(
                                                    gstInfoResult?.address?.city,
                                                    "india",
                                                    cart_business_address1.text.toString(),
                                                    null,
                                                    gstInfoResult?.address?.state,
                                                    gstInfoResult?.address?.pincode
                                            ),
                                            BusinessDetails(
                                                    null,
                                                    null,
                                                    null
                                            ),
                                            (activity as CartActivity).clientid,
                                            null,
                                            null,
                                            null,
                                            (activity as CartActivity).fpid,
                                            null,
                                            null,
                                            com.boost.dbcenterapi.data.api_model.customerId.customerInfo.TaxDetails(
                                                    if (business_gstin_number.text?.isEmpty() == true) null else business_gstin_number.text.toString(),
                                                    null,
                                                    null,
                                                    null
                                            )

                                    )
                            )
                        } else {
                            viewModel.updateCustomerInfo(
                                    (activity as? CartActivity)?.getAccessToken() ?: "",
                                    CreateCustomerInfoRequest(
                                            AddressDetails(
                                                    if (cart_business_city_name.text.isEmpty()) null else cart_business_city_name.text.toString(),
                                                    "india",
                                                    cart_business_address1.text.toString(),
                                                    null,
                                                    if (cart_business_city_name.text.isEmpty()) null else cart_business_city_name.text.toString(),
                                                    null
                                            ),
                                            BusinessDetails(
                                                    null,
                                                    null,
                                                    null
                                            ),
                                            (activity as CartActivity).clientid,
                                            null,
                                            null,
                                            null,
                                            (activity as CartActivity).fpid,
                                            null,
                                            null,
                                            com.boost.dbcenterapi.data.api_model.customerId.customerInfo.TaxDetails(
                                                    if (business_gstin_number.text?.isEmpty() == true) null else business_gstin_number.text.toString(),
                                                    null,
                                                    null,
                                                    null
                                            )

                                    )
                            )
                        }

                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Please wait while data is loading", Toast.LENGTH_SHORT).show()
            }

        }



        back_button12.setOnClickListener {
            WebEngageController.trackEvent(ADDONS_MARKETPLACE_CART_BACK, NO_EVENT_LABLE, event_attributes)
            (activity as CartActivity).onBackPressed()
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
                    (activity as CartActivity).supportFragmentManager,
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
                        (activity as CartActivity).supportFragmentManager,
                        COUPON_POPUP_FRAGEMENT
                )*/
                val args = Bundle()
//                args.putDouble("cartValue", grandTotal)
                args.putDouble("cartValue", total)
                couponPopUpFragment.arguments = args
                couponPopUpFragment.show(
                        (activity as CartActivity).supportFragmentManager,
                        COUPON_POPUP_FRAGEMENT
                )
            }
            /*couponPopUpFragment.show(
                    (activity as CartActivity).supportFragmentManager,
                    COUPON_POPUP_FRAGEMENT
            )*/
        }

        mp_cart_compare_packs.setOnClickListener {
            val args = Bundle()
            args.putStringArrayList(
                    "userPurchsedWidgets",
                    arguments?.getStringArrayList("userPurchsedWidgets")
            )
            (activity as CartActivity).addFragmentHome(
                    ComparePackageFragment.newInstance(),
                    Constants.COMPARE_FRAGMENT, args
            )
        }
        cart_spk_to_expert.setOnClickListener {
            speakToExpert(prefs.getExpertContact())
        }
//    enter_gst_number.setOnClickListener {
//      gstinPopUpFragment.show(
//        (activity as CartActivity).supportFragmentManager,
//        GSTIN_POPUP_FRAGEMENT
//      )
//    }

//    enter_gstin_number.setOnClickListener {
//      gstinPopUpFragment.show(
//        (activity as CartActivity).supportFragmentManager,
//        GSTIN_POPUP_FRAGEMENT
//      )
//    }

//    gstin_remove.setOnClickListener {
//      GSTINNumber = null
//      gstin_title.text = "GSTIN (optional)"
//      entered_gstin_number.visibility = View.GONE
//      gstin_remove.visibility = View.GONE
//      enter_gstin_number.visibility = View.VISIBLE
//    }

//    tan_remove.setOnClickListener {
//      TANNumber = null
//      tan_title.text = "TAN (optional)"
//      entered_tan_number.visibility = View.GONE
//      tan_remove.visibility = View.GONE
//      enter_tan_number.visibility = View.VISIBLE
//    }

//    remove_gstin_number.setOnClickListener {
//      GSTINNumber = null
//      gstin_layout1.visibility = View.VISIBLE
//      gstin_layout2.visibility = View.GONE
//      fill_in_gstin_value.text = ""
//    }

//    enter_tan_number.setOnClickListener {
//      tanPopUpFragment.show(
//        (activity as CartActivity).supportFragmentManager,
//        TAN_POPUP_FRAGEMENT
//      )
//    }
//    entered_tan_number.setOnClickListener {
//      tanPopUpFragment.show(
//        (activity as CartActivity).supportFragmentManager,
//        TAN_POPUP_FRAGEMENT
//      )
//    }

//    all_recommended_addons.setOnClickListener {
//      (activity as CartActivity).goBackToRecommentedScreen()
//    }

        totalValidityDays = 30 * 1
        prefs.storeMonthsValidity(totalValidityDays)
        months_validity_edit_inc.setOnClickListener {
            if (!bundles_in_cart) {
//                if (default_validity_months < 12){
//                    default_validity_months++
                if (default_validity_months == 1) {
                    default_validity_months = default_validity_months + 2
                } else if (default_validity_months >= 12 && default_validity_months < 36) {
                    if (default_validity_months % 12 == 0) {
                        default_validity_months = default_validity_months + 12
                    } else {
                        default_validity_months = default_validity_months + ((12 - default_validity_months % 12))
                    }
//        default_validity_months = default_validity_months+ 12
                } else {
                    if (default_validity_months < 36) {
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
                if(default_validity_months >1){
                    months_validity.setText(default_validity_months.toString() + " months")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    totalValidityDays = 30 * default_validity_months
                    prefs.storeMonthsValidity(totalValidityDays)
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
                }else{
                    months_validity.setText(default_validity_months.toString() + " month")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    totalValidityDays = 30 * default_validity_months
                    prefs.storeMonthsValidity(totalValidityDays)
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Month"
                }
//                months_validity.text = default_validity_months.toString() + " months"

                prefs.storeCartOrderInfo(null)
//                totalCalculation()
                totalCalculationAfterCoupon()
                Log.v("cart_amount_value1", " " + total)
                if (couponCode.isNotEmpty())
                    viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
                else
                    totalCalculationAfterCoupon()
//                Toasty.success(requireContext(), "Validity increased by 1 month.", Toast.LENGTH_SHORT, true).show()
//            }
            } else if (bundles_in_cart) {
//                if (default_validity_months < 12){
                if (default_validity_months == 1) {
                    default_validity_months = default_validity_months + 2
//    }else if(default_validity_months % 12 == 0 && default_validity_months < 60){
                } else if (default_validity_months >= 12 && default_validity_months < 36) {
                    if (default_validity_months % 12 == 0) {
                        default_validity_months = default_validity_months + 12
                    } else {
//            Log.v("validity_calculator", " "+ (default_validity_months - default_validity_months % 12)  + " "+default_validity_months + " "+ (default_validity_months % 12) )
                        default_validity_months = default_validity_months + ((12 - default_validity_months % 12))
                    }
//        default_validity_months = default_validity_months+ 12
                } else {
//        if(default_validity_months < 60)
                    if (default_validity_months < 36 && default_validity_months < 12) {
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

//            default_validity_months
                if(default_validity_months > 1){
                    months_validity.setText(default_validity_months.toString() + " months")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    totalValidityDays = 30 * default_validity_months
                    prefs.storeMonthsValidity(totalValidityDays)
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
                }else{
                    months_validity.setText(default_validity_months.toString() + " month")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    totalValidityDays = 30 * default_validity_months
                    prefs.storeMonthsValidity(totalValidityDays)
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Month"
                }

                prefs.storeCartOrderInfo(null)
//                totalCalculation()
                totalCalculationAfterCoupon()
                Log.v("cart_amount_value1", " " + total)
                if (couponCode.isNotEmpty())
                    viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
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
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
//                    totalCalculation()
                    totalCalculationAfterCoupon()
                    if (couponCode.isNotEmpty())
                        viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
//                    Toasty.warning(requireContext(), "Validity reduced by 1 month.", Toast.LENGTH_SHORT, true).show()
                }
                if (default_validity_months > 1) {
                    months_validity.setText(default_validity_months.toString() + " months")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
                } else {
                    months_validity.setText(default_validity_months.toString() + " month")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Month"
                }
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
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
//                    totalCalculation()
                    totalCalculationAfterCoupon()
                    if (couponCode.isNotEmpty())
                        viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
//                Toasty.warning(requireContext(), "Validity reduced by 3 month(s).", Toast.LENGTH_SHORT, true).show()
                }
                if (default_validity_months > 1) {
                    months_validity.setText(default_validity_months.toString() + " months")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Months"
                } else {
                    months_validity.setText(default_validity_months.toString() + " month")
                    prefs.storeCartValidityMonths(default_validity_months.toString())
                    feature_validity.text = ((totalValidityDays / 30).toString()) + " Month"
                }
            }
        }
        Log.v("package_validity_months", " " + package_validity_months)
        months_validity.setFilters(arrayOf<InputFilter>(MinMaxFilter(package_validity_months, 36)))
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
                    if (n <= 36) {
                        default_validity_months = n
                    } else if (n > 36) {
                        default_validity_months = 36
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
                            viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
                        else
                            totalCalculationAfterCoupon()
                    } else if (bundles_in_cart) {
//                            months_validity.setText(default_validity_months.toString())
                        totalValidityDays = 30 * default_validity_months
                        prefs.storeMonthsValidity(totalValidityDays)
                        prefs.storeCartOrderInfo(null)
                        totalCalculationAfterCoupon()
                        if (couponCode.isNotEmpty())
                            viewModel.getCouponRedeem(RedeemCouponRequest(coupontotal, couponCode, (activity as CartActivity).fpid!!), couponCode)
                        else
                            totalCalculationAfterCoupon()
                    }

                } catch (nfe: NumberFormatException) {
                    SentryController.captureException(nfe)
                }

            }
        })

        refund_policy.setOnClickListener {
            (activity as CartActivity).addFragment(WebViewFragment, Constants.WEB_VIEW_FRAGMENT)
        }

    }


    private fun validateAgreement(): Boolean {
        if (cart_business_city_name.text.toString().isEmpty()
                || cart_business_address.text.toString().isEmpty()
                || business_gstin_number.text.toString().isEmpty()) {
//      Log.v("business_name_value", " " + business_name_value.text.toString())
//            Toasty.error(requireContext(), "Fields are Empty!!", Toast.LENGTH_LONG).show()
            if (gstcheck.isChecked && !Utils.isValidGSTIN(business_gstin_number.text.toString())) {
                business_gstin_number.setBackgroundResource(R.drawable.et_validity_error)
                cart_main_scroller.post {
                    cart_main_scroller.scrollTo(
                            0,
                            gst_layout.getBottom()
                    )
                }
                Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
                return false
            } else {
                business_gstin_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
            }

//      if (business_contact_number.text!!.isEmpty()) {
//        business_contact_number.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//        Toasty.error(requireContext(), "Please enter Mobile no.", Toast.LENGTH_LONG).show()
//        return false
//      } else {
//        if (!com.boost.payment.utils.Utils.isValidMobile(business_contact_number.text.toString())) {
//          business_contact_number.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//          Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
//            .show()
//          return false
//        } else {
////          business_contact_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
//        }
//      }
//
//      Log.v("business_name_value1", " " + business_contact_number.text.toString())
//
//
//      Log.v("business_name_value2", " " + business_email_address.text.toString())
//      if (business_email_address.text.isEmpty()) {
//        business_email_address.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//        Toasty.error(requireContext(), "Please enter Email ID", Toast.LENGTH_LONG).show()
//        return false
//      } else {
//        if (!com.boost.payment.utils.Utils.isValidMail(business_email_address.text.toString())) {
//          business_email_address.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//          Toasty.error(requireContext(), "Entered Email ID is not valid!!", Toast.LENGTH_LONG)
//            .show()
//          return false
//        } else {
//          business_email_address.setBackgroundResource(com.boost.payment.R.drawable.rounded_edit_fill_kyc)
//        }
//      }


            //  Log.v("business_name_value3", " " + business_name_value.text.toString())
//      if (business_name_value.text.isEmpty()) {
//        business_name_value.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//        Toasty.error(requireContext(), "Entered Business name is not valid!!", Toast.LENGTH_LONG)
//          .show()
//        return false
//      } else {
//        business_name_value.setBackgroundResource(com.boost.payment.R.drawable.rounded_edit_fill_kyc)
//      }
            if (!gstcheck.isChecked) {
                if (cart_business_address.text.toString().isEmpty()) {
                    cart_business_address.setBackgroundResource(com.boost.cart.R.drawable.et_validity_error)
                    cart_main_scroller.post {
                        cart_main_scroller.scrollTo(
                                0,
                                gst_layout.getBottom()
                        )
                    }
                    Toasty.error(
                            requireContext(),
                            "Entered Business address is not valid!!",
                            Toast.LENGTH_LONG
                    )
                            .show()
                    return false
                } else {
                    cart_business_address.setBackgroundResource(com.boost.cart.R.drawable.rounded_edit_fill_kyc)
                }
            }

            return !gstcheck.isChecked && cart_business_address.text.toString().isNotEmpty()
        }

        if (!business_gstin_number.text.toString().isEmpty() && !com.boost.cart.utils.Utils.isValidGSTIN(
                        business_gstin_number.text.toString()
                )) {
            business_gstin_number.setBackgroundResource(com.boost.cart.R.drawable.et_validity_error)
            Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
            return false
        }
//    if (!com.boost.payment.utils.Utils.isValidMail(business_email_address.text.toString())) {
//      business_email_address.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//      Toasty.error(requireContext(), "Entered Email ID is not valid!!", Toast.LENGTH_LONG).show()
//      return false
//    } else {
//      business_email_address.setBackgroundResource(com.boost.payment.R.drawable.rounded_edit_fill_kyc)
//    }

//    if (!com.boost.payment.utils.Utils.isValidMobile(business_contact_number.text.toString())) {
//      business_contact_number.setBackgroundResource(com.boost.payment.R.drawable.et_validity_error)
//      Toasty.error(requireContext(), "Entered Mobile Number is not valid!!", Toast.LENGTH_LONG)
//        .show()
//      return false
//    } else {
////      business_contact_number.setBackgroundResource(R.drawable.rounded_edit_fill_kyc)
//    }

        /*if (!confirm_checkbox.isChecked) {
            Toasty.error(requireContext(), "Accept the Agreement!!", Toast.LENGTH_LONG).show()
            return false
        }*/
        return true
//        return false
    }

    private fun loadCustomerInfo() {
        viewModel.getCustomerInfo(
                (activity as? CartActivity)?.getAccessToken() ?: "",
                (activity as CartActivity).fpid!!,
                (activity as CartActivity).clientid
        )
    }

    private fun updateVisibility() {
        cart_business_address.visibility = View.GONE
        cart_business_city_name.visibility = View.GONE
        state_tin_value.visibility = View.GONE
        cart_business_address1.visibility = View.VISIBLE
        cart_business_city_name1.visibility = View.VISIBLE
    }

    private fun reverseVisibility() {
        cart_business_address.visibility = View.VISIBLE
        cart_business_city_name.visibility = View.VISIBLE
        state_tin_value.visibility = View.VISIBLE
        cart_business_address1.visibility = View.GONE
        cart_business_city_name1.visibility = View.GONE
    }


    private fun callGSTApi(gstNo: String) {
        if (Utils.isValidGSTIN(gstNo)) {
            loadGSTInfo(gstNo)
            viewModel.getGstApiResult().observe(viewLifecycleOwner, {
                gstInfoResult = it.result
                if (gstInfoResult != null) {
                    //  gst_business_name_value.text = gstInfoResult!!.legalName
                    val addressDetails = arrayOf(gstInfoResult!!.address!!.addressLine1, gstInfoResult!!.address!!.addressLine2, gstInfoResult!!.address!!.city, gstInfoResult!!.address!!.pincode, gstInfoResult!!.address!!.district, gstInfoResult!!.address!!.state)
                    val businessAddressDetails = joinNonBlankStringArray(addressDetails, ",")
                    //cart_business_address.text = businessAddressDetails
                    cart_business_address1.setText(businessAddressDetails)
                    cart_place_of_supply_cl1.visibility = View.VISIBLE
                    gst_info_tv.visibility = View.GONE
                    cart_business_city_name1.text = gstInfoResult!!.address!!.state
                    isGstApiCalled = true
                } else {
                    Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
                }
                hideProgress1()
            })
        } else {
            Toasty.error(requireContext(), "Invalid GST Number!!", Toast.LENGTH_LONG).show()
            hideProgress1()
        }
    }

    private fun joinNonBlankStringArray(s: Array<String?>?, separator: String?): String? {
        val sb = StringBuilder()
        if (s != null && s.size > 0) {
            for (w in s) {
                if (w != null && !w.trim { it <= ' ' }.isEmpty()) {
                    sb.append(w)
                    sb.append(separator)
                }
            }
        }
        return sb.substring(0, sb.length - 1) // length() - 1 to cut-down last extra separator
    }

    private fun showProgress() {
        progress_bar.visibility = View.VISIBLE
    }

    private fun hideProgress1() {
        progress_bar.visibility = View.GONE
    }

    private fun initMvvm1() {
        viewModel.getCustomerInfoResult().observeOnce(viewLifecycleOwner, Observer {
            createCustomerInfoRequest = it.Result
            if (createCustomerInfoRequest != null) {
                if (createCustomerInfoRequest!!.BusinessDetails != null) {
//          business_contact_number.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
//          business_email_address.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
                }
                if (createCustomerInfoRequest!!.AddressDetails != null) {
                    cart_business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                    business_supply_place_value.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                    cart_business_supply_place_missing.visibility = View.GONE

                    if (createCustomerInfoRequest!!.AddressDetails!!.City != null) {
                        viewModel.getStateFromCityAssetJson(
                                requireActivity(),
                                createCustomerInfoRequest!!.AddressDetails!!.City
                        )
                    }
                    if (createCustomerInfoRequest!!.AddressDetails!!.State != null || !createCustomerInfoRequest!!.AddressDetails!!.State.equals(
                                    "string"
                            )
                    ) {
                        cart_business_city_name.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                        cart_business_city_name1.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                        setStates = createCustomerInfoRequest!!.AddressDetails!!.State
                    }
                    if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
                        cart_business_address.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                        cart_business_address1.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                        //  gst_business_address_value.text = createCustomerInfoRequest!!.AddressDetails.Line1.toString()
                    }
                } else {
                    cart_business_supply_place_missing.visibility = View.VISIBLE
                    business_supply_place_value.visibility = View.GONE
                }
                if (createCustomerInfoRequest!!.TaxDetails != null) {
                    business_gstin_number.setText(createCustomerInfoRequest!!.TaxDetails!!.GSTIN)
                }

                if (createCustomerInfoRequest!!.TaxDetails?.GSTIN != null /*|| createCustomerInfoRequest!!.TaxDetails?.GSTIN.equals("")*/) {
                    cart_business_gstin_value.setText(createCustomerInfoRequest!!.TaxDetails.GSTIN)
                    cart_business_gstin_missing.visibility = View.GONE
                } else {
                    cart_business_gstin_value.visibility = View.GONE
                    cart_business_gstin_missing.visibility = View.VISIBLE
                }


                if (createCustomerInfoRequest!!.Name != null) {
                    // business_name_value.setText(createCustomerInfoRequest!!.Name)
                }

                if (createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber != null) {
                    //  business_contact_number.setText(createCustomerInfoRequest!!.BusinessDetails!!.PhoneNumber)
                } else {
//          if (session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")) {
//
//          } else {
//            business_contact_number.setText(session?.userPrimaryMobile)
//          }
                }


//        if (createCustomerInfoRequest!!.BusinessDetails!!.Email != null) {
//          business_email_address.setText(createCustomerInfoRequest!!.BusinessDetails!!.Email)
//        } else {
//          if (session?.fPEmail == null || session?.fPEmail.equals("")) {
//
//          } else {
//            business_email_address.setText(session?.fPEmail)
//          }
//        }


//        if (createCustomerInfoRequest!!.Name != null) {
//          business_name_value.setText(createCustomerInfoRequest!!.Name)
//          gst_business_name_value.text = createCustomerInfoRequest!!.Name
//        } else {
//          if (session?.fPName == null || session?.fPName.equals("")) {
//
//          } else {
//            business_name_value.setText(session?.fPName)
//            gst_business_name_value.text = session?.fPName
//          }
//        }


                if (createCustomerInfoRequest!!.AddressDetails != null) {


                    if (createCustomerInfoRequest!!.AddressDetails.Line1 != null) {
                        cart_business_address1.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                        cart_business_address_value.setText(createCustomerInfoRequest!!.AddressDetails.Line1.toString())
                        cart_business_address_missing.visibility = View.GONE
                        //  business_supply_place_value.setText(createCustomerInfoRequest!!.AddressDetails!!.State)
                        // gst_business_address_value.text = createCustomerInfoRequest!!.AddressDetails.Line1.toString()
                    } else {
                        if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(
                                        Key_Preferences.GET_FP_DETAILS_ADDRESS
                                ).equals("")
                        ) {

                        } else {
                            cart_business_address1.setText(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))
                            cart_business_address.setText(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))
                            cart_business_address_value.visibility = View.GONE
                            cart_business_address_missing.visibility = View.VISIBLE
                            //  gst_business_address_value.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
                        }
                    }
                }

            }
        })
        viewModel.getCustomerInfoStateResult().observeOnce(this, Observer {
            customerInfoState = it
            if (!customerInfoState) {
                if (session?.userPrimaryMobile == null || session?.userPrimaryMobile.equals("")) {

                } else {
                    //    business_contact_number.setText(session?.userPrimaryMobile)
                }

                //if(session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL) == null || session?.getFPDetails(Key_Preferences.PRIMARY_EMAIL).equals("") ){
                if (session?.fPEmail == null || session?.fPEmail.equals("")) {
                } else {
                    //    business_email_address.setText(session?.fPEmail)
                }

                if (session?.fPName == null || session?.fPName.equals("")) {

                } else {
                    //  business_name_value.setText(session?.fPName)
                }

                if (session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS) == null || session?.getFPDetails(
                                Key_Preferences.GET_FP_DETAILS_ADDRESS
                        ).equals("")
                ) {

                } else {
                    cart_business_address.setText(session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))
                }
            }
        })

        viewModel.getUpdatedCustomerBusinessResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
                val event_attributes: HashMap<String, Any> = HashMap()
                event_attributes.put("", it.Result.CustomerId)
                com.boost.cart.utils.WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_BUSINESS_DETAILS_UPDATE_SUCCESS,
                        ADDONS_MARKETPLACE,
                        event_attributes
                )
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
                        .show()
                com.boost.cart.utils.WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_BUSINESS_DETAILS_FAILED,
                        ADDONS_MARKETPLACE,
                        NO_EVENT_VALUE
                )
                (activity as CartActivity).prefs.storeInitialLoadMarketPlace(true)
            }
            // dismiss()
        })

        viewModel.getUpdatedCustomerResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Updated Profile.", Toast.LENGTH_LONG).show()
                loadCustomerInfo()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
                        .show()
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(true)
            }
        })


        viewModel.getUpdatedResult().observeOnce(viewLifecycleOwner, Observer {
            if (it.Result != null) {
                Toasty.success(requireContext(), "Successfully Created Profile.", Toast.LENGTH_LONG).show()
                val event_attributes: HashMap<String, Any> = HashMap()
                event_attributes.put("", it.Result.CustomerId)
                com.boost.cart.utils.WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_BUSINESS_DETAILS_CREATE_SUCCESS,
                        ADDONS_MARKETPLACE,
                        event_attributes
                )
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(false)
            } else {
                Toasty.error(requireContext(), "Something went wrong. Try Later!!", Toast.LENGTH_LONG)
                        .show()
                com.boost.cart.utils.WebEngageController.trackEvent(
                        ADDONS_MARKETPLACE_BUSINESS_DETAILS_FAILED,
                        ADDONS_MARKETPLACE,
                        NO_EVENT_VALUE
                )
//                (activity as PaymentActivity).prefs.storeInitialLoadMarketPlace(true)
            }
            //  dismiss()
        })


        viewModel.stateResult().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val adapter =
                        ArrayAdapter(requireActivity(), android.R.layout.simple_spinner_dropdown_item, it)
//                business_city_name.setAdapter(adapter)
            }

        })

//    viewModel.getSelectedStateResult().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
//      if (it != null) {
//        business_city_name.text = it
//        setStates = it
//      }
//
//    })

        viewModel.getSelectedStateResult().observe(viewLifecycleOwner, {
            if (it != null) {
                cart_business_city_name.text = it
                setStates = it
            }
        })
        viewModel.getSelectedStateTinResult().observe(viewLifecycleOwner, {
            if (it != null) {
                state_tin_value.text = "(" + it + ")"
                setStateTin = it
            }
        })

    }

    private fun loadGSTInfo(gstIn: String) {
        viewModel.getGstApiInfo(
                (activity as? CartActivity)?.getAccessToken() ?: "",
                gstIn,
                (activity as CartActivity).clientid,
                progress_bar
        )
    }


    fun updateRecycler(list: List<FeaturesModel>) {
//    if (shimmer_view_recomm_addons.isShimmerStarted) {
//      shimmer_view_recomm_addons.stopShimmer()
//      shimmer_view_recomm_addons.visibility = View.GONE
//    }

//        val temp: List<FeaturesModel> = arrayListOf()

        recomended_viewpager.offscreenPageLimit = 3
        upgradeAdapter.addupdates(list)
        recomended_viewpager.adapter = upgradeAdapter
        upgradeAdapter.notifyDataSetChanged()
        viewpager_layout.visibility = View.VISIBLE
        // recycler.isFocusable = false
//        back_image.isFocusable = true
    }

//  fun updatePackageViewPager(list: List<Bundles>) {
//    recomended_viewpager.offscreenPageLimit = 3
//    packageViewPagerAdapter.addupdates(list)
//    packageViewPagerAdapter.notifyDataSetChanged()
//    //show dot indicator only when the (list.size > 2)
////    if (list.size > 1) {
////      package_indicator.visibility = View.VISIBLE
////    } else {
////      package_indicator.visibility = View.INVISIBLE
////      package_compare_layout.visibility = View.INVISIBLE
////    }
//  }

    private fun initializePackageViewPager() {
        recomended_viewpager.adapter = upgradeAdapter
        // package_indicator.setViewPager2(package_viewpager)

        recomended_viewpager?.setPageTransformer(SimplePageTransformer())

        val itemDecoration = com.boost.dbcenterapi.utils.HorizontalMarginItemDecoration(
                requireContext(), R.dimen.viewpager_current_item_horizontal_margin4
        )
        recomended_viewpager!!.addItemDecoration(itemDecoration)

//        package_viewpager.setPageTransformer(SimplePageTransformer())

//    val itemDecoration = HorizontalMarginItemDecoration(
//        requireContext(),
//        R.dimen.viewpager_current_item_horizontal_margin
//    )
//    package_viewpager.addItemDecoration(itemDecoration)

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
                    (activity as CartActivity).supportFragmentManager,
                    Constants.SPLASH_FRAGMENT
            )
        }
    }

    private fun createCartStateRenewal(renewalItems: List<CartModel>?) {
        Log.v("createPurchaseOrder2", " " + renewalItems.toString())
        val widgetList = arrayListOf<com.boost.dbcenterapi.data.renewalcart.Widget>()
        renewalItems?.forEach { widgetList.add(com.boost.dbcenterapi.data.renewalcart.Widget(it.item_id, it.boost_widget_key)) }

        val request = CreateCartStateRequest((activity as CartActivity).clientid, (activity as CartActivity).fpid, "RENEWAL", widgetList)
        viewModel.createCartStateRenewal((activity as? CartActivity)?.getAccessToken()
                ?: "", request)
        viewModel.createCartRenewalResult().observeOnce(Observer { createPurchaseOrder(it.cartStateId) })
    }

    private fun checkRenewalItemDeepLinkClick() {
        val request1 =
                RenewalPurchasedRequest(
                        floatingPointId = (activity as CartActivity).fpid, clientId = (activity as CartActivity).clientid,
                        widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
                        dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_MM_DD_YYYY), endDate = DateUtils.getAmountDate((activity as CartActivity).deepLinkDay).parseDate(DateUtils.FORMAT_MM_DD_YYYY)
                )
        Log.v("allPurchasedWidgets", " " + request1)
        val ac = (activity as CartActivity)
        if (ac.isBackCart.not() && (ac.isDeepLink || ac.isOpenCardFragment)) {
            val currentDate = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_MM_DD_YYYY)
            val deepLinkDay = (activity as CartActivity).deepLinkDay
            val dateAmount = DateUtils.getAmountDate(deepLinkDay).parseDate(DateUtils.FORMAT_MM_DD_YYYY)
            val request = if (deepLinkDay <= -1) {
                RenewalPurchasedRequest(
                        floatingPointId = (activity as CartActivity).fpid, clientId = (activity as CartActivity).clientid,
                        widgetStatus = RenewalPurchasedRequest.WidgetStatus.EXPIRED.name, dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name,
                        startDate = dateAmount, endDate = currentDate
                )
            } else {
                RenewalPurchasedRequest(
                        floatingPointId = (activity as CartActivity).fpid, clientId = (activity as CartActivity).clientid,
                        widgetStatus = RenewalPurchasedRequest.WidgetStatus.ACTIVE.name, nextWidgetStatus = RenewalPurchasedRequest.NextWidgetStatus.RENEWAL.name,
                        dateFilter = RenewalPurchasedRequest.DateFilter.EXPIRY_DATE.name, startDate = currentDate, endDate = dateAmount
                )
            }

            viewModel.allPurchasedWidgets((activity as? CartActivity)?.getAccessToken()
                    ?: "", request)
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
//                    (activity as CartActivity).onBackPressed()
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
                                                    Key = prop.key!!,
                                                    Value = prop.value!!
                                            )
                                    )
                                }
                            }

                        }
                    } catch (ex: Exception) {
                        SentryController.captureException(ex)
                        ex.printStackTrace()
                    }
                }
                val widget = Widget(
                        data?.category
                                ?: "", ConsumptionConstraint("DAYS", 30), "", item.description_title,
                        item.discount, Expiry("MONTHS", default_validity_months), listOf(), true, true, item.item_name
                        ?: "",
                        item.price, item.MRPPrice, if (outputExtendedPropsRenew.size > 0) outputExtendedPropsRenew else null, 1, "MONTHLY", item.boost_widget_key
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
                                                    Key = prop.key!!,
                                                    Value = prop.value!!
                                            )
                                    )
                                }
                            }

                        }
                    } catch (ex: Exception) {
                        SentryController.captureException(ex)
                        ex.printStackTrace()
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

        viewModel.InitiatePurchaseOrder((activity as? CartActivity)?.getAccessToken() ?: "",
                CreatePurchaseOrderV2(
                        (activity as CartActivity).clientid,
                        (activity as CartActivity).fpid!!,
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
                                                    Key = prop.key!!,
                                                    Value = prop.value!!
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
                        item.price, item.MRPPrice, if (outputExtendedProps1.size > 0) outputExtendedProps1 else null, 1, "MONTHLY", item.boost_widget_key
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
                                                    Key = prop.key!!,
                                                    Value = prop.value!!
                                            )
                                    )
                                }
                            }

                        }
                    } catch (ex: Exception) {
                        SentryController.captureException(ex)
                        ex.printStackTrace()
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

        viewModel.InitiatePurchaseAutoRenewOrder((activity as? CartActivity)?.getAccessToken()
                ?: "",
                CreatePurchaseOrderV2(
                        (activity as CartActivity).clientid,
                        (activity as CartActivity).fpid!!,
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
        viewModel.getAllFeatures()
        viewModel.getAllBundles()
        Handler().postDelayed({
            viewModel.getCartItems()
        }, 2000)
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
//                (activity as CartActivity).clientid,
//                "+91",
//                "ANDROID",
//                null,
//                (activity as CartActivity).fpTag!!,
//                null,
//                null,
//                com.boost.cart.data.api_model.customerId.customerInfo.TaxDetails(
//                        null,
//                        null,
//                        null,
//                        null
//                )
//        ))
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvM() {

//    viewModel.getSelectedStateResult().observe(viewLifecycleOwner,{
//      if(it!= null){
//        cart_business_city_name.text = it
//        setStates = it
//      }
//    })
//    viewModel.getSelectedStateTinResult().observe(viewLifecycleOwner,{
//      if(it!=null){
//        state_tin_value.text = "(" + it + ")"
//        setStateTin = it
//      }
//    })


        viewModel.getCouponApiResult().observe(this) {
            if (it != null) {

                for (i in 0 until it.size) {

                    it[i].data?.let { it1 -> couponData.addAll(it1) }
                }
                System.out.println("CouponData" + couponData)
                cart_coupon_code_rv.layoutManager = LinearLayoutManager(requireContext())
                couponDataNo.add(couponData[0])
                val adapter = CartCouponAdapter(couponDataNo, total, this)
                cart_coupon_code_rv.adapter = adapter
                tv_Show_more.setOnClickListener {
                    tv_Show_less.visibility = VISIBLE
                    tv_Show_more.visibility = GONE
                    val adapter = CartCouponAdapter(couponData, total, this)
                    cart_coupon_code_rv.adapter = adapter
                }
                tv_Show_less.setOnClickListener {
                    tv_Show_more.visibility = VISIBLE
                    tv_Show_less.visibility = GONE
                    couponDataNo.clear()
                    couponDataNo.add(couponData[0])
                    val adapter = CartCouponAdapter(couponDataNo, total, this)
                    cart_coupon_code_rv.adapter = adapter
                }

            }
        }

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
                        if (!recommendedAddonsWidgetKey.contains(items.boost_widget_key!!)) {
                            recommendedAddonsWidgetKey.add(items.boost_widget_key!!)
                        }
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
                        for (singleBundle in bundlesList) {
                            if (singleBundle.bundle_id.equals(bundle.item_id)) {
                                val temp = Gson().fromJson<List<IncludedFeature>>(singleBundle.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                                for (singleFeatures in temp) {
                                    if (!recommendedAddonsWidgetKey.contains(singleFeatures.feature_code)) {
                                        recommendedAddonsWidgetKey.add(singleFeatures.feature_code)
                                    }
                                }
                                break
                            }
                        }
                    }
                    if (default_validity_months > 0) {
                        if (prefs.getCartValidityMonths().isNullOrEmpty().not()) {
                            months_validity.setText(prefs.getCartValidityMonths().toString() + " months")
                            feature_validity.text = prefs.getCartValidityMonths().toString() + " Months"
                        } else {
                            months_validity.setText(default_validity_months.toString() + " months")
                            feature_validity.text = default_validity_months.toString() + " Months"

                        }
                    }
//                        months_validity.text = default_validity_months.toString() + " months"
                    else {
                        if (prefs.getCartValidityMonths().isNullOrEmpty().not()) {
                            months_validity.setText(prefs.getCartValidityMonths() + " month")
                            feature_validity.text = prefs.getCartValidityMonths().toString() + " Month"

                        } else {
                            months_validity.setText(default_validity_months.toString() + " month")
                            feature_validity.text = default_validity_months.toString() + " Month"

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

                    if (prefs.getCartValidityMonths().isNullOrEmpty().not()) {
                        Log.e("getCartValidityMonths", prefs.getCartValidityMonths()!!)
                        months_validity.setText(prefs.getCartValidityMonths() + " month")
                        feature_validity.text = prefs.getCartValidityMonths().toString() + " Month"

                    } else {
                        Log.e("default_validity_months", default_validity_months.toString())
                        months_validity.setText(default_validity_months.toString() + " month")
                        feature_validity.text = default_validity_months.toString() + " Month"

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
                viewModel.getRecommendedAddons(RecommendedAddonsRequest(recommendedAddonsWidgetKey, (activity as CartActivity).fpid, (activity as CartActivity).experienceCode))
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
                cart_business_gstin_missing.visibility = View.GONE
                cart_business_gstin_value.visibility = View.VISIBLE
                cart_business_gstin_value.text = it

//        gstin_remove.visibility = View.VISIBLE
//        gstin_title.text = "GSTIN"
//        entered_gstin_number.visibility = View.VISIBLE
//        enter_gstin_number.visibility = View.GONE
//        entered_gstin_number.text = it
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
                     (activity as CartActivity).addFragment(
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

//    viewModel.getTAN().observe(this, Observer {
//      if (it != null) {
//        Log.i("getTAN >> ", it)
//        TANNumber = it
//        enter_tan_number.visibility = View.GONE
//        entered_tan_number.visibility = View.VISIBLE
//        entered_tan_number.text = it
//
//        tan_remove.visibility = View.VISIBLE
//        tan_title.text = "TAN"
//      }
//    })

        //getting all features
        viewModel.updateAllFeaturesResult().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                featuresList = it
                updatePackageFeature(it)
                val lessList = it!!.subList(0, 20)
            }


        })

        //getting all bunles
        viewModel.updateAllBundlesResult().observe(this, Observer {
            if (it.isNullOrEmpty().not()) bundlesList = it

//      val list = arrayListOf<Bundles>()
//      for (item in it) {
//        val temp = Gson().fromJson<List<IncludedFeature>>(item.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
//        list.add(Bundles(
//          item.bundle_id,
//          temp,
//          item.min_purchase_months,
//          item.name,
//          item.overall_discount_percent,
//          PrimaryImage(item.primary_image),
//          item.target_business_usecase,
//          Gson().fromJson<List<String>>(item.exclusive_to_categories, object : TypeToken<List<String>>() {}.type),
//          null,
//          item.desc))
//      }
//      if (list.size > 0) {
//
//        for(items in list){
//          Log.v("getkeyWidget"," "+ items.name +" "+items.included_features.size)
//          val itemIds = arrayListOf<String>()
//          for (item in  items!!.included_features) {
//
//            itemIds.add(item.feature_code)
////                        viewModel.getFeatureValues(item.feature_code)
//          }
//          Log.v("getkeyWidget123"," "+ itemIds.size)
//     //     viewModel.getFeatureValues(itemIds)
////                    viewModel.loadUpdates(itemIds)
////                    viewModel.getAssociatedWidgetKeys(items._kid)
////                    viewModel.getAssociatedWidgetKeys("5f6a2d66663bb00001e2b1d7")
//        }
////                package_layout.visibility = View.VISIBLE
//        upgradeList = list
//                updatePackageViewPager(list)
//      } else {
////                package_layout.visibility = View.GONE
//      }

        })

        //getting valid Coupon Code
        viewModel.updateValidCouponResult().observe(this, Observer {
            if (it != null) {
                //clear stored cartOrderInfo
                prefs.storeCartOrderInfo(null)

                //save coupon Details
                prefs.storeApplyedCouponDetails(it)

                validCouponCode = it
                //      discount_coupon_title.text = validCouponCode!!.coupon_key
                cart_apply_coupon.visibility = View.GONE
                //       discount_coupon_remove.visibility = View.VISIBLE
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
                    //         prefs.storeApplyedCouponDetails(it)

//                    validCouponCode = it
                    couponServiceModel = it
                    couponCode = it!!.coupon_key!!
                    //         coupon_discount_title.text = it!!.coupon_key
                    cart_apply_coupon.visibility = View.GONE
                    //     discount_coupon_remove.visibility = View.VISIBLE
                    if (it.success!!) {
                        //        discount_coupon_message.visibility = View.VISIBLE
                        //       discount_coupon_message.text = it.message
                        cart_applied_coupon_full_layout.visibility = View.VISIBLE
                        cart_coupon_code_rv.visibility = View.GONE
                        tv_Show_more.visibility = View.GONE
                        tv_Show_less.visibility = GONE

                        val dialog = Dialog(requireActivity())
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.layout_saved_coupon)
                        val tv_coupon_saved = dialog.findViewById(R.id.tv_badge_text) as AppCompatTextView
                        val tv_coupon_name = dialog.findViewById(R.id.tv_error_message) as AppCompatTextView

                        tv_coupon_saved.text = "₹" + it.couponDiscountAmt.toString() + " Saved!"
                        tv_coupon_name.text = it.coupon_key.toString() + " coupon code applied."
                        val closeBtn = dialog.findViewById(R.id.close_dialog) as AppCompatImageView
                        val iv_coupon_saved = dialog.findViewById(R.id.iv_badge_bg) as AppCompatImageView

                        closeBtn.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
                        dialog.show()
                        cart_coupon_discount_title.text = it.coupon_key.toString()
                        save.text = " You save ₹ " + (it.couponDiscountAmt.toString())
                        discount_banner.visibility = View.VISIBLE
                        discount_banner_text.text = "Hooray! You save ₹ " + (it.couponDiscountAmt.toString()) + " with coupon & long validity discount"
                    } else {
                        //        discount_coupon_message.visibility = View.VISIBLE
                        //        discount_coupon_message.text = it.message
                        discount_banner.visibility = View.GONE
                        cart_coupon_code_rv.visibility = View.VISIBLE
                        if (tv_Show_less.visibility == VISIBLE) {
                            tv_Show_more.visibility = View.GONE

                        } else {
                            tv_Show_more.visibility = View.VISIBLE

                        }

                        discount_banner_text.text = it.message
                    }
                    totalCalculationAfterCoupon()
                } else {
                    validCouponCode = null
                }
            }

        })

        //get customerId
        viewModel.getCustomerId().observe(this, Observer {
            if (it != null && it.isNotEmpty()) {
                customerId = it
            }
        })
        viewModel.getRecommendedAddonResult().observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                val temp: MutableList<FeaturesModel> = arrayListOf()

                for (singleData in it.recommendations!!) {
                    for (singleItem in featuresList) {

                        if (singleData.widgetName.equals(singleItem.boost_widget_key)) {

                            temp.add(singleItem)
                            break
                        }

                    }
                }
                updateRecycler(temp)

            }

        })

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

    fun updatePackageFeature(list: List<FeaturesModel>) {
        cartPackageAdaptor.addupdate(list)
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
                cart_amount.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total)
                couponDiscountAmount = total * couponDisount / 100
                coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)
                total -= couponDiscountAmount
                val temp = (total * 18) / 100
                taxValue = Math.round(temp * 100) / 100.0
                grandTotal = (Math.round((total + taxValue) * 100) / 100.0)
//        igst_value.text = "+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue)
//                order_total_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                cart_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                footer_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                header_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
            }
        }
    }

    fun totalCalculationAfterCoupon() {
        if (::cartList.isInitialized) {
            total = 0.0
            overalltotal = 0.0
            couponDiscountAmount = 0.0
            var couponDisount = 0
//            if (validCouponCode != null) {
//                couponDisount = validCouponCode!!.discount_percent
//                coupon_discount_title.text = "Discount(" + couponDisount.toString() + "%)"
//            } else {
//                coupon_discount_title.text = "Discount"
//            }
            if (cartList != null && cartList.size > 0) {
                for (item in cartList) {
                    if (!bundles_in_cart && item.item_type.equals("features"))
                        total += (item.price * default_validity_months)
                    else
                        total += ((item.price / package_validity_months) * default_validity_months)
                }
                cart_amount_title.text = "Cart total (" + cartList.size + " items)"
                overalltotal = total + taxValue
                cart_amount.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(overalltotal)
                coupontotal = total

                if (couponServiceModel != null)
                    couponDiscountAmount = couponServiceModel?.couponDiscountAmt!!
                else
                    couponServiceModel = null
//                couponDiscountAmount = total * couponDisount / 100
//                couponDiscountAmount = couponServiceModel!!.couponDiscountAmt!!
                coupon_discount_value.text = "-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount)

//                if (validCouponCode != null) {
                coupon_discount_title.text =
                        "'" + couponServiceModel?.coupon_key + "'" + " coupon discount"
//                }else {
//                coupon_discount_title.text = "Discount Coupon"
//            }

                overalltotal -= couponDiscountAmount
                Log.v("cart_amount_value", " " + total)
                val temp = (total * 18) / 100
                taxValue = Math.round(temp * 100) / 100.0
                grandTotal = (Math.round((overalltotal) * 100) / 100.0)
                //       igst_value.text = "+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue)
//                order_total_value.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                cart_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                footer_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)
                header_grand_total.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal)

                val revenue = Math.round(grandTotal * 100).toInt() / 100
                event_attributes.put("total amount", revenue)
                event_attributes.put("cart validity months", default_validity_months)


            }
        }
    }

    override fun deleteCartAddonsItem(itemID: String) {
        viewModel.deleteCartItems(itemID)
        couponDiwaliRedundant.remove(itemID)
        //remove saved orderdetails from prefs
        prefs.storeCartOrderInfo(null)
        prefs.storeCartValidityMonths(null)
        //remove item from firebase
        CartFirestoreManager.removeDocument(itemID)
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
            (activity as CartActivity).addFragment(packageFragment, Constants.PACKAGE_FRAGMENT)
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
//    val paymentFragment = PaymentFragment.newInstance()
//    val args = Bundle()
//    args.putString("customerId", customerId)
//    args.putDouble("amount", result.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
//    args.putString("order_id", result.Result.OrderId)
//    args.putString("transaction_id", result.Result.TransactionId)
//    args.putString("email", (activity as CartActivity).email)
//    args.putString("currency", "INR")
//    args.putString("contact", (activity as CartActivity).mobileNo)
        prefs.storeCardIds(cartItems)
        prefs.storeCouponIds(couponCode)
        prefs.storeValidityMonths(default_validity_months.toString())
//    paymentFragment.arguments = args
//    (activity as CartActivity).addFragment(
//      paymentFragment,
//      Constants.PAYMENT_FRAGMENT
//    )
        val intent = Intent(
                requireContext(),
                PaymentActivity::class.java
        )
        intent.putExtra("months",default_validity_months)
        intent.putExtra("fpid", (activity as CartActivity).fpid)
        intent.putExtra("fpName", (activity as CartActivity).fpName)
        intent.putExtra("customerId", customerId)
        intent.putExtra("amount", result.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
        intent.putExtra("order_id", result.Result.OrderId)
        intent.putExtra("transaction_id", result.Result.TransactionId)
        intent.putExtra("email", (activity as CartActivity).email)
        intent.putExtra("currency", "INR")
        intent.putExtra("contact", (activity as CartActivity).mobileNo)
        startActivity(intent)
    }

    fun proceedToAutoRenewPayment(result: CreatePurchaseOrderResponse) {
        val autoRenewFragment: AutoRenewSubsFragment = AutoRenewSubsFragment.newInstance()
        val args = Bundle()
        args.putString("title", "Auto Renewal Subscription")
        args.putString("link", result.Result.TransactionRequestLink)
        Log.v("proceedToAutoRenew", " " + result.Result.TransactionRequestLink)
        autoRenewFragment.arguments = args
        (activity as CartActivity).addFragment(
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
        }
    }

    private fun hideProgress() {
        try {
//      if (progressDialog.isShowing) progressDialog.hide()
            if (progressDialog.isShowing) progressDialog.cancel()
        } catch (e: Exception) {
            SentryController.captureException(e)
            e.printStackTrace()
        }
    }

    private fun loadOfferCoupons() {

        var bulkPropertySegment = ArrayList<ArrayList<BulkPropertySegment>>()
        val bulkObject1 = BulkPropertySegment(propertyDataType = "upgrade", propertyName = "upgrade", type = 5)
        var objectKeys = ObjectKeys(true, description = true, discountPercent = true, kid = true, termsandconditions = true, title = true)
        val bulkObject2 = BulkPropertySegment(0, 10, objectKeys, "coupon", "discount_coupons", 1)
        bulkPropertySegment.add(0, arrayListOf(bulkObject1, bulkObject2))
        viewModel.getCouponRedeem(CouponRequest(bulkPropertySegment, SCHEMA_ID, WEBSITE_ID))
    }


    private fun loadLastUsedPayData() {
        viewModel.getLastUsedPaymentDetails(
                (activity as? CartActivity)?.getAccessToken() ?: "",
                (activity as CartActivity).fpid!!,
                (activity as CartActivity).clientid
        )
    }

    override fun applycoupon(mList: Data) {
        mList.code?.let { viewModel.getCouponRedeem(mList.code?.let { RedeemCouponRequest(total, it, (activity as CartActivity).fpid!!) }!!, it) }

    }

    private fun showPopupWindow(anchor: View) {
        val view: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.popup_window_text, null)
        val popupWindow = PopupWindow(
                view,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
        )
        val txtSub: TextView = popupWindow.contentView.findViewById(R.id.price1)
        val txtSub1: TextView = popupWindow.contentView.findViewById(R.id.price2)
        txtSub.setText(" ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total))
        txtSub1.setText(" ₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) popupWindow.elevation =
                5.0f
        popupWindow.showAsDropDown(anchor, (anchor.width - 40), -166)
    }

}




