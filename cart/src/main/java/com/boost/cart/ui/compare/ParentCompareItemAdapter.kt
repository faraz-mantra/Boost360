package com.boost.cart.ui.compare

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.CartActivity
import com.boost.cart.R
import com.boost.cart.adapter.CompareItemAdapter
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.*
import com.boost.cart.interfaces.CompareListener
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
//import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class ParentCompareItemAdapter (var list: java.util.ArrayList<Bundles>, val activity: CartActivity, val homeListener: CompareListener) : RecyclerView.Adapter<ParentCompareItemAdapter.ParentViewHolder>() {
    // An object of RecyclerView.RecycledViewPool
    // is created to share the Views
    // between the child and
    // the parent RecyclerViews
    private val viewPool = RecyclerView.RecycledViewPool()
//    private var list = ArrayList<Bundles>()
    private var featureList = ArrayList<FeaturesModel>()

/*    init {
        this.list = itemList as ArrayList<Bundles>
    }*/
    override fun onCreateViewHolder(
            viewGroup: ViewGroup,
            i: Int): ParentViewHolder {

        // Here we inflate the corresponding
        // layout of the parent item
        val view = LayoutInflater
                .from(viewGroup.context)
                .inflate(
                        R.layout.package_fragment_adapter,
                        viewGroup, false)
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
            parentViewHolder: ParentViewHolder,
            position: Int) {

        // Create an instance of the ParentItem
        // class for the given position
        val parentItem = list[position]

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
        parentViewHolder.PackageItemTitle.text = parentItem.name
        val data = parentItem.name
//        val items = data!!.split(" ".toRegex()).toTypedArray()
        val items = data!!.split(" ".toRegex())
        if(items.size == 1){
            parentViewHolder.PackageItemTitle.text = items[0]
        }else if(items.size == 2){
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1]
        }else if(items.size == 3){
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1] + " " + items[2]
        }else if(items.size == 4){
            parentViewHolder.PackageItemTitle.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3]
        }else if(items.size == 5){
            parentViewHolder.PackageItemTitle.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3] + " " +items[4]
        }

        if(parentItem.desc != null){
            parentViewHolder.parent_item_title.text = parentItem.desc
        }else{
            /*parentViewHolder.parent_item_title.text = "LEARN MORE"
            parentViewHolder.parent_item_title.setTextColor(activity.getResources().getColor(R.color.app_yellow_1))
            parentViewHolder.parent_item_title.setTextSize(activity.getResources().getDimension(R.dimen.txt_15sp))
            parentViewHolder.parent_item_title.setTextSize(15.0f)
            parentViewHolder.parent_item_title.setOnClickListener {
                homeListener.onLearnMoreClicked(parentItem)
            }*/
        }

        val listSamp = ArrayList<String>()

        for( item in parentItem.included_features ){
//            Log.v("onBindViewHolder", " "+ item.feature_code)
            listSamp.add(item.feature_code)
        }

//        getPackageInfoFromDB(parentViewHolder,parentItem)
//        isItemAddedInCart(parentViewHolder,parentItem)
        getPackageInfoFromDB(parentViewHolder,list.get(position))
        isItemAddedInCart(parentViewHolder,list.get(position))

        val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()

        val layoutManager1 = LinearLayoutManager(parentViewHolder.ChildRecyclerView.context,
                LinearLayoutManager.VERTICAL, false)
