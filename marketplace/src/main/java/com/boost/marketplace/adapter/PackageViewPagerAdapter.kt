package com.boost.marketplace.adapter

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils
import com.boost.cart.utils.WebEngageController
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.home.MarketPlaceActivity
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE_CART_EXPERT_CALL_CLICK
import com.framework.webengageconstant.ADD_ON_MARKETPLACE_CARD_CLICK
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*


class PackageViewPagerAdapter(
  val list: ArrayList<Bundles>, val activity: MarketPlaceActivity, val homeListener: HomeListener
) : RecyclerView.Adapter<PackageViewPagerAdapter.PagerViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
    val item = View.inflate(parent.context, R.layout.item_packs, null)
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
//        holder.getNowButton.setOnClickListener {
//            holder.primaryImageCopy.visibility = View.VISIBLE
//            holder.getNowButton.setFocusable(true)
//            holder.getNowButton.background = ContextCompat.getDrawable(
//                    activity.application,
//                    R.drawable.added_to_cart_grey
//            )
//
//            holder.getNowButton.setTextColor(context.getResources().getColor(R.color.tv_color_BB))
//            holder.getNowButton.setText("Added to cart")
//
//            homeListener.onPackageAddToCart(list.get(position),holder.primaryImageCopy)
//
//
//        }

//        holder.details_button.setOnClickListener {
//            homeListener.onPackageClicked(list.get(position))
//        }

    holder.itemView.setOnClickListener {
      WebEngageController.trackEvent(ADD_ON_MARKETPLACE_CARD_CLICK, PAGE_VIEW, NO_EVENT_VALUE)
      homeListener.onPackageClicked(list[position])
    }
    holder.name.text = list[position].name ?: ""

    try {
      getPackageInfoFromDB(holder, list[position])
    } catch (e: Exception) {
      SentryController.captureException(e)
    }
    try {
      isItemAddedInCart(holder, list.get(position))
    } catch (e: Exception) {
      SentryController.captureException(e)
    }
//        image_title.setText(list.get(position).title)
//        image_description.setText(list.get(position).description)
//        page_images.setImageResource(list.get(position).image!!)
  }


  fun addupdates(Bundles: List<Bundles>) {
    val initPosition = list.size
    list.clear()
    list.addAll(Bundles)
    notifyItemRangeInserted(initPosition, list.size)
  }

  class PagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.name)
    val item_package_name = itemView.findViewById<LinearLayout>(R.id.item_package_name)
    val offerPrice = itemView.findViewById<TextView>(R.id.offer_price)
    val origCost = itemView.findViewById<TextView>(R.id.orig_cost)

    //        val getNowButton = itemView.findViewById<TextView>(R.id.getnow_button)
//        val details_button = itemView.findViewById<TextView>(R.id.details_button)
    val primaryImage = itemView.findViewById<ImageView>(R.id.package_primary_image)
    val primaryImageCopy = itemView.findViewById<ImageView>(R.id.package_primary_image_copy)
    val bundleDiscount = itemView.findViewById<TextView>(R.id.bundle_level_discount)
    val tv_inlcuded_add_on=itemView.findViewById<TextView>(R.id.tv_inlcuded_add_on)
    val package_feature_name_tv=itemView.findViewById<TextView>(R.id.package_feature_name_tv)
    val image1=itemView.findViewById<ImageView>(R.id.image1)
    val image2=itemView.findViewById<ImageView>(R.id.image2)
    val image3=itemView.findViewById<ImageView>(R.id.image3)
    val arrw1=itemView.findViewById<ImageView>(R.id.arrw1)
