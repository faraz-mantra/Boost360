package com.boost.cart.adapter

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.utils.SharedPrefs
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*

class PackageViewPagerAdapter(
    val list: ArrayList<Bundles>,
    val activity: Activity)
    : RecyclerView.Adapter<PackageViewPagerAdapter.PagerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val item = View.inflate(parent.context, R.layout.package_item, null)
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
        holder.name.setText(list.get(position).name)

        getPackageInfoFromDB(holder, list.get(position))
        isItemAddedInCart(holder, list.get(position))
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
        val name = itemView.findViewById<TextView>(R.id.package_name)
        val offerPrice = itemView.findViewById<TextView>(R.id.offer_price)
        val origCost = itemView.findViewById<TextView>(R.id.orig_cost)
        val getNowButton = itemView.findViewById<TextView>(R.id.getnow_button)
        val details_button = itemView.findViewById<TextView>(R.id.details_button)
        val primaryImage = itemView.findViewById<ImageView>(R.id.package_primary_image)
        val primaryImageCopy = itemView.findViewById<ImageView>(R.id.package_primary_image_copy)
        val bundleDiscount = itemView.findViewById<TextView>(R.id.bundle_level_discount)
        val bundlePriceLabel = itemView.findViewById<TextView>(R.id.bundle_price_label)
    }

    fun getPackageInfoFromDB(holder: PagerViewHolder, bundles: Bundles) {
        val prefs = SharedPrefs(activity)
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }

        var offeredBundlePrice = 0.0
        var originalBundlePrice = 0.0
        val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
        CompositeDisposable().add(
                AppDatabase.getInstance(Application())!!
                        .featuresDao()
                        .getallFeaturesInList(itemsIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    for (singleItem in it) {
                                        for (item in bundles.included_features) {
                                            if (singleItem.feature_code == item.feature_code) {
                                                originalBundlePrice += RootUtil.round((singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),2) * minMonth
                                            }
                                        }
                                    }
                                    if(bundles.overall_discount_percent > 0){
                                        offeredBundlePrice = (RootUtil.round(originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent/100),2))
                                        if(prefs.getYearPricing())
                                            offeredBundlePrice = offeredBundlePrice * 12
                                        holder.bundleDiscount.visibility = View.VISIBLE
                                        holder.bundlePriceLabel.visibility = View.GONE
                                        holder.bundleDiscount.setText(bundles.overall_discount_percent.toString() + "%")
                                    } else {
                                        if(prefs.getYearPricing())
                                            offeredBundlePrice = originalBundlePrice * 12
                                        else
                                            offeredBundlePrice = originalBundlePrice
                                        holder.bundleDiscount.visibility = View.GONE
                                        holder.bundlePriceLabel.visibility = View.VISIBLE
                                    }
                                    if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1){
                                        if(prefs.getYearPricing())
                                            holder.offerPrice.setText("₹" +
                                                    NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                                                    "/year")
                                        else
                                            holder.offerPrice.setText("₹" +
                                                    NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                                                    "/" + bundles.min_purchase_months + "mths")
                                        if (offeredBundlePrice != originalBundlePrice) {
                                            spannableString(holder, if(prefs.getYearPricing()) originalBundlePrice * 12 else originalBundlePrice, bundles.min_purchase_months!!)
                                            holder.origCost.visibility = View.VISIBLE
                                        } else {
                                            holder.origCost.visibility = View.GONE
                                        }
                                    }else{
                                        if(prefs.getYearPricing())
                                            holder.offerPrice.setText("₹" +
                                                    NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                                                    + "/year")
                                        else
                                            holder.offerPrice.setText("₹" +
                                                    NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                                                    + "/mth")
                                        if (offeredBundlePrice != originalBundlePrice) {
                                            spannableString(holder, if(prefs.getYearPricing()) originalBundlePrice * 12 else originalBundlePrice, 1)
                                            holder.origCost.visibility = View.VISIBLE
                                        } else {
                                            holder.origCost.visibility = View.GONE
                                        }
                                    }

                                    if(bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()){
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

    fun isItemAddedInCart(holder: PagerViewHolder, bundles: Bundles){
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }
        CompositeDisposable().add(
                AppDatabase.getInstance(Application())!!
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
                                    if (singleItem.item_id.equals(bundles!!._kid)) {
                                        Log.d("isItemAddedInCart1", " "+ holder.getNowButton.isClickable)
//                                        holder.getNowButton.background = ContextCompat.getDrawable(
//                                                activity.application,
//                                                R.drawable.added_to_cart_grey
//                                        )
                                        holder.getNowButton.setTextColor(Color.parseColor("#bbbbbb"))
                                        holder.getNowButton.setText("Added To Cart")
//                                        holder.getNowButton.setEnabled(false)
//                                        holder.getNowButton.isEnabled = false
//                                        holder.getNowButton.isClickable = false

                                    }

//                                }
                            }
                        }, {

                        }
                        )
        )
    }

    fun spannableString(holder: PagerViewHolder, value: Double, minMonth: Int) {
        val prefs = SharedPrefs(activity)
        val origCost: SpannableString
        if(minMonth > 1){
            if(prefs.getYearPricing())
                origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/year")
            else
                origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/" + minMonth + "mths")
        }else{
            if(prefs.getYearPricing())
                origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/year")
            else
                origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/mth")
        }

        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
        holder.origCost.setText(origCost)
    }
}