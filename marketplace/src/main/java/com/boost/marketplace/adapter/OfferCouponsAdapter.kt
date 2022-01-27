package com.boost.marketplace.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R
import com.boost.marketplace.data.OfferCoupons

class OfferCouponsAdapter(private val mList: List<OfferCoupons>) : RecyclerView.Adapter<OfferCouponsAdapter.ViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferCouponsAdapter.ViewHolder {
    val view = LayoutInflater.from(parent.context)
      .inflate(R.layout.item_offer_coupons, parent, false)

    return ViewHolder(view)
  }

  override fun onBindViewHolder(holder: OfferCouponsAdapter.ViewHolder, position: Int) {
    val offerCoupons = mList[position]
    holder.couponCodetextView.text =offerCoupons.couponCode
    holder.cashBacktextView.text =offerCoupons.cashback
    holder.learnMoretextView.text =offerCoupons.learnMore

  }

  override fun getItemCount(): Int {
    return mList.size
  }

  class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
    val couponCodetextView: TextView = itemView.findViewById(R.id.offer_coupn_code_tv)
    val cashBacktextView: TextView = itemView.findViewById(R.id.offer_cb_tv)
    val learnMoretextView: TextView = itemView.findViewById(R.id.offer_learn_more_tv)

  }
}