package com.boost.cart.ui.popup

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
import com.boost.cart.R
import com.boost.cart.CartActivity
import com.boost.dbcenterapi.data.api_model.couponSystem.redeem.RedeemCouponRequest
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.cart.ui.home.CartViewModel
import com.boost.cart.utils.Utils
import com.boost.cart.utils.WebEngageController
import com.framework.webengageconstant.ADDONS_MARKETPLACE_DISCOUNT_COUPON_LOADED
import com.framework.webengageconstant.DISCOUNT_COUPON
import com.framework.webengageconstant.NO_EVENT_VALUE
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.coupon_popup.*
import java.util.*

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
    root = inflater.inflate(R.layout.coupon_dialog, container, false)

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
      if (validateCouponCode()) {
//                if (couponsList.size > 0) {
        viewModel.getCouponRedeem(
          RedeemCouponRequest(
            arguments!!.getDouble("cartValue"),
            entered_coupon_value.text.toString(),
            (activity as CartActivity).fpid!!
          ), entered_coupon_value.text.toString()
        )
        dismiss()
        /*coupon_invalid.visibility = View.GONE
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
        }*/
//                } else {
//                    coupon_invalid.visibility = View.VISIBLE
//                }
      }
    }

    WebEngageController.trackEvent(
      ADDONS_MARKETPLACE_DISCOUNT_COUPON_LOADED,
      DISCOUNT_COUPON,
      NO_EVENT_VALUE
    )

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
      if (entered_coupon_value.text.toString()
          .toUpperCase(Locale.getDefault()) == singleCoupon.coupon_key.toUpperCase(Locale.getDefault())
      ) {
        validCouponCode = singleCoupon
        break
      }
    }
    if (validCouponCode == null) {
      return false
    }
    return true
  }
  override fun onResume() {
    super.onResume()
//    UserExperiorController.startScreen("MarketPlaceCouponPopUpFragment")

  }

}