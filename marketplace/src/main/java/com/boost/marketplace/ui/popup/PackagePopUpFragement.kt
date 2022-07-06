package com.boost.marketplace.ui.popup

import android.app.Application
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.Utils
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.WebEngageController
import com.boost.marketplace.Adapters.CompareItemAdapter
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.AddonsListener
import com.boost.marketplace.interfaces.CompareListener
import com.boost.marketplace.interfaces.DetailsFragmentListener
import com.boost.marketplace.ui.details.FeatureDetailsViewModel
import com.boost.marketplace.ui.popup.removeItems.RemoveFeatureBottomSheet
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feature_details.*
import kotlinx.android.synthetic.main.package_popup.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class PackagePopUpFragement(val homeListener: CompareListener, var addonsListener: AddonsListener) : DialogFragment(), AddonsListener {

    lateinit var root: View
    lateinit var singleBundle: Bundles
    lateinit var cartItems: List<CartModel>
    var initialLoad = true

    var offeredBundlePrice = 0.0
    var originalBundlePrice = 0.0
    lateinit var removeFeatureBottomSheet: RemoveFeatureBottomSheet
    val sameAddonsInCart = ArrayList<String>()
    val addonsListInCart = ArrayList<String>()

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.setLayout(width, height)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.fullscreen_color)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.package_popup, container, false)
        singleBundle = Gson().fromJson<Bundles>(
            requireArguments().getString("bundleData"),
            object : TypeToken<Bundles>() {}.type
        )
        cartItems = Gson().fromJson<List<CartModel>>(
            requireArguments().getString("cartList"),
            object : TypeToken<List<CartModel>>() {}.type
        )

        return root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // For the created instance,
        // get the title and set it
        // as the text for the TextView
        package_title.text = singleBundle.name
        val data = singleBundle.name
//        val items = data!!.split(" ".toRegex()).toTypedArray()
        val items = data!!.split(" ".toRegex())
        if (items.size == 1) {
            package_title.text = items[0]
        } else if (items.size == 2) {
            package_title.text = items[0] + " \n" + items[1]
        } else if (items.size == 3) {
            package_title.text = items[0] + " \n" + items[1] + " " + items[2]
        } else if (items.size == 4) {
            package_title.text = items[0] + " " + items[1] + " \n" + items[2] + " " + items[3]
        } else if (items.size == 5) {
            package_title.text =
                items[0] + " " + items[1] + " \n" + items[2] + " " + items[3] + " " + items[4]
        }

        val listSamp = ArrayList<String>()

        for (item in singleBundle.included_features) {
//            Log.v("onBindViewHolder", " "+ item.feature_code)
            listSamp.add(item.feature_code)
        }

//        getPackageInfoFromDB(parentViewHolder,parentItem)
//        isItemAddedInCart(parentViewHolder,parentItem)
        getPackageInfoFromDB(singleBundle)
        isItemAddedInCart(singleBundle)

        val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()

        val layoutManager1 = GridLayoutManager(context, 3)
