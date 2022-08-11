package com.boost.marketplace.adapter


import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PrimaryImage
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.BundlesModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


class PackDetailsPacksAdapter(
    var bundleList: ArrayList<BundlesModel>,
    val activity: Activity,
    val listener: DetailsFragmentListener
) : RecyclerView.Adapter<PackDetailsPacksAdapter.upgradeViewHolder>() {

    private lateinit var context: Context
    var addonTitle: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
            R.layout.item_features_details, parent, false
        )
        context = itemView.context

        return upgradeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return bundleList.size
    }

    override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
        holder.title.setText(bundleList.get(position).name)
        Glide.with(context).load(bundleList.get(position).primary_image).into(holder.image)
        if (bundleList.get(position).overall_discount_percent > 0) {
            holder.discount.visibility = View.VISIBLE
            holder.discount.setText(bundleList.get(position).overall_discount_percent.toString() + "% saving")
        } else {
            holder.discount.visibility = View.GONE
        }
        try {
            getPackageInfoFromDB(holder, bundleList.get(position))
        } catch (e: Exception) {
            SentryController.captureException(e)
        }

//        holder.add_to_cart.setOnClickListener {
//            listener.goToCart()
//        }

        holder.viewPacks.setOnClickListener {

            val item: Bundles = Bundles(
                bundleList.get(position).bundle_id,
                Gson().fromJson<List<IncludedFeature>>(
                    bundleList[position].included_features!!,
                    object : TypeToken<List<IncludedFeature>>() {}.type
                ),
                bundleList.get(position).min_purchase_months,
                bundleList.get(position).name,
                bundleList.get(position).overall_discount_percent,
                PrimaryImage(bundleList.get(position).primary_image!!),
                bundleList.get(position).target_business_usecase,
                Gson().fromJson<List<String>>(
                    bundleList.get(position).exclusive_to_categories!!,
                    object : TypeToken<List<String>>() {}.type
                ),
                arrayListOf(),
                null,null,null, null,
                bundleList.get(position).desc
            )
            listener.onPackageClicked(item)
        }
    }

    fun addupdates(upgradeModel: List<BundlesModel>) {
        val initPosition = bundleList.size
        bundleList.clear()
        bundleList.addAll(upgradeModel)
        notifyItemRangeInserted(initPosition, bundleList.size)
    }

    class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var mrpPrice = itemView.findViewById<TextView>(R.id.mrpPrice)
        var price = itemView.findViewById<TextView>(R.id.price)
        var discount = itemView.findViewById<TextView>(R.id.discount)
        var viewPacks = itemView.findViewById<TextView>(R.id.view_packs)
//        var add_to_cart = itemView.findViewById<TextView>(R.id.btn_add_to_cart)
        var image = itemView.findViewById<ImageView>(R.id.package_img)
    }

    fun getPackageInfoFromDB(holder: upgradeViewHolder, bundles: BundlesModel) {
        val itemsIds = arrayListOf<String>()
        val includedFeatures =
            Gson().fromJson<List<IncludedFeature>>(
                bundles.included_features,
                object :
                    TypeToken<List<IncludedFeature>>() {}.type
            )
        for (item in includedFeatures) {
            itemsIds.add(item.feature_code)
        }

        var offeredBundlePrice = 0.0
        var originalBundlePrice = 0.0
        val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
//    val minMonth: Int = 12
        CompositeDisposable().add(
            AppDatabase.getInstance(activity.application)!!
                .featuresDao()
                .getallFeaturesInList(itemsIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        for (singleItem in it) {
                            for (item in includedFeatures) {
                                if (singleItem.feature_code == item.feature_code) {
//                  originalBundlePrice += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt() * minMonth
                                    originalBundlePrice += Utils.priceCalculatorForYear(
                                        RootUtil.round(
                                            (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)),
                                            2
                                        ) * minMonth, singleItem.widget_type, activity)
                                }
                            }
                        }
                        if (bundles.overall_discount_percent > 0) {
                            offeredBundlePrice = RootUtil.round(
                                (originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent / 100.0)),
                                2
                            )
                            holder.discount.visibility = View.VISIBLE
                            holder.discount.setText(bundles.overall_discount_percent.toString() + "%\nSAVING")
                        } else {
                            offeredBundlePrice = originalBundlePrice
                            holder.discount.visibility = View.GONE
                        }
                        holder.price.setText("₹" + offeredBundlePrice + yearlyOrMonthlyOrEmptyValidity("",activity))
                        val mrpPriceString = SpannableString("₹" + originalBundlePrice + yearlyOrMonthlyOrEmptyValidity("",activity))
                        mrpPriceString.setSpan(StrikethroughSpan(), 0, mrpPriceString.length, 0)
                        holder.mrpPrice.setText(mrpPriceString)
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }
}