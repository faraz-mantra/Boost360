package com.boost.marketplace.ui.home.MarketplaceV2_ComparePacks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.marketplace.R

class OfferTermsAdapter(val termsconditions:List<String>) :
  RecyclerView.Adapter<OfferTermsAdapter.upgradeViewHolder>()
{
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.marketplaceoffers_info_item, parent, false)
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return termsconditions.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    holder.offer_details.text=termsconditions[position]
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var offer_details = itemView.findViewById<TextView>(R.id.offer_details)!!
  }
}