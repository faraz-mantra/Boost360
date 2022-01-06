package com.boost.marketplace.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R

class BrowseFeaturesAdapter(list:List<FeaturesModel>): RecyclerView.Adapter<BrowseFeaturesAdapter.ViewHolder>() {

  private var featuresList = ArrayList<FeaturesModel>()

  init {
    this.featuresList = list as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val item = View.inflate(parent.context, R.layout.item_browse_features, null)
    return ViewHolder(item)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    TODO("Not yet implemented")
  }

  override fun getItemCount(): Int {
    return featuresList.size
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//    val image = itemView.findViewById<ImageView>(R.id.preview_image)

  }

}