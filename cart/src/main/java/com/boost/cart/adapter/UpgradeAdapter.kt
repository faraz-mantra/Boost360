package com.boost.cart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*


class UpgradeAdapter(
  cryptoCurrencies: List<FeaturesModel>?
) : RecyclerView.Adapter<UpgradeAdapter.upgradeViewHolder>() {

  private var upgradeList = ArrayList<FeaturesModel>()
  private lateinit var context: Context

  init {
    this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_others_also_bought, parent, false
    )
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    itemView.layoutParams = lp
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return upgradeList.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val cryptocurrencyItem = upgradeList[position]
    holder.upgradeListItem(cryptocurrencyItem)

//    holder.itemView.setOnClickListener {
////      val details = DetailsFragment.newInstance()
////      val args = Bundle()
////      args.putString("itemId", upgradeList.get(position).feature_code)
////      details.arguments = args
////      activity.addFragment(details, Constants.DETAILS_FRAGMENT)
//
//            val intent = Intent(this.context, FeatureDetailsActivity::class.java)
//            intent.putExtra("itemId", upgradeList.get(position).feature_code)
//            startActivity(this.context, intent, null)
//    }
  }

  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = upgradeList.size
    upgradeList.clear()
    upgradeList.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, upgradeList.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var upgradeDetails = itemView.findViewById<TextView>(R.id.title)!!
    private var upgradePrice = itemView.findViewById<TextView>(R.id.price)!!
    private var image = itemView.findViewById<ImageView>(R.id.imageView2)!!

    private var context: Context = itemView.context

    fun upgradeListItem(updateModel: FeaturesModel) {
      val discount = 100 - updateModel.discount_percent
      val price = (discount * updateModel.price) / 100
      upgradeDetails.text = updateModel.name
      upgradePrice.text =
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"
      if (updateModel.primary_image != null) {
        Glide.with(context).load(updateModel.primary_image).into(image)
      }
    }
  }
}