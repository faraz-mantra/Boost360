package com.boost.upgrades.adapter

import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.WidgetModel
import com.boost.upgrades.interfaces.HomeListener
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.package_item.view.*
import java.util.*

class PackageViewPagerAdapter(
        val list: ArrayList<Bundles>, val activity: UpgradeActivity, val homeListener: HomeListener)
    : RecyclerView.Adapter<PackageViewPagerAdapter.PagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val item = View.inflate(parent.getContext(), R.layout.package_item, null)
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
        holder.itemView.setOnClickListener {
            homeListener.onPackageClicked(list.get(position))
        }
        holder.name.setText(list.get(position).name)
        getPackageInfoFromDB(holder, list.get(position))
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
    }

    fun getPackageInfoFromDB(holder: PagerViewHolder, bundles: Bundles) {
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }
        var mrpPrice = 0.0
        var grandTotal = 0.0
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
                                            if (singleItem.boost_widget_key == item.feature_code) {
                                                val total = (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0))
                                                grandTotal += total
                                                mrpPrice += singleItem.price
                                            }
                                        }
                                    }
                                    holder.offerPrice.setText("₹"+grandTotal)
                                    if(grandTotal != mrpPrice){
                                        spannableString(holder,mrpPrice)
                                        holder.origCost.visibility = View.VISIBLE
                                    }else{
                                        holder.origCost.visibility = View.GONE
                                    }
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
        )
    }

    fun spannableString(holder: PagerViewHolder, value: Double) {
        val origCost = SpannableString("₹"+value)

        origCost.setSpan(
                StrikethroughSpan(),
                0,
                origCost.length,
                0
        )
        holder.origCost.setText(origCost)
    }
}