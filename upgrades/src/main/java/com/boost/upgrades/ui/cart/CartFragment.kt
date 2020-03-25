package com.boost.upgrades.ui.cart

import android.app.ProgressDialog
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.CartAddonsAdaptor
import com.boost.upgrades.adapter.CartPackageAdaptor
import com.boost.upgrades.data.api_model.PurchaseOrder.CreatePurchaseOrderRequest
import com.boost.upgrades.data.api_model.PurchaseOrder.PaymentDetails
import com.boost.upgrades.data.api_model.PurchaseOrder.Widget
import com.boost.upgrades.data.api_model.customerId.create.CustomerIDRequest
import com.boost.upgrades.data.model.Cart
import com.boost.upgrades.database.LocalStorage
import com.boost.upgrades.interfaces.CartFragmentListener
import com.boost.upgrades.ui.payment.PaymentFragment
import com.boost.upgrades.ui.popup.CouponPopUpFragment
import com.boost.upgrades.utils.Constants
import com.boost.upgrades.utils.Constants.Companion.COUPON_POPUP_FRAGEMENT
import kotlinx.android.synthetic.main.cart_fragment.*

class CartFragment : BaseFragment(), CartFragmentListener {

    lateinit var root: View

    lateinit var localStorage: LocalStorage

    var customerId: String? = null

    var total = 0f

    lateinit var progressDialog: ProgressDialog

    //    private var cartAdapter = CartAdapter(ArrayList())
    lateinit var cartPackageAdaptor: CartPackageAdaptor
    lateinit var cartAddonsAdaptor: CartAddonsAdaptor

    val couponPopUpFragment = CouponPopUpFragment()

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

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CartViewModel::class.java)

        loadData()
        initMvvM()
        spannableString()
        initializePackageRecycler()
        initializeAddonsRecycler()

        cart_continue_submit.setOnClickListener {
            customerId = viewModel.getCustomerId()
            if (customerId != null) {
                viewModel.InitiatePurchaseOrder(
                        CreatePurchaseOrderRequest(
                                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
                                "5e6b572c959b7e77e0c5883e",
                                PaymentDetails(20.66, "GST", 0, 1, 0, 0, 18, 1000.25),
                                listOf(
                                        Widget(
                                                "SERVICES",
                                                "TIME",
                                                "Description",
                                                0,
                                                30,
                                                listOf(),
                                                "Latest Updates",
                                                1000,
                                                1,
                                                "TOB"
                                        )
                                )
                        )
                )
            } else {
                Toast.makeText(requireContext(), "CustomerID is NULL", Toast.LENGTH_SHORT).show()
            }
//            val intent = Intent(this, Payment::class.java)
//            startActivity(intent)
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
    }

    fun loadData() {
        viewModel.requestCustomerId(
//                "5e6b572c959b7e77e0c5883e",
//                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
                CustomerIDRequest(
                        "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
                        0,
                        "tanmay.majumdar@nowfloats.com",
                        "5e6b572c959b7e77e0c5883e",
                        "Tanmay",
                        "9738084090",
                        0
                )
        )
        viewModel.getCartItems()
    }

    fun initMvvM() {
        viewModel.cartResult().observe(this, Observer {
            if (it != null && it.size > 0) {
                empty_cart.visibility = View.GONE
                cart_main_layout.visibility = View.VISIBLE
                cartAddonsAdaptor.addupdates(it)
                cartAddonsAdaptor.notifyDataSetChanged()
                totalCalculation(it)
            } else {
                empty_cart.visibility = View.VISIBLE
                cart_main_layout.visibility = View.GONE
            }
        })

        viewModel.getPurchaseOrderResponse().observe(this, Observer {
            if (it != null) {
                val paymentFragment = PaymentFragment.newInstance()
                val args = Bundle()
                args.putString("customerId", customerId)
                args.putInt("amount", (total * 100).toInt())// pass in currency subunits. For example, paise. Amount: 1000 equals ₹10
//                args.putString("order_id", "order_DgZ26rHjbzLLY2") //For testing dont send order Id
                args.putString("order_id", it.OrderId)
                args.putString("email", "gaurav.kumar@example.com")
                args.putString("currency", "INR");
                args.putString("contact", "9876543210")
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
    }

    fun totalCalculation(list: List<Cart>) {
        if (list != null && list.size > 0) {
            for (item in list) {
                total = total + item.price
            }
            cart_amount_value.setText("₹" + total.toString())
            coupon_discount_value.setText("0")
            order_total_value.setText((total + 0).toString())
            cart_grand_total.setText("₹" + total.toString())
            footer_grand_total.setText("₹" + total.toString())
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
        val origCost = SpannableString("billed on 5th day of every month")

        origCost.setSpan(StyleSpan(Typeface.BOLD), 10, 17, 0)
        billing_date.setText(origCost)
    }

    override fun deleteCartAddonsItem(itemID: String) {
        viewModel.deleteCartItems(itemID)
    }

}
