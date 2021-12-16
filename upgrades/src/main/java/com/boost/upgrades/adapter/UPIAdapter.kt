package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.dbcenterapi.upgradeDB.model.*


class UPIAdapter(itemList: List<WidgetModel>?) :
  RecyclerView.Adapter<UPIAdapter.upgradeViewHolder>(), View.OnClickListener {

  private var list = ArrayList<WidgetModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<WidgetModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.upi_payment_item, parent, false
    )
    context = itemView.context


    itemView.setOnClickListener(this)
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {

    holder.view.visibility = View.GONE
  }

  override fun onClick(v: View?) {

  }

  fun addupdates(upgradeModel: List<WidgetModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var view = itemView.findViewById<View>(R.id.upi_view_dummy)!!
    //        private var image = itemView.findViewById<ImageView>(R.id.single_freeaddon_image)!!
//
//        private var context: Context = itemView.context
//
//
//        fun upgradeListItem(updateModel: UpdatesModel) {
//
//        }
  }
}