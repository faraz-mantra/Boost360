package com.boost.dbcenterapi.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import android.text.TextUtils
import com.boost.dbcenterapi.data.api_model.gst.Result
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.dbcenterapi.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("CommitPrefEdits")
class SharedPrefs(activity: Activity) {

  private val INITIAL_LOAD = "Initial_Load_App"

  private val po_id = "Last_Purchase_Order_Created"
  private val po_status = "Last_Purchase_Order_Status"
  private val trn_id = "Last_Transaction_Id_From_Cart"
  private val po_feature_keys = "Last_Purchase_Order_Feature_Keys"
  private val po_feature_count = "Last_Purchase_Order_Feature_Count"
  private val po_price = "Last_Purchase_Order_Price"
  private val po_payment_success = "Last_payment_status"

  private val fp_email = "GET_FP_DETAILS_EMAIL"

  private val CART_ORDER_INFO = "CART_ORDER_INFO"
  private val CART_IDS = "CART_IDS"
  private val COUPON_IDS = "COUPON_IDS"
  private val CART_COUPON_DETAILS = "CART_COUPON_DETAILS"
  private val ADDON_DETAILS = "ADDON_DETAILS"

  private val VALIDITY_MONTHS = "VALIDITY_MONTHS"

  private val temp_cartAmount = "Cart_Orig_Price"
  private val temp_couponDiscount = "Coupon_Discount"
  private val temp_monthsValidity = "Months_validity"
  private val compareStatus = "compareStatus"
  private val gstRegistered = "gstRegistered"
  private val yearPricing = "yearPricing"
  private val expertContact = "expertContact"
  private val ADDED_PACK_DESC = "ADDED_PACK_DESC"
  private val GST_API_RESULT = "GST API RESULT"
  private val LAST_USED_PAYMENT_MODE = "LAST_USED_PAYMENT_MODE"
  private val CART_VALIDITY_MONTHS = "CART_VALIDITY_MONTHS"

  private var editor: SharedPreferences.Editor? = null

  var pref: SharedPreferences? = null

  init {
    pref = activity.getSharedPreferences("nowfloatsPrefs", 0)
    editor = pref!!.edit()
  }

  //initial load market place verification
  fun storeInitialLoadMarketPlace(state: Boolean) {
    editor!!.putBoolean(INITIAL_LOAD, state).apply()
  }

  fun getInitialLoadMarketPlace(): Boolean {
    return pref!!.getBoolean(INITIAL_LOAD, true)
  }

  fun storeLatestPurchaseOrderTotalPrice(price: Float) {
    editor!!.putFloat(po_price, price).apply()
  }

  fun storeLatestPurchaseOrderId(order_id: String) {
    editor!!.putString(po_id, order_id).apply()
  }

  fun storeLatestOrderStatus(status: Int) {
    editor!!.putInt(po_status, status).apply()
  }

  fun storeTransactionIdFromCart(transaction_id: String) {
    editor!!.putString(trn_id, transaction_id).apply()
  }

  fun storeFeatureKeysInLastOrder(keys: Set<String>) {
    editor!!.putStringSet(po_feature_keys, keys).apply()
  }

  fun storeFeaturesCountInLastOrder(count: Int) {
    editor!!.putInt(po_feature_count, count).apply()
  }

  fun storeLastUsedPaymentMode(lastPaymentMethodDetails: String) {
    editor!!.putString(LAST_USED_PAYMENT_MODE, lastPaymentMethodDetails).apply()
  }

  fun getLatestPurchaseOrderId(): String? {
    return pref!!.getString(po_id, null)
  }

  fun getLatestOrderStatus(): Int {
    return pref!!.getInt(po_status, 0)
  }

  fun getTransactionIdFromCart(): String? {
    return pref!!.getString(trn_id, null)
  }

  fun getFeaturesCountInLastOrder(): Int {
    return pref!!.getInt(po_feature_count, 0)
  }

  fun getLatestPurchaseOrderTotalPrice(): Float {
    return pref!!.getFloat(po_price, 0f)
  }

  fun getLastUsedPaymentMode(): String? {
    return pref!!.getString(LAST_USED_PAYMENT_MODE, null)
  }

  //tempStorage
  fun storeCartOriginalAmount(value: Float) {
    editor!!.putFloat(temp_cartAmount, value).apply()
  }

  fun getCartOriginalAmount(): Float {
    return pref!!.getFloat(temp_cartAmount, 0f)
  }

  fun storeCouponDiscountPercentage(value: Double) {
    editor!!.putFloat(temp_couponDiscount, value.toFloat()).apply()
  }

  fun getCouponDiscountPercentage(): Double {
    return pref!!.getFloat(temp_couponDiscount, 0.0f).toDouble()
  }

  //storing orderInfo
  fun storeCartOrderInfo(orderDetails: CreatePurchaseOrderResponse?) {
    val orderInfo = Gson().toJson(orderDetails)
    editor!!.putString(CART_ORDER_INFO, orderInfo).apply()
  }

