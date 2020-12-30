package com.boost.upgrades.ui.compare

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.adapter.PackageAdaptor
import com.boost.upgrades.adapter.PackageViewPagerAdapter
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.interfaces.CompareListener
import com.boost.upgrades.interfaces.HomeListener
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet

class ParentCompareItemAdapter (val itemList: List<Bundles>, featuresHashMap : MutableMap<String?,FeaturesModel>, val activity: UpgradeActivity, val homeListener: CompareListener) : RecyclerView.Adapter<ParentCompareItemAdapter.ParentViewHolder>() {
    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private val viewPool = RecyclerView.RecycledViewPool()
    private var list = ArrayList<Bundles>()
    private var featureList = ArrayList<FeaturesModel>()

    init {
        this.list = itemList as ArrayList<Bundles>
    }
    override fun onCreateViewHolder(
            viewGroup: ViewGroup,
            i: Int): ParentViewHolder {

        // Here we inflate the corresponding
        // layout of the parent item
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(
                        R.layout.parent_item_new,
                        viewGroup, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
            parentViewHolder: ParentViewHolder,
            position: Int) {

        // Create an instance of the ParentItem
        // class for the given position
        val parentItem = itemList[position]

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
        parentViewHolder.PackageItemTitle.text = parentItem.name
        if(parentItem.desc != null){
            parentViewHolder.parent_item_title.text = parentItem.desc
        }else{
            parentViewHolder.parent_item_title.text = "LEARN MORE"
            parentViewHolder.parent_item_title.setTextColor(activity.getResources().getColor(R.color.app_yellow_1))
            parentViewHolder.parent_item_title.setTextSize(activity.getResources().getDimension(R.dimen.txt_15sp))
            parentViewHolder.parent_item_title.setTextSize(15.0f)
            parentViewHolder.parent_item_title.setOnClickListener {
                homeListener.onLearnMoreClicked(parentItem)
            }
        }

//Log.v("onBindViewHolder", " "+ parentItem.included_features + " "+ parentItem.name)

        val listSamp = ArrayList<String>()

        for( item in parentItem.included_features ){
//            Log.v("onBindViewHolder", " "+ item.feature_code)
            listSamp.add(item.feature_code)
        }

        getPackageInfoFromDB(parentViewHolder,parentItem)

        val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()

        println(distinct)

        val layoutManager1 = LinearLayoutManager(parentViewHolder.ChildRecyclerView.context,
                LinearLayoutManager.VERTICAL, false)
        val sectionAdapter1 = SectionedRecyclerViewAdapter()
        CompositeDisposable().add(
                AppDatabase.getInstance(activity!!.application)!!
                        .featuresDao()
                        .getallFeaturesInList(distinct)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {
                                    val itemIds = java.util.ArrayList<String?>()
                                    for (item in it) {
                                        itemIds.add(item.feature_code)
                                    }
                                    for(listItems in it){
//            Log.v("target_business_usecase", " "+ listItems.target_business_usecase +" " + bundleData!!.target_business_usecase )
                                        CompositeDisposable().add(
                                                AppDatabase.getInstance(activity!!.application)!!
                                                        .featuresDao()
                                                        .getFeatureListTargetBusiness(listItems.target_business_usecase,itemIds)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe({
                                                            if (it != null) {

//                                                                Log.v("getFeatureListTarget", " "+ it )
                                                                val section = MySection(listItems.target_business_usecase, it)
                                                                // add your section to the adapter
                                                                sectionAdapter1.addSection(section)
                                                                parentViewHolder.ChildRecyclerView
                                                                        .setAdapter(sectionAdapter1)
                                                                parentViewHolder.ChildRecyclerView
                                                                        .setLayoutManager(layoutManager1)
                                                            } else {
//                                                                Toasty.error(requireContext(), "Bundle Not Available To This Account", Toast.LENGTH_LONG).show()
                                                            }
                                                        }, {
                                                            it.printStackTrace()
                                                        })
                                        )
                                    }
                                },
                                {
                                    it.printStackTrace()

                                }
                        )
        )


        // Here we have assigned the layout
        // as LinearLayout with vertical orientation
        val layoutManager = LinearLayoutManager(parentViewHolder.ChildRecyclerView.context,
                LinearLayoutManager.VERTICAL, false)


        layoutManager.initialPrefetchItemCount = list.size



parentViewHolder.package_submit.setOnClickListener{
    homeListener.onPackageClicked(parentItem)
}

    }

    // This method returns the number
    // of items we have added in the
    // ParentItemList i.e. the number
    // of instances we have created
    // of the ParentItemList
    override fun getItemCount(): Int {
        return itemList.size
    }

    fun addupdates(upgradeModel: List<Bundles>, hash: ArrayList<FeaturesModel>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        featureList = hash
        notifyItemRangeInserted(initPosition, list.size)
    }

    // This class is to initialize
    // the Views present in
    // the parent RecyclerView
    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle: TextView
        val ChildRecyclerView: RecyclerView
        val package_submit: TextView
        val tv_price: TextView
        val package_profile_image: ImageView
        val parent_item_title: TextView

        init {
            PackageItemTitle = itemView
                    .findViewById(
                            R.id.package_title)
            ChildRecyclerView = itemView
                    .findViewById(
                            R.id.child_recyclerview)
            package_submit = itemView
                    .findViewById(
                            R.id.package_addCart)
            tv_price = itemView
                    .findViewById(
                            R.id.tv_price)
            package_profile_image = itemView
                    .findViewById(
                            R.id.package_profile_image)
            parent_item_title = itemView
                    .findViewById(
                            R.id.parent_item_title)
        }
    }

    fun getPackageInfoFromDB(holder: ParentViewHolder, bundles: Bundles) {
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

                                    } else {
                                        offeredBundlePrice = originalBundlePrice

                                    }
                                    if (bundles.min_purchase_months != null && bundles.min_purchase_months > 1){
                                        holder.tv_price.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                                                "/" + bundles.min_purchase_months + "months")

                                    }else{
                                        holder.tv_price.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                                                + "/month")

                                    }

                                    if(bundles.primary_image != null && !bundles.primary_image.url.isNullOrEmpty()){
                                        Glide.with(holder.itemView.context).load(bundles.primary_image.url).into(holder.package_profile_image)
                                    } else {
                                        holder.package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
                                    }
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
        )
    }

}