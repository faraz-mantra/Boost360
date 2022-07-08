package com.boost.marketplace.Adapters

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.infra.utils.Utils1
import com.boost.marketplace.ui.comparePacksV3.ComparePacksV3Activity
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*

class PacksV3Adapter(
    var list: ArrayList<Bundles>,
    val activity: ComparePacksV3Activity
) : RecyclerView.Adapter<PacksV3Adapter.ParentViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ParentViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(
                R.layout.item_packsv3,
                viewGroup, false
            )
        context = view.context
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
        parentViewHolder: ParentViewHolder,
        position: Int
    ) {
        val sameAddonsInCart = ArrayList<String>()
        val addonsListInCart = ArrayList<String>()
        val parentItem = list[position]
        parentViewHolder.PackageItemTitle.text = parentItem.name
        val data = parentItem.name
        val items = data!!.split(" ".toRegex())
        if (items.size == 1) {
            parentViewHolder.PackageItemTitle.text = items[0]
        } else if (items.size == 2) {
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1]
        } else if (items.size == 3) {
            parentViewHolder.PackageItemTitle.text = items[0] + " \n" + items[1] + " " + items[2]
        } else if (items.size == 4) {
            parentViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3]
        } else if (items.size == 5) {
            parentViewHolder.PackageItemTitle.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3] + " " + items[4]
        }

        val listSamp = ArrayList<String>()
        for (item in parentItem.included_features) {
            listSamp.add(item.feature_code)
        }
        getPackageInfoFromDB(parentViewHolder, parentItem)

        val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getSpecificFeature(distinct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val itemIds = java.util.ArrayList<String?>()
                        for (item in it) {
                            itemIds.add(item.feature_code)
                        }
                        for (listItems in it) {
                            CompositeDisposable().add(
                                AppDatabase.getInstance(Application())!!
                                    .featuresDao()
                                    .getFeatureListForCompare(itemIds)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it != null) {
                                            Log.v("getFeatureListTarget", " " + itemIds)
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

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addupdates(upgradeModel: List<Bundles>) {
        val initPosition = list.size
        list.clear()
        list.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, list.size)
    }


    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle: TextView
        val tv_price: TextView
        val package_profile_image: ImageView

        init {
            PackageItemTitle = itemView
                .findViewById(
                    R.id.pack_title1
                )
            tv_price = itemView
                .findViewById(
                    R.id.pack_price1
                )
            package_profile_image = itemView
                .findViewById(
                    R.id.packs_image1
                )
        }
    }

    fun getPackageInfoFromDB(holder: ParentViewHolder, bundles: Bundles) {
        val prefs = SharedPrefs(activity)
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }
        var offeredBundlePrice = 0.0
        var originalBundlePrice = 0.0
        val minMonth: Int =
            if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
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
                                    originalBundlePrice += Utils1.priceCalculatorForYear(
                                        RootUtil.round(
                                            (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                                            2
                                        ) * minMonth, singleItem.widget_type ?: "", activity
                                    )
                                }
                            }
                        }

                        if (bundles.overall_discount_percent > 0) {
                            offeredBundlePrice = RootUtil.round(
                                originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent / 100.0),
                                2
                            )

                        } else {
                            offeredBundlePrice = originalBundlePrice

                        }
                        if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) {
                            holder.tv_price.setText(
                                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                                    .format(offeredBundlePrice) + Utils1.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )
                        } else {
                            holder.tv_price.setText(
                                "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice)
                                        +  Utils1.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )
                        }
                        if (bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()) {
                            Glide.with(holder.itemView.context).load(bundles.primary_image!!.url)
                                .into(holder.package_profile_image)

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



//    fun spannableString(holder: ParentViewHolder, value: Double) {
//        val origCost = SpannableString(
//            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
//                .format(value) + Utils1.yearlyOrMonthlyOrEmptyValidity("", activity)
//        )
//        origCost.setSpan(
//            StrikethroughSpan(),
//            0,
//            origCost.length,
//            0
//        )
//        holder.origCost.setText(origCost)
//    }

}