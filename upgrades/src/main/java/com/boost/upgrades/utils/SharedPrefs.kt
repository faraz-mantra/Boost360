package com.boost.upgrades.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class SharedPrefs(activity: Activity) {

    private val INITIAL_LOAD = "Initial_Load_App"

    private val po_id = "Last_Purchase_Order_Created"
    private val po_status = "Last_Purchase_Order_Status"
    private val pmt_id = "Last_Purchase_Order_Payment_Id"
    private val po_feature_count = "Last_Purchase_Order_Feature_Count"
    private val po_price = "Last_Purchase_Order_Price"

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


}