//        val sectionAdapter1 = SectionedRecyclerViewAdapter()
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .featuresDao()
                .getallFeaturesInList(distinct)
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
//                                                        .getFeatureListTargetBusiness(listItems.target_business_usecase,itemIds)
                                    .getFeatureListForCompare(itemIds)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        if (it != null) {

                                            Log.v("getFeatureListTarget", " " + itemIds)
                                            /* val section = MySection(listItems.target_business_usecase, it)
                                             sectionAdapter1.addSection(section)
                                             parentViewHolder.ChildRecyclerView
                                                     .setAdapter(sectionAdapter1)
                                             parentViewHolder.ChildRecyclerView
                                                     .setLayoutManager(layoutManager1)*/

                                            val sectionLayout =
                                                CompareItemAdapter(it, this, requireActivity())
                                            child_recyclerview.setAdapter(sectionLayout)
                                            child_recyclerview.setLayoutManager(layoutManager1)


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



        package_addCartNew.setOnClickListener {
            if (sameAddonsInCart.size > 0) {
                removeFeatureBottomSheet = RemoveFeatureBottomSheet(
                    homeListener,
                    addonsListener,
                    null
                )
                val args = Bundle()
                args.putStringArrayList("addonNames", sameAddonsInCart)
                args.putStringArrayList("addonsListInCart", addonsListInCart)
                args.putString("packageDetails", Gson().toJson(singleBundle))
                removeFeatureBottomSheet.arguments = args
                removeFeatureBottomSheet.show(
                    requireActivity().supportFragmentManager,
                    RemoveFeatureBottomSheet::class.java.name
                )
                dismiss()
            } else {
                package_addCartNew.background = ColorDrawable(resources.getColor(R.color.greyLight))
                package_addCartNew.setTextColor(getResources().getColor(R.color.tv_color_BB))
                package_addCartNew.text = getString(R.string.added_to_cart)
                package_addCartNew.isClickable = false
                removeOtherBundlesFromCart(addonsListInCart, singleBundle, null)
            }
        }

        back_btn.setOnClickListener {
            dialog!!.dismiss()
        }
    }

    fun removeOtherBundlesFromCart(addonsListInCart: List<String>, parentItem: Bundles, imageView: ImageView?){
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

    fun onPackageClicked(item: Bundles?) {
        if (item != null) {
            val prefs = SharedPrefs(requireActivity())
            prefs.storeAddedPackageDesc(item.desc ?: "")

            val itemIds = arrayListOf<String>()
            for (i in item.included_features) {
                itemIds.add(i.feature_code)
            }

            CompositeDisposable().add(
                AppDatabase.getInstance(requireActivity().application)!!
                    .featuresDao()
                    .getallFeaturesInList(itemIds)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {

                            //clear cartOrderInfo from SharedPref to requestAPI again
                            prefs.storeCartOrderInfo(null)
                            val cartItem = CartModel(
                                item!!._kid,
                                null,
                                null,
                                item!!.name,
                                "",
                                item!!.primary_image!!.url,
                                offeredBundlePrice,
                                originalBundlePrice,
                                item!!.overall_discount_percent,
                                1,
                                if (item!!.min_purchase_months != null) item!!.min_purchase_months!! else 1,
                                "bundles",
                                null,
                                ""
                            )
                            Completable.fromAction {
                                AppDatabase.getInstance(requireActivity().application)!!.cartDao()
                                    .insertToCart(cartItem)
                            }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete {
//                  updatesLoader.postValue(false)
                                }
                                .doOnError {
//                  updatesError.postValue(it.message)
//                  updatesLoader.postValue(false)
                                }
                                .subscribe()
                            addonsListener.onRefreshCart()
                            val event_attributes: java.util.HashMap<String, Any> =
                                java.util.HashMap()
                            item!!.name?.let { it1 ->
                                event_attributes.put(
                                    "Package Name",
                                    it1
                                )
                            }
                            item!!.target_business_usecase?.let { it1 ->
                                event_attributes.put(
                                    "Package Tag",
                                    it1
                                )
                            }
                            event_attributes.put("Package Price", originalBundlePrice)
                            event_attributes.put("Discounted Price", offeredBundlePrice)
                            event_attributes.put(
                                "Discount %",
                                item!!.overall_discount_percent
                            )
                            item!!.min_purchase_months?.let { it1 ->
                                event_attributes.put(
                                    "Validity",
                                    it1
                                )
                            }
                            WebEngageController.trackEvent(
                                ADDONS_MARKETPLACE_COMPARE_PACKAGE_ADDED_TO_CART,
                                ADDONS_MARKETPLACE,
                                event_attributes
                            )
                        },
                        {
                            it.printStackTrace()

                        }
                    )
            )


        }
    }

    fun getPackageInfoFromDB(bundles: Bundles) {
        val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }

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
                                        ) * minMonth, singleItem.widget_type, requireActivity()
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
                            tv_price.setText(
                                "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                                    .format(offeredBundlePrice) + Utils.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    requireActivity()
                                )
                            )
                            tv_inlcuded_add_on.setText("Includes these " + it.size + " add-ons")
                            if (offeredBundlePrice != originalBundlePrice) {
                                spannableString(originalBundlePrice)
                                upgrade_list_orig_cost.visibility = View.VISIBLE
                            } else {
                                upgrade_list_orig_cost.visibility = View.GONE
                            }

                        } else {
                            tv_price.setText(
                                "₹" +
                                        NumberFormat.getNumberInstance(Locale.ENGLISH)
                                            .format(offeredBundlePrice)
                                        + Utils.yearlyOrMonthlyOrEmptyValidity(
                                    "",
                                    requireActivity()
                                )
                            )
                            tv_inlcuded_add_on.setText("Includes these " + it.size + " add-ons")
                        }

                        if (bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()) {
                            //   Glide.with(itemView.context).load(bundles.primary_image!!.url).into(package_profile_image)
                            Glide.with(requireContext()).load(bundles.primary_image!!.url)
                                .into(package_profile_image_compare_new)
                        } else {
                            //  package_profile_image.setImageResource(R.drawable.rectangle_copy_18)
                            package_profile_image_compare_new.setImageResource(R.drawable.rectangle_copy_18)
                        }
                    },
                    {
                        it.printStackTrace()
                    }
                )
        )
    }

    fun isItemAddedInCart(bundles: Bundles) {
        /*val itemsIds = arrayListOf<String>()
        for (item in bundles.included_features) {
            itemsIds.add(item.feature_code)
        }*/
        CompositeDisposable().add(
            AppDatabase.getInstance(Application())!!
                .cartDao()
                .getCartItems()
//                        .getAllCartItemsInList(itemsIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    for (singleItem in it) {
                        Log.v("isItemAddedInCart", " " + bundles!!.name + " " + singleItem.item_id)
//                                for (item in bundles.included_features) {

                        if (singleItem.item_id.equals(bundles!!._kid)) {
                            Log.v(
                                "isItemAddedInCar12",
                                " item_id: " + singleItem.item_id + " kid: " + bundles!!._kid + " " + bundles!!.name
                            )
                            package_addCartNew.background = ColorDrawable(resources.getColor(R.color.greyLight))
                            package_addCartNew.setTextColor(getResources().getColor(R.color.tv_color_BB))
                            package_addCartNew.text = getString(R.string.added_to_cart)
                            package_addCartNew.isClickable = false
                        }
//                                }
                    }
                }, {

                }
                )
        )
    }

    fun spannableString(value: Double) {
        val origCost = SpannableString(
            "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
                .format(value) + Utils.yearlyOrMonthlyOrEmptyValidity("", requireActivity())
        )

        origCost.setSpan(
            StrikethroughSpan(),
            0,
            origCost.length,
            0
        )
        upgrade_list_orig_cost.setText(origCost)
    }


    override fun onAddonsClicked(item: FeaturesModel) {
    }

    override fun onRefreshCart() {
    }

}