package com.appservice.ui.paymentgateway

import android.os.Build
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.BulletSpan
import android.view.View
import androidx.annotation.RequiresApi
import com.appservice.R
import com.appservice.databinding.BottomSheetTermsPaymentGatewayBinding
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel


class WhyPaymentBottomSheet :
  BaseBottomSheetDialog<BottomSheetTermsPaymentGatewayBinding, BaseViewModel>() {


  var onClicked: () -> Unit = { }
  override fun getLayout(): Int {
    return R.layout.bottom_sheet_terms_payment_gateway
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.understoodBtnPaymentGateway)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) createBulletedList()
    else {
      val point1 = "●  ${getText(R.string.payment_gateway_terms_point_one)}"
      val point2 = "●  ${getText(R.string.payment_gateway_terms_point_two)}"
      val point3 = "●  ${getText(R.string.payment_gateway_terms_point_three)}"
      val point4 = "●  ${getText(R.string.payment_gateway_terms_point_four)}"
      val point5 = "●  ${getText(R.string.payment_gateway_terms_point_five)}"
      binding?.tvTermsAndConditions?.text = TextUtils.concat(point1, point2, point3, point4, point5)
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.understoodBtnPaymentGateway -> dismiss()
    }
  }

  @RequiresApi(Build.VERSION_CODES.P)
  private fun createBulletedList() {
    val point1 = getText(R.string.payment_gateway_terms_point_one)
    val s1 = SpannableString(point1)
    s1.setSpan(BulletSpan(20, resources.getColor(R.color.grey_787878), 8), 0, point1.length, 0)
    val point2 = getText(R.string.payment_gateway_terms_point_two)
    val s2 = SpannableString(point2)
    s2.setSpan(BulletSpan(20, resources.getColor(R.color.grey_787878), 8), 0, point2.length, 0)
    val point3 = getText(R.string.payment_gateway_terms_point_three)
    val s3 = SpannableString(point3)
    s3.setSpan(BulletSpan(20, resources.getColor(R.color.grey_787878), 8), 0, point3.length, 0)
    val point4 = getText(R.string.payment_gateway_terms_point_four)
    val s4 = SpannableString(point4)
    s4.setSpan(BulletSpan(20, resources.getColor(R.color.grey_787878), 8), 0, point4.length, 0)
    val point5 = getText(R.string.payment_gateway_terms_point_five)
    val s5 = SpannableString(point5)
    s5.setSpan(BulletSpan(20, resources.getColor(R.color.grey_787878), 8), 0, point5.length, 0)
    binding?.tvTermsAndConditions?.text = TextUtils.concat(s1, s2, s3, s4, s5)
  }
}