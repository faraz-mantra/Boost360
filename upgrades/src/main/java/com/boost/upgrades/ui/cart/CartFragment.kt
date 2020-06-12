package com.boost.upgrades.ui.cart

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
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
import com.boost.upgrades.data.api_model.GetAllFeatures.response.ExtendedProperty
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
//import com.boost.upgrades.data.api_model.PurchaseOrder.request.*
import com.boost.upgrades.data.api_model.PurchaseOrder.requestV2.*
import com.boost.upgrades.data.model.BundlesModel
import com.boost.upgrades.data.model.CartModel
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.CartFragmentListener
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.CouponPopUpFragment
import com.boost.upgrades.ui.popup.GSTINPopUpFragment
import com.boost.upgrades.ui.popup.TANPopUpFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.GSTIN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.Constants.Companion.TAN_POPUP_FRAGEMENT
import com.boost.upgrades.utils.SharedPrefs
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.cart_fragment.*
import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartFragment : BaseFragment(), CartFragmentListener {

    lateinit var root: View

    lateinit var localStorage: LocalStorage

    var customerId: String = ""

    lateinit var cartList: List<CartModel>

    lateinit var featuresList: List<FeaturesModel>

    lateinit var bundlesList: List<BundlesModel>

    var total = 0.0

    var grandTotal = 0.0

    var GSTINNumber: String? = null
    var TANNumber: String? = null

    var taxValue = 0.0

    var validCouponCode: CouponsModel? = null

    var couponDiscountAmount = 0.0

    lateinit var progressDialog: ProgressDialog

    //    private var cartAdapter = CartAdapter(ArrayList())
    lateinit var cartPackageAdaptor: CartPackageAdaptor
    lateinit var cartAddonsAdaptor: CartAddonsAdaptor

    val couponPopUpFragment = CouponPopUpFragment()

    val gstinPopUpFragment = GSTINPopUpFragment()

    val tanPopUpFragment = TANPopUpFragment()

    companion object {
        fun newInstance() = CartFragment()
    }

    private lateinit var viewModel: CartViewModel

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.cart_fragment, container, false)
        localStorage = LocalStorage.getInstance(context!!)!!

        progressDialog = ProgressDialog(requireContext())

        cartPackageAdaptor = CartPackageAdaptor(ArrayList(), this)
        cartAddonsAdaptor = CartAddonsAdaptor(ArrayList(), this)

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Cart Initialised", "ADDONS_MARKETPLACE Cart", "")

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

        loadData()
        initMvvM()
        initializePackageRecycler()
        initializeAddonsRecycler()

        cart_continue_submit.setOnClickListener {
            //customerId = viewModel.getCustomerId()
//            customerId != null &&
            if (total > 0 && ::cartList.isInitialized && ::featuresList.isInitialized) {
                val purchaseOrders = ArrayList<PurchaseOrder>()
                for (item in cartList) {
                    val widgetList = ArrayList<Widget>()
                    var extendProps: List<ExtendedProperty>? = null
                    var outputExtendedProps = ArrayList<Property>()
                    var extraPurchaseOrderDetails: ExtraPurchaseOrderDetails? = null
                    var bundleNetPrice = 0.0
                    var bundleDiscount = 0
                    var couponCode: String? = null

                    if (item.extended_properties != null && item.extended_properties!!.length > 0) {
                        try {
                            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
                            extendProps = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

                            if (extendProps != null) {
                                for (prop in extendProps) {
                                    outputExtendedProps.add(Property(
                                            Key = prop.key!!,
                                            Value = prop.value!!
                                    ))
                                }

                            }
                        } catch (ex: Exception) {
                            Log.e("FAILED", ex.message)
                        }
                    }

                    if (validCouponCode != null) {
                        couponCode = validCouponCode!!.coupon_key
                    }

                    if (item.item_type.equals("features")) {
                        val discount = 100 - item.discount
                        val netPrice = (discount * item.MRPPrice) / 100

                        widgetList.add(Widget(
                                "CLINICS",
                                ConsumptionConstraint(
                                        "DAYS",
                                        30
                                ),
                                "",
                                item.description_title,
                                item.discount,
                                Expiry(
                                        "DAYS",
                                        30
                                ),
                                listOf(),
                                true,
                                true,
                                item.item_name!!,
                                netPrice,
                                item.MRPPrice,
                                if (outputExtendedProps.size > 0) outputExtendedProps else null,
                                1,
                                "MONTHLY",
                                item.boost_widget_key
                        ))
                    } else if (item.item_type.equals("bundles")) {
                        if (::bundlesList.isInitialized && bundlesList.size > 0) {
                            for (singleBundle in bundlesList) {
                                if (singleBundle.bundle_key.equals(item.boost_widget_key)) {
                                    val outputBundleProps: ArrayList<Property> = arrayListOf()
                                    outputBundleProps.add(Property(
                                            Key = singleBundle.bundle_key,
                                            Value = singleBundle.name!!
                                    ))
                                    extraPurchaseOrderDetails = ExtraPurchaseOrderDetails(
                                            null,
                                            singleBundle.primary_image,
                                            singleBundle.name,
                                            outputBundleProps)
                                    bundleDiscount = singleBundle.overall_discount_percent
                                    val includedFeatures = Gson().fromJson<List<IncludedFeature>>(singleBundle.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
                                    for (singleIndludedFeature in includedFeatures) {
                                        for (singleFeature in featuresList) {
                                            if (singleIndludedFeature.feature_code.equals(singleFeature.boost_widget_key)) {
                                                val discount = 100 - item.discount
                                                val netPrice = (discount * item.MRPPrice) / 100
                                                //adding bundle netPrice
                                                bundleNetPrice += netPrice * singleBundle.min_purchase_months
                                                widgetList.add(Widget(
                                                        "CLINICS",
                                                        ConsumptionConstraint(
                                                                "DAYS",
                                                                30 * singleBundle.min_purchase_months
                                                        ),
                                                        "",
                                                        item.description_title,
                                                        item.discount,
                                                        Expiry(
                                                                "DAYS",
                                                                30 * singleBundle.min_purchase_months
                                                        ),
                                                        listOf(),
                                                        true,
                                                        true,
                                                        item.item_name!!,
                                                        netPrice * singleBundle.min_purchase_months,
                                                        item.MRPPrice * singleBundle.min_purchase_months,
                                                        if (outputExtendedProps.size > 0) outputExtendedProps else null,
                                                        1,
                                                        "MONTHLY",
                                                        item.boost_widget_key
                                                ))
                                                break
                                            }
                                        }
                                    }
                                    break
                                }
                            }
                        }
                    }


                    purchaseOrders.add(
                            PurchaseOrder(
                                    couponCode,
                                    bundleDiscount,
                                    extraPurchaseOrderDetails,
                                    bundleNetPrice,
                                    widgetList
                            )
                    )
                }

                var prefs = SharedPrefs(activity as UpgradeActivity)
                prefs.storeFeaturesCountInLastOrder(purchaseOrders.count())

                viewModel.InitiatePurchaseOrder(
//                        CreatePurchaseOrderRequest(
//                                (activity as UpgradeActivity).clientid,
//                                (activity as UpgradeActivity).fpid!!,
//                                PaymentDetails(
//                                        "INR",
//                                        0,
//                                        "RAZORPAY",
//                                        TaxDetails(
//                                                GSTINNumber,
//                                                0,
//                                                null,
//                                                18),
//                                        grandTotal),
//                                widgetsToBeBought,
//                                null
//                        )
                        CreatePurchaseOrderV2(
                                (activity as UpgradeActivity).clientid,
                                (activity as UpgradeActivity).fpid!!,
                                PaymentDetails(
                                        "INR",
                                        0,
                                        "RAZORPAY",
                                        TaxDetails(
                                                GSTINNumber,
                                                0,
                                                null,
                                                18),
                                        grandTotal),
                                "NEW",
                                purchaseOrders
                        )
                )
            } else {
                Toasty.error(requireContext(), "Invalid items found in the cart. Please re-launch the Marketplace.", Toast.LENGTH_SHORT).show()
            }
        }

        back_button12.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        cart_view_details.setOnClickListener {
            cart_main_scroller.post {
                cart_main_scroller.fullScroll(View.FOCUS_DOWN)
            }
        }

        cart_apply_coupon.setOnClickListener {
            couponPopUpFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    COUPON_POPUP_FRAGEMENT
            )
        }
        enter_gst_number.setOnClickListener {
            gstinPopUpFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    GSTIN_POPUP_FRAGEMENT
            )
        }

        remove_gstin_number.setOnClickListener {
            GSTINNumber = null
            gstin_layout1.visibility = View.VISIBLE
            gstin_layout2.visibility = View.GONE
            fill_in_gstin_value.setText("")
        }

        enter_tan_number.setOnClickListener {
            tanPopUpFragment.show(
                    (activity as UpgradeActivity).supportFragmentManager,
                    TAN_POPUP_FRAGEMENT
            )
        }

        all_recommended_addons.setOnClickListener {
            (activity as UpgradeActivity).goBackToRecommentedScreen()
        }

    }

    fun loadData() {
//        viewModel.requestCustomerId(
//                CustomerIDRequest(
//                        (activity as UpgradeActivity).clientid,
//                        "ANDROID",
//                        (activity as UpgradeActivity).email!!,
//                        (activity as UpgradeActivity).loginid!!,
//                        (activity as UpgradeActivity).fpName!!,
//                        (activity as UpgradeActivity).mobileNo!!,
//                        "RAZORPAY",
//                        com.boost.upgrades.data.api_model.customerId.create.TaxDetails(
//                                null,
//                                0,
//                                null,
//                                0
//                        )
//                )
//        )
        viewModel.getCartItems()
        viewModel.getAllFeatures()
        viewModel.getAllBundles()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvM() {
        viewModel.cartResult().observe(this, Observer {
            if (it != null && it.size > 0) {
                cartList = it
                WebEngageController.trackEvent("ADDONS_MARKETPLACE Full_Cart Loaded", "Cart Size:" + it.size, "")
                empty_cart.visibility = View.GONE
                cart_main_layout.visibility = View.VISIBLE
                val features = arrayListOf<CartModel>()
                val bundles = arrayListOf<CartModel>()
                for (items in it) {
                    if (items.item_type.equals("features")) {
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
                if (bundles.size > 0) {
                    updatePackage(bundles)
                    package_layout.visibility = View.VISIBLE
                } else {
                    package_layout.visibility = View.GONE
                }
                totalCalculation()
            } else {
                WebEngageController.trackEvent("ADDONS_MARKETPLACE Empty_Cart Loaded", "ADDONS_MARKETPLACE Empty_Cart Loaded", "")
                empty_cart.visibility = View.VISIBLE
                cart_main_layout.visibility = View.GONE
            }
        })

        viewModel.getPurchaseOrderResponse().observe(this, Observer {
            if (it != null) {
                var prefs = SharedPrefs(activity as UpgradeActivity)
                prefs.storeLatestPurchaseOrderId(it.Result.OrderId)
                prefs.storeLatestPurchaseOrderTotalPrice(it.Result.TotalPrice.toFloat())

                //original cart amount and coupon discount added to shareprefs
                prefs.storeCartOriginalAmount((total + couponDiscountAmount).toFloat())
                prefs.storeCouponDiscountPercentage(if (validCouponCode == null) 0 else validCouponCode!!.discount_percent)

                val paymentFragment = PaymentFragment.newInstance()
                val args = Bundle()
                args.putString("customerId", customerId)
                args.putDouble("amount", it.Result.TotalPrice)// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
                args.putString("order_id", it.Result.OrderId)
                args.putString("email", (activity as UpgradeActivity).email)
                args.putString("currency", "INR");
                args.putString("contact", (activity as UpgradeActivity).mobileNo)
                paymentFragment.arguments = args
                (activity as UpgradeActivity).addFragment(
                        paymentFragment,
                        Constants.PAYMENT_FRAGMENT
                )
            }
        })

        viewModel.getLoaderStatus().observe(this, Observer {
            if (it) {
                val status = viewModel.getAPIRequestStatus()
                progressDialog.setMessage(status)
                progressDialog.setCancelable(false) // disable dismiss by tapping outside of the dialog
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })

        viewModel.getGSTIN().observe(this, Observer {
            if (it != null) {
                Log.i("getGSTIN >> ", it)
                GSTINNumber = it
                gstin_layout1.visibility = View.GONE
                gstin_layout2.visibility = View.VISIBLE
                fill_in_gstin_value.setText(it)
            }
        })

        viewModel.getTAN().observe(this, Observer {
            if (it != null) {
                Log.i("getTAN >> ", it)
                TANNumber = it
                enter_tan_number.visibility = View.GONE
                entered_tan_number.visibility = View.VISIBLE
                entered_tan_number.setText(it)
            }
        })

        //getting all features
        viewModel.updateAllFeaturesResult().observe(this, Observer {
            featuresList = it
        })

        //getting all bunles
        viewModel.updateAllBundlesResult().observe(this, Observer {
            bundlesList = it
        })

        //getting valid Coupon Code
        viewModel.updateValidCouponResult().observe(this, Observer {
            validCouponCode = it
            discount_coupon_title.setText(validCouponCode!!.coupon_key)
            cart_apply_coupon.visibility = View.GONE
            totalCalculation()
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


    fun totalCalculation() {
        total = 0.0
        var couponDisount = 0
        if (validCouponCode != null) {
            couponDisount = validCouponCode!!.discount_percent!!
            coupon_discount_title.setText("Coupon discount(" + couponDisount.toString() + "%)")
        }
        if (cartList != null && cartList.size > 0) {
            for (item in cartList) {
                total += item.price * item.min_purchase_months
            }
            cart_amount_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(total))
            couponDiscountAmount = total * couponDisount / 100
            coupon_discount_value.setText("-₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(couponDiscountAmount))
            total -= couponDiscountAmount
            val temp = (total * 18) / 100
            taxValue = Math.round(temp * 100) / 100.0
            grandTotal = (Math.round((total + taxValue) * 100) / 100.0)
            igst_value.setText("+₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(taxValue))
            order_total_value.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal))
            cart_grand_total.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal))
            footer_grand_total.setText("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(grandTotal))
        }
    }


    override fun deleteCartAddonsItem(itemID: String) {
        viewModel.deleteCartItems(itemID)
    }

}
