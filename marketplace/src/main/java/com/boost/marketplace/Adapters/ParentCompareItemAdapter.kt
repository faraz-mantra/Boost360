package com.boost.marketplace.Adapters

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.utils.Utils
import com.boost.cart.utils.Utils.yearlyOrMonthlyOrEmptyValidity
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.SharedPrefs
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.ui.Compare_Plans.ComparePacksActivity
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.bumptech.glide.Glide
import com.framework.analytics.SentryController
import com.framework.utils.RootUtil
import com.google.gson.Gson
import io.reactivex.Completable
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
    private var cartItems = listOf<CartModel>()
    lateinit var removeFeatureBottomSheet:RemoveFeatureBottomSheet

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): ParentViewHolder {
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
        val layoutManager1 = GridLayoutManager(parentViewHolder.ChildRecyclerView.context, 3)
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getSpecificFeature(distinct)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        //same features available in cart
                        for(singleItem in cartItems){
                            for(singleFeature in it) {
                                if (singleFeature.boost_widget_key.equals(singleItem.boost_widget_key)) {
                                    sameAddonsInCart.add(singleFeature.name!!)
                                    addonsListInCart.add(singleItem.item_id)
                                }
                            }
                            //if there is any other bundle available remove it
                            if(singleItem.item_type.equals("bundles")){
                                addonsListInCart.add(singleItem.item_id)
                            }
                        }
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
                                                    activity,
                                                    if(!activity.prefs.getYearPricing() && parentItem.min_purchase_months != null) parentItem.min_purchase_months!! else 1
                                                )
                                            parentViewHolder.ChildRecyclerView
                                                .setAdapter(sectionLayout)
                                            parentViewHolder.ChildRecyclerView
                                                .setLayoutManager(layoutManager1)
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
        if (cartItems.size > 0) {
            for(singleCartItem in cartItems){
                if(singleCartItem.item_id.equals(list.get(position)._kid)){
                    itemInCart = true
                    break
                }
            }
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
            if(sameAddonsInCart.size > 0){
                removeFeatureBottomSheet = RemoveFeatureBottomSheet(homeListener, addonsListener, parentViewHolder.package_profile_image_compare_new)
                val args = Bundle()
                args.putStringArrayList("addonNames", sameAddonsInCart)
                args.putStringArrayList("addonsListInCart", addonsListInCart)
                args.putString("packageDetails", Gson().toJson(parentItem!!))
                removeFeatureBottomSheet.arguments = args
                removeFeatureBottomSheet.show(activity.supportFragmentManager, RemoveFeatureBottomSheet::class.java.name)
            }else {
                parentViewHolder.package_submit.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.button_added_to_cart
                )
                parentViewHolder.package_submit.setTextColor(
                    context.getResources().getColor(R.color.tv_color_BB)
                )
                parentViewHolder.package_submit.setText(context.getString(R.string.added_to_cart))
                parentViewHolder.package_submit.isClickable = false
                removeOtherBundlesFromCart(addonsListInCart, parentItem, parentViewHolder.package_profile_image_compare_new )
            }
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

    fun updateCartItem(cartItems: List<CartModel>) {
        this.cartItems = cartItems
        notifyDataSetChanged()
    }

    inner class ParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val PackageItemTitle: TextView
        val ChildRecyclerView: RecyclerView
        val package_submit: TextView
        val tv_price: TextView
        val package_profile_image: ImageView
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
            if (!prefs.getYearPricing() && bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
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
                            holder.bundleDiscount.setText(bundles.overall_discount_percent.toString() + "% OFF")
                        } else {
                            offeredBundlePrice = originalBundlePrice
                            holder.bundleDiscount.visibility = View.GONE
                        }
                        if (!prefs.getYearPricing() && bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) {
                            holder.tv_price.setText(
                                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                                    .format(offeredBundlePrice) + yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    activity,
                                    bundles.min_purchase_months!!
                                )
                            )
                            holder.tv_inlcuded_add_on.setText("Includes these " + it.size + " add-ons")
                            if (offeredBundlePrice != originalBundlePrice) {
                                spannableString(
                                    holder,
                                    originalBundlePrice,
                                    bundles.min_purchase_months!!)
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

    fun removeOtherBundlesFromCart(addonsListInCart: List<String>, parentItem: Bundles, imageView: ImageView){
        Completable.fromAction {
            AppDatabase.getInstance(Application())!!.cartDao().deleteCartItemsInList(addonsListInCart)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                homeListener.onPackageClicked(parentItem, imageView)
                addonsListener.onRefreshCart()
            }
            .doOnError {
                Toast.makeText(context, "Not able to Delete the Add-ons!!", Toast.LENGTH_LONG).show()
                addonsListener.onRefreshCart()
            }
            .subscribe()
    }

    fun spannableString(holder: ParentViewHolder, value: Double, minMonth: Int = 1) {
        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(value) + yearlyOrMonthlyOrEmptyValidity("", activity, minMonth)
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