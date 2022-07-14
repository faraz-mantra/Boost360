package com.boost.marketplace.Adapters

import android.app.Application
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.R
import com.boost.marketplace.infra.utils.Utils1
import com.boost.marketplace.ui.comparePacksV3.ComparePacksV3Activity
import com.framework.utils.RootUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*

class PacksV3PricingAdapter (
    var list: ArrayList<Bundles>,
    val activity: ComparePacksV3Activity
) : RecyclerView.Adapter<PacksV3PricingAdapter.ParentViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ParentViewHolder {
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(
                R.layout.item_packs_pricev3,
                viewGroup, false
            )
        context = view.context
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
        parentViewHolder: ParentViewHolder,
        position: Int
    ) {

        val parentItem = list[position]

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
        val tv_price: TextView

        init {
            tv_price = itemView
                .findViewById(
                    R.id.pack_price
                )
        }
    }

    fun getPackageInfoFromDB(holder: ParentViewHolder, bundles: Bundles) {

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
                                    .format(RootUtil.round(offeredBundlePrice,0) ) + Utils1.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )
                        } else {
                            holder.tv_price.setText(
                                "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(RootUtil.round(offeredBundlePrice,0))
                                        +  Utils1.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )
                        }
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }
}