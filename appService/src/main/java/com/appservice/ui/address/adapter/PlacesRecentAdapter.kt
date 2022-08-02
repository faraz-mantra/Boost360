package com.appservice.ui.address.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.google.android.libraries.places.api.model.AutocompletePrediction
import kotlinx.android.synthetic.main.item_recent_searches.view.*

class PlacesRecentAdapter(
  var mContext: Context,
  var mResultList: ArrayList<AutocompletePrediction>,
  val onClick: (prediction: AutocompletePrediction) -> Unit,
) : RecyclerView.Adapter<PlacesRecentAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recent_searches, parent, false)
    return ViewHolder(view)
  }

  override fun getItemCount(): Int {
    return mResultList.size
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.onBind(position)
    holder.itemView.setOnClickListener {
      onClick(mResultList.get(position))
    }
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun onBind(position: Int) {
      val res = mResultList.get(position)
      itemView.apply {
        tv_address_title.text = res.getPrimaryText(null)
      }
    }
  }

}