  fun getCartOrderInfo(): CreatePurchaseOrderResponse? {
    val jsonString = pref!!.getString(CART_ORDER_INFO, null)
    if (jsonString != null) {
      return Gson().fromJson<CreatePurchaseOrderResponse>(jsonString, object : TypeToken<CreatePurchaseOrderResponse>() {}.type)
    } else {
      return null
    }
  }

  fun storeGstApiResponse(result: Result?) {
    val gstResult = Gson().toJson(result)
    editor!!.putString(GST_API_RESULT, gstResult).apply()
  }

  fun getGstApiResponse(): Result? {
    val jsonString = pref!!.getString(GST_API_RESULT, null)
    if (jsonString != null) {
      return Gson().fromJson<Result>(jsonString, object : TypeToken<Result>() {}.type)
    } else {
      return null
    }
  }

  fun storeCardIds(orderDetails: List<String?>?) {
    val orderInfo = Gson().toJson(orderDetails)
    editor!!.putString(CART_IDS, orderInfo).apply()
  }

  fun getCardIds(): List<String?>? {
    val str = pref!!.getString(CART_IDS, "")
    return if (TextUtils.isEmpty(str)) ArrayList() else Gson().fromJson(str, object : TypeToken<List<String?>?>() {}.type)
  }

  fun storeValidityMonths(orderDetails: String?) {
    editor!!.putString(VALIDITY_MONTHS, orderDetails).apply()
  }

  fun getValidityMonths(): String? {
    val str = pref!!.getString(VALIDITY_MONTHS, "")
    return str
  }

  fun storeCouponIds(couponCode: String?) {
    val orderInfo = Gson().toJson(couponCode)
    editor!!.putString(COUPON_IDS, orderInfo).apply()
  }

  fun getCouponIds(): String? {
    val str = pref!!.getString(COUPON_IDS, "")
    return str
  }

  fun storeOrderSuccessFlag(value: Boolean) {
    editor!!.putBoolean(po_payment_success, value).apply()
  }

  //storing couponDetails
  fun storeApplyedCouponDetails(couponDetails: CouponsModel?) {
    val couponInfo = Gson().toJson(couponDetails)
    editor!!.putString(CART_COUPON_DETAILS, couponInfo).apply()
  }

  fun getApplyedCouponDetails(): CouponsModel? {
    val jsonString = pref!!.getString(CART_COUPON_DETAILS, null)
    if (jsonString != null) {
      return Gson().fromJson<CouponsModel>(jsonString, object : TypeToken<CouponsModel>() {}.type)
    } else {
      return null
    }
  }

  //storing addonDetails
  fun storeAddonDetails(addonDetails: FeaturesModel?) {
    val couponInfo = Gson().toJson(addonDetails)
    editor!!.putString(ADDON_DETAILS, couponInfo).apply()
  }

  fun getAddonDetails(): FeaturesModel? {
    val jsonString = pref!!.getString(ADDON_DETAILS, null)
    if (jsonString != null) {
      return Gson().fromJson<FeaturesModel>(jsonString, object : TypeToken<FeaturesModel>() {}.type)
    } else {
      return null
    }
  }

  fun getFPEmail(): String {
    val email = pref!!.getString(fp_email, "")
    return email!!
  }

  fun storeMonthsValidity(monthsValidity: Int) {
    editor!!.putInt(temp_monthsValidity, monthsValidity).apply()
  }

  fun getStoreMonthsValidity(): Int {
    return pref!!.getInt(temp_monthsValidity, 0)
  }
  fun storeCartValidityMonths(cartValidityMonths : String?){
    editor!!.putString(CART_VALIDITY_MONTHS,cartValidityMonths).apply()
  }
  fun getCartValidityMonths():String?{
    return pref!!.getString(CART_VALIDITY_MONTHS,null)
  }

  fun storeAddedPackageDesc(description: String) {
    editor!!.putString(ADDED_PACK_DESC, description).apply()
  }

  fun getStoreAddedPackageDesc(): String? {
    return pref!!.getString(ADDED_PACK_DESC, null)
  }

  fun storeCompareState(value: Int) {
    editor!!.putInt(compareStatus, value).apply()
  }

  fun getCompareState(): Int {
    return pref!!.getInt(compareStatus, 0)
  }

  fun storeGstRegistered(value: Boolean) {
    editor!!.putBoolean(gstRegistered, value).apply()
  }

  fun getGstRegistered(): Boolean {
    return pref!!.getBoolean(gstRegistered, true)
  }

  fun storeExpertContact(value: String) {
    editor!!.putString(expertContact, value).apply()
  }

  fun getExpertContact(): String? {
    return pref!!.getString(expertContact, null)
  }

  fun storeYearPricing(value: Boolean){
    editor!!.putBoolean(yearPricing, value).apply()
  }

  fun getYearPricing(): Boolean {
    return pref!!.getBoolean(yearPricing, true)
  }

}