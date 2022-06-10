package com.boost.marketplace.Adapters

//import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksActivity
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ParentCompareItemAdapter(
    var list: ArrayList<Bundles>,
    val homeListener: CompareListener,
    val addonsListener: AddonsListener,
    val activity: ComparePacksActivity
) : RecyclerView.Adapter<ParentCompareItemAdapter.ParentViewHolder>() {

    lateinit var context: Context
    private var cartItemId = ArrayList<String>()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ParentViewHolder {

        // Here we inflate the corresponding
        // layout of the parent item
        val view = LayoutInflater
            .from(viewGroup.context)
            .inflate(
                R.layout.item_compare_packs,
                viewGroup, false
            )
        context = view.context
        return ParentViewHolder(view)
    }

    override fun onBindViewHolder(
        parentViewHolder: ParentViewHolder,
        position: Int
    ) {

        // Create an instance of the ParentItem
        // class for the given position
        val parentItem = list[position]

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
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

        val layoutManager1 = GridLayoutManager(parentViewHolder.ChildRecyclerView.context, 3)
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
                                            val sectionLayout =
                                                CompareItemAdapter(
                                                    it,
                                                    addonsListener,
                                                    activity
                                                )
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

        var itemInCart = false
        if (cartItemId.contains(list.get(position)._kid)) {
            itemInCart = true
        }
        if (!itemInCart) {
            parentViewHolder.package_submit.background = ContextCompat.getDrawable(
                activity.applicationContext,
                R.drawable.button_bckgrnd
            )
            parentViewHolder.package_submit.setTextColor(activity.resources.getColor(R.color.white))
            parentViewHolder.package_submit.setText("Add To Cart")
            parentViewHolder.package_submit.isClickable = true
        } else {
            parentViewHolder.package_submit.background = ContextCompat.getDrawable(
                activity.applicationContext,
                R.drawable.button_added_to_cart
            )
            parentViewHolder.package_submit.setTextColor(
                context.getResources().getColor(R.color.tv_color_BB)
            )
            parentViewHolder.package_submit.setText(activity.getString(R.string.added_to_cart))
            parentViewHolder.package_submit.isClickable = false
        }

        parentViewHolder.package_submit.setOnClickListener {
            parentViewHolder.package_submit.background = ContextCompat.getDrawable(
                context,
                R.drawable.button_added_to_cart
            )
            parentViewHolder.package_submit.setTextColor(
                context.getResources().getColor(R.color.tv_color_BB)
            )
            parentViewHolder.package_submit.setText(context.getString(R.string.added_to_cart))
            parentViewHolder.package_submit.isClickable = false
            homeListener.onPackageClicked(
                parentItem,
                parentViewHolder.package_profile_image_compare_new
            )
        }

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

    fun updateCartItem(cartItemId: ArrayList<String>) {
        this.cartItemId = cartItemId
        notifyDataSetChanged()
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle: TextView
        val ChildRecyclerView: RecyclerView
        val package_submit: TextView
        val tv_price: TextView
        val package_profile_image: ImageView

        //        val parent_item_title: TextView
        val tv_inlcuded_add_on: TextView
        val package_profile_image_compare_new: ImageView
        val bundleDiscount: TextView
        val origCost: TextView


        init {
            PackageItemTitle = itemView
                .findViewById(
                    R.id.package_title
                )
            ChildRecyclerView = itemView
                .findViewById(
                    R.id.child_recyclerview
                )
            package_submit = itemView
                .findViewById(
                    R.id.package_addCartNew
                )
            tv_price = itemView
                .findViewById(
                    R.id.tv_price
                )
            package_profile_image = itemView
                .findViewById(
                    R.id.package_profile_image
                )
//            parent_item_title = itemView
//                    .findViewById(
//                            R.id.parent_item_title)
            tv_inlcuded_add_on = itemView
                .findViewById(
                    R.id.tv_inlcuded_add_on
                )
            package_profile_image_compare_new =
                itemView.findViewById(R.id.package_profile_image_compare_new)
            bundleDiscount = itemView.findViewById(R.id.pack_discount_tv)
            origCost = itemView.findViewById(R.id.upgrade_list_orig_cost)

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
                                    originalBundlePrice += Utils.priceCalculatorForYear(
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
                            holder.bundleDiscount.visibility = View.VISIBLE
//                                        holder.bundlePriceLabel.visibility = View.GONE
                            holder.bundleDiscount.setText(bundles.overall_discount_percent.toString() + "% OFF")
                        } else {
                            offeredBundlePrice = originalBundlePrice
                            holder.bundleDiscount.visibility = View.GONE
//                                        holder.bundlePriceLabel.visibility = View.VISIBLE
                        }


                        if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) {
                            holder.tv_price.setText(
                                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                                    .format(offeredBundlePrice) + yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )
                            holder.tv_inlcuded_add_on.setText("Includes these " + it.size + " add-ons")

                            if (offeredBundlePrice != originalBundlePrice) {
                                spannableString(
                                    holder,
                                    originalBundlePrice
                                )
                                holder.origCost.visibility = View.VISIBLE
                            } else {
                                holder.origCost.visibility = View.GONE
                            }

                        } else {
                            holder.tv_price.setText(
                                "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice)
                                        + yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity
                                )
                            )

                            holder.tv_inlcuded_add_on.setText("Includes these " + it.size + " add-ons")
                            if (offeredBundlePrice != originalBundlePrice) {
                                spannableString(
                                    holder,
                                    originalBundlePrice
                                )
                                holder.origCost.visibility = View.VISIBLE
                            } else {
                                holder.origCost.visibility = View.GONE
                            }
                        }

                        if (bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()) {
                            Glide.with(holder.itemView.context).load(bundles.primary_image!!.url)
                                .into(holder.package_profile_image)
                            Glide.with(holder.itemView.context).load(bundles.primary_image!!.url)
                                .into(holder.package_profile_image_compare_new)
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

    fun spannableString(holder: ParentViewHolder, value: Double) {
        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(value) + yearlyOrMonthlyOrEmptyValidity("", activity)
        )

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        holder.origCost.setText(origCost)
    }

}