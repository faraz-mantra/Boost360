package com.boost.upgrades.ui.popup

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.upgrades.data.model.CouponsModel
import com.boost.upgrades.ui.cart.CartViewModel
import com.boost.upgrades.ui.compare.ComparePackageFragment
import com.boost.upgrades.utils.Utils
import com.boost.upgrades.utils.WebEngageController
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.add_card_popup.*
import kotlinx.android.synthetic.main.coupon_popup.*
import kotlinx.android.synthetic.main.order_confirmation_fragment.*
import java.util.*
import kotlin.collections.ArrayList

class CouponPopUpFragment : DialogFragment() {

    lateinit var root: View

    private lateinit var viewModel: CartViewModel

    var couponsList: List<CouponsModel> = arrayListOf()

    companion object {
        fun newInstance() = CouponPopUpFragment()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.coupon_popup, container, false)

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(CartViewModel::class.java)

        loadAllCoupons()
        initMvvm()
        entered_coupon_value.setFilters(entered_coupon_value.filters + InputFilter.AllCaps())

        entered_coupon_value.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length < 2) {
                    coupon_invalid.visibility = View.GONE
                }
            }

        })

        coupon_popup_outer_layout.setOnClickListener {
            Utils.hideSoftKeyboard(requireActivity())
            dialog!!.dismiss()
        }

        enter_coupon_layout.setOnClickListener { }

        coupon_submit.setOnClickListener {
            if (validateCouponCode() && validateCouponCodeWithAPI()) {
                if (couponsList.size > 0) {
//                    viewModel.getCouponRedeem(RedeemCouponRequest(arguments!!.getDouble("cartValue"), entered_coupon_value.text.toString(), (activity as UpgradeActivity).fpid!!), entered_coupon_value.text.toString())
//                    dismiss()
                     coupon_invalid.visibility = View.GONE
                     var validCouponCode:CouponsModel? = null
                     for(singleCoupon in couponsList){
                         if(entered_coupon_value.text.toString().toUpperCase(Locale.getDefault()) == singleCoupon.coupon_key.toUpperCase(Locale.getDefault())){
                             validCouponCode = singleCoupon
                             break
                         }
                     }
                     if(validCouponCode==null){
                         coupon_invalid.visibility = View.VISIBLE
                     }else{
                         dismiss()
                         viewModel.addCouponCodeToCart(validCouponCode)
                     }
                } else {
                    coupon_invalid.visibility = View.VISIBLE
                }
            }else if(validateCouponCode() && validateCouponCodeWithAPI().not()){
                viewModel.getCouponRedeem(RedeemCouponRequest(arguments!!.getDouble("cartValue"), entered_coupon_value.text.toString(), (activity as UpgradeActivity).fpid!!), entered_coupon_value.text.toString())
                dismiss()
            }
        }

        WebEngageController.trackEvent("ADDONS_MARKETPLACE Discount_Coupon Loaded", "Discount_Coupon", "")

    }

    fun loadAllCoupons() {
        viewModel.getAllCoupon()
    }

    @SuppressLint("FragmentLiveDataObserve")
    fun initMvvm() {
        viewModel.updateAllCouponsResult().observe(this, Observer {
            couponsList = it
        })
    }

    fun validateCouponCode(): Boolean {
        if (entered_coupon_value.text.isEmpty()) {
            Toasty.error(requireContext(), "Field is Empty!!", Toast.LENGTH_LONG).show();
            return false
        }
        return true
    }

    fun clearData() {
        entered_coupon_value.text.clear()
        coupon_invalid.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        clearData()
    }

    fun validateCouponCodeWithAPI(): Boolean {
        var validCouponCode: CouponsModel? = null
        for (singleCoupon in couponsList) {
            if (entered_coupon_value.text.toString().toUpperCase(Locale.getDefault()) == singleCoupon.coupon_key.toUpperCase(Locale.getDefault())) {
                validCouponCode = singleCoupon
                break
            }
        }
        if (validCouponCode == null) {
            return false
        }
        return true
    }

}