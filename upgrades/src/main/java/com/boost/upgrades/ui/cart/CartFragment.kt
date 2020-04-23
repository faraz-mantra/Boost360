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
import com.boost.upgrades.data.api_model.PurchaseOrder.request.*
import com.boost.upgrades.data.model.CartModel
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
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CartFragment : BaseFragment(), CartFragmentListener {

    lateinit var root: View

    lateinit var localStorage: LocalStorage

    var customerId: String = ""

    lateinit var cartList: List<CartModel>

    var total = 0.0

    var grandTotal = 0.0

    var GSTINNumber: String? = null
    var TANNumber: String? = null

    var taxValue = 0.0

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

        cartPackageAdaptor = CartPackageAdaptor(ArrayList())
        cartAddonsAdaptor = CartAddonsAdaptor(ArrayList(), this)

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Cart Initialised", "ADDONS_MARKETPLACE Cart", "")

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

        loadData()
        initMvvM()
        spannableString()
        initializePackageRecycler()
        initializeAddonsRecycler()

        cart_continue_submit.setOnClickListener {
            //customerId = viewModel.getCustomerId()
//            customerId != null &&
            if (total > 0 && ::cartList.isInitialized) {
                val widgetsToBeBought = ArrayList<Widget>()
                for (item in cartList) {
                    var extendProps: List<ExtendedProperty>? = null
                    var outputExtendedProps: List<ExtendedProperties>? = null

                    if(item.extended_properties != null && item.extended_properties!!.length > 0){
                        try {
                            val objectType = object : TypeToken<List<ExtendedProperty>>() {}.type
                            extendProps = Gson().fromJson<List<ExtendedProperty>>(item.extended_properties, objectType)

                            if(extendProps != null){
                                outputExtendedProps = ArrayList<ExtendedProperties>()
                                for(prop in extendProps) {
                                    outputExtendedProps.add(ExtendedProperties(
                                            Key = prop.key!!,
                                            Value = prop.value!!
                                    ))
                                }

                            }
                        } catch (ex: Exception){
                            Log.e("FAILED", ex.message)
                        }
                    }
                    widgetsToBeBought.add(Widget(
                            ConsumptionConstraint(
                                    "DAYS",
                                    30
                            ),
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
                            item.MRPPrice.toDouble(),
                            "MONTHLY",
                            item.boost_widget_key,
                            1,
                            outputExtendedProps
                    ))
                }

                var prefs = SharedPrefs(activity as UpgradeActivity)
                prefs.storeFeaturesCountInLastOrder(widgetsToBeBought.count())

                viewModel.InitiatePurchaseOrder(
                        CreatePurchaseOrderRequest(
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
                                widgetsToBeBought,
                                null
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
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvM() {
        viewModel.cartResult().observe(this, Observer {
            if (it != null && it.size > 0) {
                cartList = it
                WebEngageController.trackEvent("ADDONS_MARKETPLACE Full_Cart Loaded", "Cart Size:"+it.size, "")
                empty_cart.visibility = View.GONE
                cart_main_layout.visibility = View.VISIBLE
                cartAddonsAdaptor.addupdates(it)
                cartAddonsAdaptor.notifyDataSetChanged()
                totalCalculation(it)
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
    }

    fun totalCalculation(list: List<CartModel>) {
        total = 0.0
        if (list != null && list.size > 0) {
            for (item in list) {
                total = total + item.price
            }
            cart_amount_value.setText("₹" + total.toString())
            coupon_discount_value.setText("0")
            val temp = (total * 18) / 100
            taxValue = Math.round(temp * 100) / 100.0
            grandTotal = Math.round((total + taxValue) * 100) / 100.0
            igst_value.setText("₹" + taxValue)
            order_total_value.setText("₹" + grandTotal.toString())
            cart_grand_total.setText("₹" + grandTotal.toString())
            footer_grand_total.setText("₹" + grandTotal.toString())
        }
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

    fun spannableString() {
        val billingDay = Utils.getDayOfMonthSuffix(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))

        val origCost = SpannableString("billed on " + billingDay + " day of every month")

        origCost.setSpan(StyleSpan(Typeface.BOLD), 10, 17, 0)
        billing_date.setText(origCost)
    }

    override fun deleteCartAddonsItem(itemID: String) {
        viewModel.deleteCartItems(itemID)
    }

}
