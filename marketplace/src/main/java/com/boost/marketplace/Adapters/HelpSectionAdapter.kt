package com.boost.marketplace.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R


class HelpSectionAdapter(videoList: List<YoutubeVideoModel>?) :
  RecyclerView.Adapter<HelpSectionAdapter.upgradeViewHolder>() {

  private lateinit var context: Context
  private var list = ArrayList<YoutubeVideoModel>()

  init {
    list = videoList as ArrayList<YoutubeVideoModel>
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

  fun addUpdates(videoList: List<YoutubeVideoModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(videoList)
    notifyItemRangeInserted(initPosition, list.size)
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    if (list.get(position).youtube_link != null && list.get(position).youtube_link!!.isNotEmpty())
      holder.offer_details.setText(list.get(position).title)
  }


  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var offer_details = itemView.findViewById<TextView>(R.id.offer_details)!!
  }
}