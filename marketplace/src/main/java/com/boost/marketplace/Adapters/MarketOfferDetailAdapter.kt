package com.boost.marketplace.Adapters

import android.content.Context
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R


class MarketOfferDetailAdapter(itemList: ArrayList<String>?) :
  RecyclerView.Adapter<MarketOfferDetailAdapter.upgradeViewHolder>() {

  private var list = ArrayList<String>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<String>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_marketplaceoffers_info, parent, false
    )
    context = itemView.context
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    Log.v("onBindViewHolder", " " + list.get(position).isNotEmpty())
//        if (list.get(position) != null || list.get(position).isNotEmpty()) {
    holder.offer_details.setText(Html.fromHtml(list.get(position)))
//            holder.offer_details.setText(list.get(position))
//        }


  }


  fun addupdates(purchaseResult: List<String>) {
    val initPosition = list.size
    list.clear()
    list.addAll(purchaseResult)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var offer_details = itemView.findViewById<TextView>(R.id.offer_details)!!
  }
}