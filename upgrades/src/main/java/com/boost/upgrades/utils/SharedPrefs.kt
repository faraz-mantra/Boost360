package com.boost.upgrades.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class SharedPrefs(activity: Activity) {

    private val po_id = "Last_Purchase_Order_Created"
    private val po_status = "Last_Purchase_Order_Status"
    private val pmt_id = "Last_Purchase_Order_Payment_Id"
    private val po_feature_count = "Last_Purchase_Order_Feature_Count"
    private val po_price = "Last_Purchase_Order_Price"

    private var editor: SharedPreferences.Editor? = null

    var pref: SharedPreferences? = null

     init {
        pref  = activity.getSharedPreferences( "nowfloatsPrefs", 0)
        editor = pref!!.edit()
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
}