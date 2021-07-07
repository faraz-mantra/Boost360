package com.boost.upgrades.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.upgrades.interfaces.HomeListener
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*

class PartnerViewPagerAdapter(
  val list: ArrayList<PartnerZone>, val activity: UpgradeActivity, val homeListener: HomeListener
) : RecyclerView.Adapter<PartnerViewPagerAdapter.PagerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
    val item = View.inflate(parent.context, R.layout.partner_item, null)
    val lp = ViewGroup.LayoutParams(
      ViewGroup.LayoutParams.MATCH_PARENT,
      ViewGroup.LayoutParams.MATCH_PARENT
    )
    item.layoutParams = lp
    return PagerViewHolder(item)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
    Glide.with(holder.itemView.context).load(list.get(position).image.url).into(holder.primaryImage)
    holder.primaryImage.setOnClickListener {
      homeListener.onPartnerZoneClicked(list.get(position))
    }
    holder.title.setText(list.get(position).title)
    checkBannerDetails(position)
  }


  fun addupdates(partnerZone: List<PartnerZone>) {
    val initPosition = list.size
    list.clear()
    list.addAll(partnerZone)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val primaryImage = itemView.findViewById<ImageView>(R.id.partner_primary_image)
    val title = itemView.findViewById<TextView>(R.id.partner_title)
  }

  //check if the details available in the DB else remove item from list
  fun checkBannerDetails(position: Int) {
    if (list.get(position)!!.cta_feature_key != null) {
      CompositeDisposable().add(
        AppDatabase.getInstance(activity.application)!!
          .featuresDao()
          .checkFeatureTableKeyExist(list.get(position)!!.cta_feature_key)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            try {
              if (it == 0) {
                for (singleBanner in list) {
                  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
                    list.remove(singleBanner)
                    notifyDataSetChanged()
                    homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                  }
                }
              } else {
                for (singleBanner in list) {
                  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
                    if (singleBanner.exclusive_to_customers != null && !singleBanner.exclusive_to_customers.contains(
                        activity.fpTag
                      )
                    ) {
                      list.remove(singleBanner)
                      notifyDataSetChanged()
                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains(
                        activity.experienceCode
                      )
                    ) {
                      list.remove(singleBanner)
                      notifyDataSetChanged()
                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                    }
                  }
                }
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }, {
            it.printStackTrace()
          })
      )
    } else if (list.get(position)!!.cta_bundle_identifier != null) {
      CompositeDisposable().add(
        AppDatabase.getInstance(activity.application)!!
          .bundlesDao()
          .checkBundleKeyExist(list.get(position)!!.cta_bundle_identifier)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe({
            try {
              if (it == 0) {
                for (singleBanner in list) {
                  if (singleBanner.cta_bundle_identifier == list.get(position)!!.cta_bundle_identifier) {
                    list.remove(singleBanner)
                    notifyDataSetChanged()
                    homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                  }
                }
              } else {
                for (singleBanner in list) {
                  if (singleBanner.cta_bundle_identifier == list.get(position)!!.cta_bundle_identifier) {
                    if (singleBanner.exclusive_to_customers != null && !singleBanner.exclusive_to_customers.contains(
                        activity.fpTag
                      )
                    ) {
                      list.remove(singleBanner)
                      notifyDataSetChanged()
                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains(
                        activity.experienceCode
                      )
                    ) {
                      list.remove(singleBanner)
                      notifyDataSetChanged()
                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
                    }
                  }
                }
              }
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }, {
            it.printStackTrace()
          })
      )
    }
  }
}