package com.boost.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.interfaces.ApplyCouponListener
import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data
import java.text.DecimalFormat

class CartCouponAdapter(val applyCouponListener: ApplyCouponListener)
    : RecyclerView.Adapter<CartCouponAdapter.ViewHolder>() {

    private var mList: List<Data> = arrayListOf()
    var total: Double = 0.0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartCouponAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_coupon_code, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartCouponAdapter.ViewHolder, position: Int) {
        val offerCoupons = mList[position]
        holder.couponCodetextView.text = offerCoupons.code
        val amount = (total * (offerCoupons.discountPercent!! / 100.0f))
        val df = DecimalFormat()
        df.setMaximumFractionDigits(2)
        holder.cashBacktextView.text = "Max Save: ₹" + df.format(amount)
        var date = mList[position].termsandconditions?.split("till ")
        var expiredDate = date?.get(1)
        var percent = mList[position].title?.split(" ")
        var savePercent = percent?.get(1)
        holder.learnMoretextView.text = "Extra " + savePercent + " off if you purchase a pack. Cannot be combined with any other offer, expires on " + expiredDate
        holder.apply_coupon.setOnClickListener {
            applyCouponListener.applycoupon(mList.get(position))
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateCouponList(mList: List<Data>,total: Double){
        this.mList = mList
        this.total = total
        notifyDataSetChanged()
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val couponCodetextView: TextView = itemView.findViewById(R.id.offer_coupn_code_tv)
        val cashBacktextView: TextView = itemView.findViewById(R.id.offer_cb_tv)
        val learnMoretextView: TextView = itemView.findViewById(R.id.offer_learn_more_tv)
        val apply_coupon: LinearLayoutCompat = itemView.findViewById(R.id.apply_coupon)

    }
}