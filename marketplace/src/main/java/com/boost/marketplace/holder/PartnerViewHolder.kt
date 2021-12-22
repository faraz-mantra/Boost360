package com.boost.marketplace.holder

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.databinding.ItemPartnerBinding
import com.framework.analytics.SentryController
import com.framework.glide.util.glideLoad
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class PartnerViewHolder(binding:ItemPartnerBinding):AppBaseRecyclerViewHolder<ItemPartnerBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? PartnerZone ?: return
    activity?.glideLoad(binding.mpPartnerPrimaryImage, data.image.url ?: "", R.drawable.placeholder_image_n)
    binding.mpPartnerTitle.text =item.title


  }
//  fun checkBannerDetails(position: Int) {
//    if (list.get(position)!!.cta_feature_key != null) {
//      CompositeDisposable().add(
//        activity?.let { AppDatabase.getInstance(it.application) }!!
//          .featuresDao()
//          .checkFeatureTableKeyExist(list.get(position)!!.cta_feature_key)
//          .subscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe({
//            try {
//              if (it == 0) {
//                for (singleBanner in list) {
//                  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
//                    list.remove(singleBanner)
//                    notifyDataSetChanged()
//                    homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                  }
//                }
//              } else {
//                for (singleBanner in list) {
//                  if (singleBanner.cta_feature_key == list.get(position)!!.cta_feature_key) {
//                    if (singleBanner.exclusive_to_customers != null && !singleBanner.exclusive_to_customers.contains(
//                        activity.fpTag
//                      )
//                    ) {
//                      list.remove(singleBanner)
//                      notifyDataSetChanged()
//                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains(
//                        activity.experienceCode
//                      )
//                    ) {
//                      list.remove(singleBanner)
//                      notifyDataSetChanged()
//                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                    }
//                  }
//                }
//              }
//            } catch (e: Exception) {
//              e.printStackTrace()
//              SentryController.captureException(e)
//            }
//          }, {
//            it.printStackTrace()
//          })
//      )
//    } else if (list.get(position)!!.cta_bundle_identifier != null) {
//      CompositeDisposable().add(
//        AppDatabase.getInstance(activity.application)!!
//          .bundlesDao()
//          .checkBundleKeyExist(list.get(position)!!.cta_bundle_identifier)
//          .subscribeOn(Schedulers.io())
//          .observeOn(AndroidSchedulers.mainThread())
//          .subscribe({
//            try {
//              if (it == 0) {
//                for (singleBanner in list) {
//                  if (singleBanner.cta_bundle_identifier == list.get(position)!!.cta_bundle_identifier) {
//                    list.remove(singleBanner)
//                    notifyDataSetChanged()
//                    homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                  }
//                }
//              } else {
//                for (singleBanner in list) {
//                  if (singleBanner.cta_bundle_identifier == list.get(position)!!.cta_bundle_identifier) {
//                    if (singleBanner.exclusive_to_customers != null && !singleBanner.exclusive_to_customers.contains(
//                        activity.fpTag
//                      )
//                    ) {
//                      list.remove(singleBanner)
//                      notifyDataSetChanged()
//                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                    } else if (singleBanner.exclusive_to_categories != null && !singleBanner.exclusive_to_categories.contains(
//                        activity.experienceCode
//                      )
//                    ) {
//                      list.remove(singleBanner)
//                      notifyDataSetChanged()
//                      homeListener.onShowHidePartnerZoneIndicator(list.size > 1)
//                    }
//                  }
//                }
//              }
//            } catch (e: Exception) {
//              e.printStackTrace()
//              SentryController.captureException(e)
//            }
//          }, {
//            it.printStackTrace()
//          })
//      )
//    }
//  }

}