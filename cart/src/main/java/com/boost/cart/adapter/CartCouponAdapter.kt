package com.boost.cart.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.interfaces.ApplyCouponListener
import com.boost.dbcenterapi.data.api_model.getCouponResponse.Data

class CartCouponAdapter(private val mList: List<Data>,private val total:Double,private val applyCouponListener: ApplyCouponListener)
    : RecyclerView.Adapter<CartCouponAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartCouponAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_coupon_code, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartCouponAdapter.ViewHolder, position: Int) {
        val offerCoupons = mList[position]
        holder.couponCodetextView.text =offerCoupons.code
        val amount = (total.toInt()*(offerCoupons.discountPercent!!.toFloat() /100.0f))
        holder.cashBacktextView.text ="Save: ₹"+amount
        var date = mList[position].termsandconditions?.split("till ")
        var expiredDate = date?.get(1)
        var percent = mList[position].title?.split(" ")
        var savePercent = percent?.get(1)
        holder.learnMoretextView.text ="Extra "+savePercent+" off if you purchase a pack. Cannot be combined with any other offer, expires on "+expiredDate
        holder.apply_coupon.setOnClickListener {
            applyCouponListener.applycoupon(mList.get(position))
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val couponCodetextView: TextView = itemView.findViewById(R.id.offer_coupn_code_tv)
        val cashBacktextView: TextView = itemView.findViewById(R.id.offer_cb_tv)
        val learnMoretextView: TextView = itemView.findViewById(R.id.offer_learn_more_tv)
        val apply_coupon:TextView=itemView.findViewById(R.id.apply_coupon)

    }
}