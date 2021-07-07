package com.boost.upgrades.adapter

import android.graphics.Color
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.interfaces.HomeListener
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.package_fragment.*
import kotlinx.android.synthetic.main.package_item.view.*
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.*

class PackageViewPagerAdapter(
        val list: ArrayList<Bundles>, val activity: UpgradeActivity, val homeListener: HomeListener)
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
        holder.getNowButton.setOnClickListener {
            holder.getNowButton.background = ContextCompat.getDrawable(
                    activity.application,
                    R.drawable.added_to_cart_grey
            )

            holder.getNowButton.setTextColor(Color.parseColor("#bbbbbb"))
            holder.getNowButton.setText("Added To Cart")

            homeListener.onPackageAddToCart(list.get(position),holder.primaryImageCopy)


        }

        holder.details_button.setOnClickListener {
            homeListener.onPackageClicked(list.get(position))
        }

        holder.itemView.setOnClickListener {
            homeListener.onPackageClicked(list.get(position))
        }
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
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }

        var offeredBundlePrice = 0
        var originalBundlePrice = 0
        val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months > 1) bundles.min_purchase_months else 1
        CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                        .featuresDao()
                        .getallFeaturesInList(itemsIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    for (singleItem in it) {
                                        for (item in bundles.included_features) {
                                            if (singleItem.feature_code == item.feature_code) {
                                                originalBundlePrice += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt() * minMonth
                                            }
                                        }
                                    }
                                    if(bundles.overall_discount_percent > 0){
                                        offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent/100)
                                        holder.bundleDiscount.visibility = View.VISIBLE
                                        holder.bundlePriceLabel.visibility = View.GONE
                                        holder.bundleDiscount.setText(bundles.overall_discount_percent.toString() + "%")
                                    } else {
                                        offeredBundlePrice = originalBundlePrice
                                        holder.bundleDiscount.visibility = View.GONE
                                        holder.bundlePriceLabel.visibility = View.VISIBLE
                                    }

                                    if (bundles.min_purchase_months != null && bundles.min_purchase_months > 1){
                                        holder.offerPrice.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                                                "/" + bundles.min_purchase_months + "mths")
                                        if (offeredBundlePrice != originalBundlePrice) {
                                            spannableString(holder, originalBundlePrice, bundles.min_purchase_months)
                                            holder.origCost.visibility = View.VISIBLE
                                        } else {
                                            holder.origCost.visibility = View.GONE
                                        }
                                    }else{
                                        holder.offerPrice.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                                                + "/mth")
                                        if (offeredBundlePrice != originalBundlePrice) {
                                            spannableString(holder, originalBundlePrice, 1)
                                            holder.origCost.visibility = View.VISIBLE
                                        } else {
                                            holder.origCost.visibility = View.GONE
                                        }
                                    }

                                    if(bundles.primary_image != null && !bundles.primary_image.url.isNullOrEmpty()){
                                        Glide.with(holder.itemView.context).load(bundles.primary_image.url).into(holder.primaryImage)
                                        Glide.with(holder.itemView.context).load(bundles.primary_image.url).into(holder.primaryImageCopy)
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
                AppDatabase.getInstance(activity.application)!!
                        .cartDao()
                        .getCartItems()
//                        .getAllCartItemsInList(itemsIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            for (singleItem in it) {
                                Log.v("isItemAddedInCart", " "+ bundles!!._kid + " "+ singleItem.item_id)
//                                for (item in bundles.included_features) {
//                                    Log.v("isItemAddedInCar12", " "+ item.feature_code)
                                    if (singleItem.item_id.equals(bundles!!._kid)) {
                                        holder.getNowButton.background = ContextCompat.getDrawable(
                                                activity.application,
                                                R.drawable.added_to_cart_grey
                                        )
                                        holder.getNowButton.setTextColor(Color.parseColor("#bbbbbb"))
                                        holder.getNowButton.setText("Added To Cart")
                                        holder.getNowButton.setEnabled(false)
                                    }
//                                }
                            }
                        }, {

                        }
                        )
        )
    }

    fun spannableString(holder: PagerViewHolder, value: Int, minMonth: Int) {
        val origCost: SpannableString
        if(minMonth > 1){
            origCost = SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/" + minMonth + "mths")
        }else{
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