//        val sectionAdapter1 = SectionedRecyclerViewAdapter()
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
                                        CompositeDisposable().add(
                                                AppDatabase.getInstance(activity!!.application)!!
                                                        .featuresDao()
//                                                        .getFeatureListTargetBusiness(listItems.target_business_usecase,itemIds)
                                                        .getFeatureListForCompare(itemIds)
                                                        .subscribeOn(Schedulers.io())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe({
                                                            if (it != null) {

                                                                Log.v("getFeatureListTarget", " "+ itemIds )
                                                               /* val section = MySection(listItems.target_business_usecase, it)
                                                                sectionAdapter1.addSection(section)
                                                                parentViewHolder.ChildRecyclerView
                                                                        .setAdapter(sectionAdapter1)
                                                                parentViewHolder.ChildRecyclerView
                                                                        .setLayoutManager(layoutManager1)*/

                                                                val sectionLayout = CompareItemAdapter(activity, it)
                                                                parentViewHolder.ChildRecyclerView
                                                                        .setAdapter(sectionLayout)
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
        /*val layoutManager = LinearLayoutManager(parentViewHolder.ChildRecyclerView.context,
                LinearLayoutManager.VERTICAL, false)


        layoutManager.initialPrefetchItemCount = list.size*/



parentViewHolder.package_submit.setOnClickListener{
    parentViewHolder.package_submit.background = ContextCompat.getDrawable(
            activity.application,
            R.drawable.added_to_cart_grey
    )
    parentViewHolder.package_submit.setTextColor(Color.parseColor("#bbbbbb"))
    parentViewHolder.package_submit.setText("Added To Cart")
    homeListener.onPackageClicked(parentItem,parentViewHolder.package_profile_image_compare_new)
}

    }

    // This method returns the number
    // of items we have added in the
    // ParentItemList i.e. the number
    // of instances we have created
    // of the ParentItemList
    override fun getItemCount(): Int {
        return list.size
    }

    fun addupdates(upgradeModel: List<Bundles>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, list.size)
        /*val diffResult = DiffUtil.calculateDiff(BundleCallBack(this.list, upgradeModel))
//        list = upgradeModel as java.util.ArrayList<Bundles>
        diffResult.dispatchUpdatesTo(this)
//        list = upgradeModel as java.util.ArrayList<Bundles>
        list.clear()
        list.addAll(upgradeModel)
        notifyDataSetChanged()*/
    }

    fun addupdatesNew(upgradeModel: List<Bundles>) {
        list = upgradeModel as java.util.ArrayList<Bundles>
        notifyDataSetChanged()
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
        val tv_inlcuded_add_on: TextView
        val package_profile_image_compare_new:ImageView

        init {
            PackageItemTitle = itemView
                    .findViewById(
                            R.id.package_title)
            ChildRecyclerView = itemView
                    .findViewById(
                            R.id.child_recyclerview)
            package_submit = itemView
                    .findViewById(
                            R.id.package_addCartNew)
            tv_price = itemView
                    .findViewById(
                            R.id.tv_price)
            package_profile_image = itemView
                    .findViewById(
                            R.id.package_profile_image)
            parent_item_title = itemView
                    .findViewById(
                            R.id.parent_item_title)
            tv_inlcuded_add_on = itemView
                    .findViewById(
                            R.id.tv_inlcuded_add_on)
            package_profile_image_compare_new = itemView.findViewById(R.id.package_profile_image_compare_new)

        }
    }

    fun getPackageInfoFromDB(holder: ParentViewHolder, bundles: Bundles) {
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }

        var offeredBundlePrice = 0.0
        var originalBundlePrice = 0.0
        val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
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
                                                originalBundlePrice += RootUtil.round((singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),2) * minMonth
                                            }
                                        }
                                    }

                                    if(bundles.overall_discount_percent > 0){
                                        offeredBundlePrice = RootUtil.round((originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent/100)),2)

                                    } else {
                                        offeredBundlePrice = originalBundlePrice

                                    }
                                    if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1){
                                        holder.tv_price.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                                                "/" + bundles.min_purchase_months + " months")
                                        holder.tv_inlcuded_add_on.setText("Includes these "+ it.size+ " add-ons")

                                    }else{
                                        holder.tv_price.setText("₹" +
                                                NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                                                + "/month")
                                        holder.tv_inlcuded_add_on.setText("Includes these "+ it.size+ " add-ons")
                                    }

                                    if(bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()){
                                        Glide.with(holder.itemView.context).load(bundles.primary_image!!.url).into(holder.package_profile_image)
                                        Glide.with(holder.itemView.context).load(bundles.primary_image!!.url).into(holder.package_profile_image_compare_new)
                                    } else {
                                        holder.package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
                                        holder.package_profile_image_compare_new.setImageResource(R.drawable.rectangle_copy_18)
                                    }
                                },
                                {
                                    it.printStackTrace()
                                }
                        )
        )
    }

    fun isItemAddedInCart(holder: ParentViewHolder, bundles: Bundles){
        /*val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }*/
        CompositeDisposable().add(
                AppDatabase.getInstance(activity.application)!!
                        .cartDao()
                        .getCartItems()
//                        .getAllCartItemsInList(itemsIds)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            for (singleItem in it) {
                                Log.v("isItemAddedInCart", " "+ bundles!!.name + " "+ singleItem.item_id)
//                                for (item in bundles.included_features) {

                                if (singleItem.item_id.equals(bundles!!._kid)) {
                                    Log.v("isItemAddedInCar12", " item_id: "+ singleItem.item_id + " kid: "+ bundles!!._kid + " "+ bundles!!.name)
                                    holder.package_submit.background = ContextCompat.getDrawable(
                                            activity.application,
                                            R.drawable.added_to_cart_grey
                                    )
                                    holder.package_submit.setTextColor(Color.parseColor("#bbbbbb"))
                                    holder.package_submit.setText("Added To Cart")
                                }
//                                }
                            }
                        }, {

                        }
                        )
        )
    }
}