package com.boost.marketplace.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils.priceCalculatorForYear
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*

class BrowseChildFeaturesAdapter(list:List<FeaturesModel>, val accountType: String, val addonsListener: AddonsListener, val activity: Activity): RecyclerView.Adapter<BrowseChildFeaturesAdapter.ViewHolder>() {

  private var featuresList = ArrayList<FeaturesModel>()
  lateinit var context: Context

  init {
    this.featuresList = list as ArrayList<FeaturesModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val item = View.inflate(parent.context, R.layout.item_pack_list1, null)
    context = item.context
    return ViewHolder(item)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val cryptocurrencyItem = featuresList[position]
    holder.upgradeListItem(cryptocurrencyItem, activity)

    holder.itemView.setOnClickListener {
//      val intent = Intent(this.context, FeatureDetailsActivity::class.java)
//      intent.putExtra("itemId", featuresList.get(position).feature_code)
//      ContextCompat.startActivity(this.context, intent, null)
      addonsListener.onAddonsClicked(featuresList.get(position))
    }
  }

  override fun getItemCount(): Int {
    return featuresList.size
  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val mv_image = itemView.findViewById<ImageView>(R.id.imageView2)
    val mv_title = itemView.findViewById<TextView>(R.id.title)
    val mv_price = itemView.findViewById<TextView>(R.id.price)

    private var context: Context = itemView.context

    fun upgradeListItem(updateModel: FeaturesModel, activity: Activity) {
      val discount = 100 - updateModel.discount_percent
      val price = priceCalculatorForYear(((discount * updateModel.price) / 100), updateModel.widget_type, activity)
      mv_title.text = updateModel.name
      mv_price.text =
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + yearlyOrMonthlyOrEmptyValidity(updateModel.widget_type, activity)
      if (updateModel.primary_image != null) {
        Glide.with(context).load(updateModel.primary_image).into(mv_image)
      }
    }
  }

}