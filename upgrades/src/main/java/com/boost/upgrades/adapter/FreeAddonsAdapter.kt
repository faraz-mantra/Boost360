package com.boost.upgrades.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.upgrades.interfaces.MyAddonsListener
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.utils.Constants
import com.bumptech.glide.Glide


class FreeAddonsAdapter(
  val activity: UpgradeActivity,
  itemList: List<FeaturesModel>?, var myAddonsListener: MyAddonsListener
) : RecyclerView.Adapter<FreeAddonsAdapter.upgradeViewHolder>(), View.OnClickListener {

  private var list = ArrayList<FeaturesModel>()
  private lateinit var context: Context

  init {
    this.list = itemList as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.single_free_addon, parent, false
    )
    context = itemView.context


    itemView.setOnClickListener(this)
    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size //5
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = list[position]
    holder.upgradeListItem(cryptocurrencyItem)

    holder.itemView.setOnClickListener {
      val details = DetailsFragment.newInstance()
      val args = Bundle()
      args.putString("itemId", list.get(position).feature_code)
      details.arguments = args
      activity.addFragment(details, Constants.DETAILS_FRAGMENT)
    }
  }

  override fun onClick(v: View?) {
    myAddonsListener.onFreeAddonsClicked(v)
  }

  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var singleTitle = itemView.findViewById<TextView>(R.id.single_freeaddon_text)!!
    private var image = itemView.findViewById<ImageView>(R.id.single_freeaddon_image)!!

    private var context: Context = itemView.context


    fun upgradeListItem(updateModel: FeaturesModel) {
      singleTitle.text = updateModel.name
      Glide.with(context).load(updateModel.primary_image).into(image)

    }
  }
}