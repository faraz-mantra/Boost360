package com.boost.upgrades.adapter

import android.app.ActionBar
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.framework.upgradeDB.model.*
import com.boost.upgrades.ui.details.DetailsFragment
import com.boost.upgrades.utils.Constants.Companion.DETAILS_FRAGMENT
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class AllFeatureAdaptor(
  val activity: UpgradeActivity,
  cryptoCurrencies: List<FeaturesModel>?,
  purchasedPackages: ArrayList<String>
) : RecyclerView.Adapter<AllFeatureAdaptor.upgradeViewHolder>() {

  private var upgradeList = ArrayList<FeaturesModel>()
  private lateinit var context: Context
  private var userPurchsedWidgets = ArrayList<String>()

  init {
    this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
    this.userPurchsedWidgets = purchasedPackages as ArrayList<String>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.all_feature_list_item, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return upgradeList.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val item = upgradeList[position]
    holder.upgradeListItem(item, userPurchsedWidgets)

    holder.itemView.setOnClickListener {
      val details = DetailsFragment.newInstance()
      val args = Bundle()
      args.putString("itemId", upgradeList.get(position).feature_code)
      details.arguments = args
      activity.addFragment(details, DETAILS_FRAGMENT)
      Log.v(
        "DETAILS_FRAGMENT",
        " " + upgradeList.get(position).feature_code + " " + upgradeList.get(position).feature_code
      )
//            val intent = Intent(this.context, Details::class.java)
//            intent.putExtra("position",position)
//            ContextCompat.startActivity(this.context, intent, null)
    }

    if (position == 0) {
      holder.upgradeLayout.setBackgroundResource(R.drawable.feature_curve_top_bg)
    }
    if (position > 0 && (upgradeList.size - 1) != position) {
      holder.upgradeLayout.setBackgroundColor(ContextCompat.getColor(context!!, R.color.white))
    }
    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if ((upgradeList.size - 1) == position) {
      holder.view.visibility = View.INVISIBLE
      holder.upgradeLayout.setBackgroundResource(R.drawable.feature_curve_bottom_bg)
    }
  }

  fun addupdates(upgradeModel: List<FeaturesModel>) {
    val initPosition = upgradeList.size
    upgradeList.clear()
    upgradeList.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, upgradeList.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var upgradeLayout = itemView.findViewById<ConstraintLayout>(R.id.cardView)!!
    private var upgradeTitle = itemView.findViewById<TextView>(R.id.title)!!
    private var upgradeDetails = itemView.findViewById<TextView>(R.id.details)!!
    private var upgradePrice = itemView.findViewById<TextView>(R.id.upgrade_list_price)!!
    private var pricingLayout = itemView.findViewById<LinearLayout>(R.id.pricing_layout)!!
    private var upgradeMRP = itemView.findViewById<TextView>(R.id.upgrade_list_orig_cost)!!
    private var upgradeDiscount = itemView.findViewById<TextView>(R.id.upgrade_list_discount)!!
    private var upgradeSubscribedStatus =
      itemView.findViewById<TextView>(R.id.upgrade_list_subbscribed)!!
    private var image = itemView.findViewById<ImageView>(R.id.imageView2)!!
    var view = itemView.findViewById<View>(R.id.view)!!

    private var context: Context = itemView.context


    fun upgradeListItem(updateModel: FeaturesModel, subscribedStatus: ArrayList<String>) {

      val discount = 100 - updateModel.discount_percent
      val price = (discount * updateModel.price) / 100
      upgradeTitle.text = updateModel.target_business_usecase
      upgradeDetails.text = updateModel.name


      if (!checkSubscriptionPresence(updateModel.feature_code!!, subscribedStatus)) {
        upgradeSubscribedStatus.visibility = View.GONE
      }
      if (price > 0) {
        upgradePrice.text =
          "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/month"
        pricingLayout.visibility = View.VISIBLE
//                upgradeSubscribedStatus.visibility = View.GONE
      } else {
//                pricingLayout.visibility = View.GONE
        if (!updateModel.is_premium) {
          upgradePrice.text = "FREE"
          upgradeSubscribedStatus.visibility = View.VISIBLE
        }

      }
      if (updateModel.discount_percent > 0) {
        upgradeDiscount.visibility = View.VISIBLE
        upgradeDiscount.text = "" + updateModel.discount_percent + "%"
        upgradeMRP.text =
          "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(updateModel.price) + "/month"
        upgradeMRP.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
      } else {
        upgradeDiscount.visibility = View.GONE
        upgradeMRP.visibility = View.GONE
      }
      if (updateModel.primary_image != null) {
        Glide.with(context).load(updateModel.primary_image).into(image)
      }
    }

    fun checkSubscriptionPresence(pack: String, availableSub: ArrayList<String>): Boolean {
      for (sub in availableSub) {
        Log.d("checkSubscription", " " + pack + " " + sub)
        if (sub != null) {
//                    if(sub.equals(pack)){
          if (sub == pack) {
            return true
          }
        }

      }
      return false;
    }
  }


}