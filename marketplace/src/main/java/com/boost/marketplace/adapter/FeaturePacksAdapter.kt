package com.boost.marketplace.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.details.FeatureDetailsActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class FeaturePacksAdapter(
  var bundleList: ArrayList<BundlesModel>,
  val activity: FeatureDetailsActivity,
  val listener: DetailsFragmentListener
) : RecyclerView.Adapter<FeaturePacksAdapter.upgradeViewHolder>() {
  
  private lateinit var context: Context
  var addonTitle: String = ""

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.item_features_details, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return bundleList.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    holder.title.setText(bundleList.get(position).name)
    if(bundleList.get(position).overall_discount_percent>0) {
      holder.discount.visibility = View.VISIBLE
      holder.discount.setText(bundleList.get(position).overall_discount_percent.toString() +"% saving")
    }else{
      holder.discount.visibility = View.GONE
    }
    getPackageInfoFromDB(holder,bundleList.get(position))
    holder.desc.setText(bundleList.get(position).desc)
    holder.viewPacks.setOnClickListener {
      val item : Bundles = Bundles(
        bundleList.get(position).bundle_id,
        Gson().fromJson<List<IncludedFeature>>(
          bundleList.get(position).included_features!!,
          object : TypeToken<List<IncludedFeature>>() {}.type
        ),
        bundleList.get(position).min_purchase_months,
        bundleList.get(position).name,
        bundleList.get(position).overall_discount_percent,
        PrimaryImage( bundleList.get(position).primary_image!!),
        bundleList.get(position).target_business_usecase,
        Gson().fromJson<List<String>>(
          bundleList.get(position).exclusive_to_categories!!,
          object : TypeToken<List<String>>() {}.type
        ),
        arrayListOf(),
        bundleList.get(position).desc
      )
      listener.onPackageClicked(item)
    }
  }

  fun addupdates(upgradeModel: List<BundlesModel>, addonTitle: String) {
    this.addonTitle = addonTitle
    val initPosition = bundleList.size
    bundleList.clear()
    bundleList.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, bundleList.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var title = itemView.findViewById<TextView>(R.id.title)
    var desc = itemView.findViewById<TextView>(R.id.desc)
    var discount = itemView.findViewById<TextView>(R.id.discount)
    var viewPacks = itemView.findViewById<TextView>(R.id.view_packs)
    var image = itemView.findViewById<ImageView>(R.id.imageView2)
  }

  fun getPackageInfoFromDB(holder: upgradeViewHolder, bundles: BundlesModel) {
    val itemsIds = arrayListOf<String>()
    val includedFeatures =
      Gson().fromJson<List<IncludedFeature>>(
        bundles.included_features,
        object :
          TypeToken<List<IncludedFeature>>() {}.type
      )
    for (item in includedFeatures) {
      itemsIds.add(item.feature_code)
    }

    var offeredBundlePrice = 0
    var originalBundlePrice = 0
//    val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
    val minMonth: Int = 12
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .featuresDao()
        .getallFeaturesInList(itemsIds)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            for (singleItem in it) {
              for (item in includedFeatures) {
                if (singleItem.feature_code == item.feature_code) {
                  originalBundlePrice += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt() * minMonth
                }
              }
            }
            if(bundles.overall_discount_percent > 0){
              offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent/100)
              holder.discount.visibility = View.VISIBLE
//                                        holder.bundlePriceLabel.visibility = View.GONE
              holder.discount.setText(bundles.overall_discount_percent.toString() + "% SAVING")
            } else {
              offeredBundlePrice = originalBundlePrice
              holder.discount.visibility = View.GONE
//                                        holder.bundlePriceLabel.visibility = View.VISIBLE
            }

              holder.desc.setText("₹" +
                      NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                      "/year for "+addonTitle+" + "+(includedFeatures.size-1) + "more features")
          },
          {
            it.printStackTrace()
          }
        )
    )
  }
}