//        val bundlePriceLabel = itemView.findViewById<TextView>(R.id.bundle_price_label)
  }

  fun getPackageInfoFromDB(holder: PagerViewHolder, bundles: Bundles) {
    val prefs = SharedPrefs(activity)
    val itemsIds = arrayListOf<String>()
    for (item in bundles.included_features) {
      itemsIds.add(item.feature_code)
    }

    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    val minMonth: Int = if (!prefs.getYearPricing() && bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .featuresDao()
        .getallFeaturesInList(itemsIds)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
          {
            if(it.size>3) {
              holder.tv_inlcuded_add_on.setText("+" + (it.size - 3) + "more")
            }else{
              holder.tv_inlcuded_add_on.visibility = View.GONE
            }

            Glide.with(holder.itemView.context).load(if(it.size>=1)it[0].primary_image else "").into(holder.image1)
            Glide.with(holder.itemView.context).load(if(it.size>=2)it[1].primary_image else "").into(holder.image2)
            Glide.with(holder.itemView.context).load(if(it.size>=3)it[2].primary_image else "").into(holder.image3)
            val list = StringBuilder()
            for (singleItem in it) {
              if(it.size>1 && !list.toString().isNullOrEmpty()){
                list.append(", ")
              }
              list.append(singleItem.name)
            }
            holder.package_feature_name_tv.setText(list.toString())
            for (singleItem in it) {
              for (item in bundles.included_features) {
                if (singleItem.feature_code == item.feature_code) {
                  originalBundlePrice += Utils.priceCalculatorForYear(
                    RootUtil.round(
                      (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                      2
                    ) * minMonth, singleItem.widget_type, activity)
                }
              }
            }
            if (bundles.overall_discount_percent > 0) {
              offeredBundlePrice = RootUtil.round(originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent / 100.0), 2)
              holder.bundleDiscount.visibility = View.VISIBLE
//                                        holder.bundlePriceLabel.visibility = View.GONE
              holder.bundleDiscount.setText(bundles.overall_discount_percent.toString() + "% SAVING")
            } else {
              offeredBundlePrice = originalBundlePrice
              holder.bundleDiscount.visibility = View.GONE
              setMargins(holder.item_package_name, 15, 32, 0, 0);
              if(it.size>3){
                setMargins(holder.arrw1, 0, 12, 40, 0);
              }else{
                holder.arrw1.visibility=View.GONE
              }


//                                        holder.bundlePriceLabel.visibility = View.VISIBLE
            }

            if (!prefs.getYearPricing() && bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) {
                holder.offerPrice.setText("₹" +
                        NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                        Utils.yearlyOrMonthlyOrEmptyValidity("", activity, bundles.min_purchase_months!!)
                )
              if (offeredBundlePrice != originalBundlePrice) {
                spannableString(holder, originalBundlePrice, bundles.min_purchase_months!!)
                holder.origCost.visibility = View.VISIBLE
              } else {
                holder.origCost.visibility = View.GONE
              }
            } else {
                holder.offerPrice.setText("₹" +
                        NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                        + Utils.yearlyOrMonthlyOrEmptyValidity("", activity))
              if (offeredBundlePrice != originalBundlePrice) {
                spannableString(holder, originalBundlePrice)
                holder.origCost.visibility = View.VISIBLE
              } else {
                holder.origCost.visibility = View.GONE
              }
            }

            if (bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()) {
              Glide.with(holder.itemView.context).load(bundles.primary_image!!.url).into(holder.primaryImage)
              Glide.with(holder.itemView.context).load(bundles.primary_image!!.url).into(holder.primaryImageCopy)
            } else {
              holder.primaryImage.setImageResource(R.drawable.rectangle_copy_18)
              holder.primaryImageCopy.setImageResource(R.drawable.rectangle_copy_18)
            }
          },
          {
            it.printStackTrace()
          }
        )
    )
  }

  fun isItemAddedInCart(holder: PagerViewHolder, bundles: Bundles) {
    val itemsIds = arrayListOf<String>()
    for (item in bundles.included_features) {
      itemsIds.add(item.feature_code)
    }
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .cartDao()
        .getCartItems()
//                        .getAllCartItemsInList(itemsIds)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          for (singleItem in it) {
//                                Log.v("isItemAddedInCart", " "+ bundles!!._kid + " "+ singleItem.item_id)
//                                for (item in bundles.included_features) {
//                                    Log.v("isItemAddedInCar12", " "+ item.feature_code)
//                                    if (singleItem.item_id.equals(bundles!!._kid)) {
//                                        Log.d("isItemAddedInCart1", " "+ holder.getNowButton.isClickable)
//                                        holder.getNowButton.background = ContextCompat.getDrawable(
//                                                activity.application,
//                                                R.drawable.added_to_cart_grey
//                                        )
//                                        holder.getNowButton.setTextColor(context.getResources().getColor(R.color.tv_color_BB))
//                                        holder.getNowButton.setText("Added To Cart")
////                                        holder.getNowButton.setEnabled(false)
////                                        holder.getNowButton.isEnabled = false
////                                        holder.getNowButton.isClickable = false
//
//                                    }

//                                }
          }
        }, {

        }
        )
    )
  }

  fun spannableString(holder: PagerViewHolder, value: Double, minMonth: Int = 1) {
    val origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value)
            + Utils.yearlyOrMonthlyOrEmptyValidity("", activity, minMonth))

    origCost.setSpan(
      StrikethroughSpan(),
      0,
      origCost.length,
      0
    )
    holder.origCost.setText(origCost)
  }
  private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
    if (view.layoutParams is MarginLayoutParams) {
      val p = view.layoutParams as MarginLayoutParams
      p.setMargins(left, top, right, bottom)
      view.requestLayout()
    }
  }
}