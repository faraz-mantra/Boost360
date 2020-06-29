package com.boost.upgrades.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences
import com.boost.upgrades.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.upgrades.data.api_model.PurchaseOrder.response.CreatePurchaseOrderResponse
import com.boost.upgrades.data.model.CouponsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@SuppressLint("CommitPrefEdits")
class SharedPrefs(activity: Activity) {

    private val INITIAL_LOAD = "Initial_Load_App"

    private val po_id = "Last_Purchase_Order_Created"
    private val po_status = "Last_Purchase_Order_Status"
    private val pmt_id = "Last_Purchase_Order_Payment_Id"
    private val po_feature_keys = "Last_Purchase_Order_Feature_Keys"
    private val po_feature_count = "Last_Purchase_Order_Feature_Count"
    private val po_price = "Last_Purchase_Order_Price"
    private val po_payment_success = "Last_payment_status"

    private val CART_ORDER_INFO = "CART_ORDER_INFO"
    private val CART_COUPON_DETAILS = "CART_COUPON_DETAILS"

    private val temp_cartAmount = "Cart_Orig_Price"
    private val temp_couponDiscount = "Coupon_Discount"

    private var editor: SharedPreferences.Editor? = null

    var pref: SharedPreferences? = null

     init {
        pref  = activity.getSharedPreferences( "nowfloatsPrefs", 0)
        editor = pref!!.edit()
    }

    //initial load market place verification
    fun storeInitialLoadMarketPlace(state: Boolean){
        editor!!.putBoolean(INITIAL_LOAD, state).apply()
    }

    fun getInitialLoadMarketPlace(): Boolean{
        return pref!!.getBoolean(INITIAL_LOAD, true)
    }

    fun storeLatestPurchaseOrderTotalPrice(price: Float){
        editor!!.putFloat(po_price, price).apply()
    }

    fun storeLatestPurchaseOrderId(order_id: String){
        editor!!.putString(po_id, order_id).apply()
    }

    fun storeLatestOrderStatus(status: Int){
        editor!!.putInt(po_status, status).apply()
    }

    fun storeLatestPaymentIdFromPG(payment_id: String){
        editor!!.putString(pmt_id, payment_id).apply()
    }

    fun storeFeatureKeysInLastOrder(keys: Set<String>){
        editor!!.putStringSet(po_feature_keys, keys).apply()
    }

    fun storeFeaturesCountInLastOrder(count: Int){
        editor!!.putInt(po_feature_count, count).apply()
    }


    fun getLatestPurchaseOrderId(): String?{
        return pref!!.getString(po_id, null)
    }

    fun getLatestOrderStatus(): Int{
        return pref!!.getInt(po_status, 0)
    }

    fun getLatestPaymentIdFromPG(): String?{
        return pref!!.getString(pmt_id, null)
    }

    fun getFeaturesCountInLastOrder(): Int{
        return pref!!.getInt(po_feature_count, 0)
    }

    fun getLatestPurchaseOrderTotalPrice(): Float{
        return pref!!.getFloat(po_price, 0f)
    }

    //tempStorage
    fun storeCartOriginalAmount(value: Float){
        editor!!.putFloat(temp_cartAmount, value).apply()
    }

    fun getCartOriginalAmount(): Float{
        return pref!!.getFloat(temp_cartAmount, 0f)
    }

    fun storeCouponDiscountPercentage(value: Int){
        editor!!.putInt(temp_couponDiscount, value).apply()
    }

    fun getCouponDiscountPercentage(): Int{
        return pref!!.getInt(temp_couponDiscount, 0)
    }

    //storing orderInfo
    fun storeCartOrderInfo(orderDetails: CreatePurchaseOrderResponse?){
        val orderInfo = Gson().toJson(orderDetails)
        editor!!.putString(CART_ORDER_INFO, orderInfo).apply()
    }

    fun getCartOrderInfo(): CreatePurchaseOrderResponse?{
        val jsonString = pref!!.getString(CART_ORDER_INFO, null)
        if(jsonString!=null) {
            return Gson().fromJson<CreatePurchaseOrderResponse>(jsonString, object : TypeToken<CreatePurchaseOrderResponse>() {}.type)
        }else{
            return null
        }
    }

    fun storeOrderSuccessFlag(value: Boolean){
        editor!!.putBoolean(po_payment_success, value).apply()
    }

    //storing couponDetails
    fun storeApplyedCouponDetails(couponDetails: CouponsModel?){
        val couponInfo = Gson().toJson(couponDetails)
        editor!!.putString(CART_COUPON_DETAILS, couponInfo).apply()
    }

    fun getApplyedCouponDetails(): CouponsModel?{
        val jsonString = pref!!.getString(CART_COUPON_DETAILS, null)
        if(jsonString!=null) {
            return Gson().fromJson<CouponsModel>(jsonString, object : TypeToken<CouponsModel>() {}.type)
        }else{
            return null
        }
    }




}