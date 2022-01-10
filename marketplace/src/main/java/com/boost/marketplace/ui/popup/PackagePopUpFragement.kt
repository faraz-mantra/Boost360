package com.boost.marketplace.ui.popup

import android.app.Application
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.marketplace.Adapters.CompareItemAdapter
import com.boost.marketplace.Adapters.ParentCompareItemAdapter
import com.boost.marketplace.R
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.package_popup.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

class PackagePopUpFragement : DialogFragment() {

  lateinit var root: View
  lateinit var singleBundle: Bundles
  var initialLoad = true

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
    singleBundle = Gson().fromJson<Bundles>(requireArguments().getString("bundleData"), object : TypeToken<Bundles>() {}.type)

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
    if(items.size == 1){
      package_title.text = items[0]
    }else if(items.size == 2){
      package_title.text = items[0] + " \n" + items[1]
    }else if(items.size == 3){
      package_title.text = items[0] + " \n" + items[1] + " " + items[2]
    }else if(items.size == 4){
      package_title.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3]
    }else if(items.size == 5){
      package_title.text = items[0] + " " + items[1]  + " \n"  +items[2] + " "  + items[3] + " " +items[4]
    }

    val listSamp = ArrayList<String>()

    for( item in singleBundle.included_features ){
//            Log.v("onBindViewHolder", " "+ item.feature_code)
      listSamp.add(item.feature_code)
    }

//        getPackageInfoFromDB(parentViewHolder,parentItem)
//        isItemAddedInCart(parentViewHolder,parentItem)
    getPackageInfoFromDB(singleBundle)
    isItemAddedInCart(singleBundle)

    val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()

    val layoutManager1 = GridLayoutManager(context,3,)
//        val sectionAdapter1 = SectionedRecyclerViewAdapter()
    CompositeDisposable().add(
      AppDatabase.getInstance(Application())!!
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
                AppDatabase.getInstance(Application())!!
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

                      val sectionLayout = CompareItemAdapter(it)
                      child_recyclerview.setAdapter(sectionLayout)
                      child_recyclerview.setLayoutManager(layoutManager1)


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



    package_addCartNew.setOnClickListener{
      //  package_addCartNew.background = ContextCompat.getDrawable(activity.application, R.drawable.button_added_to_cart)
      package_addCartNew.setTextColor(Color.parseColor("#bbbbbb"))
      package_addCartNew.setText("Added To Cart")
//    homeListener.onPackageClicked(parentItem,parentViewHolder.package_profile_image_compare_new)
    }

    back_btn.setOnClickListener {
      dialog!!.dismiss()
    }
  }

  fun getPackageInfoFromDB(bundles: Bundles) {
    val itemsIds = arrayListOf<String>()
    for (item in bundles.included_features) {
      itemsIds.add(item.feature_code)
    }

    var offeredBundlePrice = 0
    var originalBundlePrice = 0
    val minMonth: Int = if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1) bundles.min_purchase_months!! else 1
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
                  originalBundlePrice += (singleItem.price - ((singleItem.price * item.feature_price_discount_percent) / 100.0)).toInt() * minMonth
                }
              }
            }

            if(bundles.overall_discount_percent > 0){
              offeredBundlePrice = originalBundlePrice - (originalBundlePrice * bundles.overall_discount_percent/100)

            } else {
              offeredBundlePrice = originalBundlePrice

            }
            if (bundles.min_purchase_months != null && bundles.min_purchase_months!! > 1){
              tv_price.setText("₹" +
                      NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)+
                      "/" + bundles.min_purchase_months + " months")
              tv_inlcuded_add_on.setText("Includes these "+ it.size+ " add-ons")

            }else{
              tv_price.setText("₹" +
                      NumberFormat.getNumberInstance(Locale.ENGLISH).format(offeredBundlePrice)
                      + "/month")
              tv_inlcuded_add_on.setText("Includes these "+ it.size+ " add-ons")
            }

            if(bundles.primary_image != null && !bundles.primary_image!!.url.isNullOrEmpty()){
              //   Glide.with(itemView.context).load(bundles.primary_image!!.url).into(package_profile_image)
              Glide.with(requireContext()).load(bundles.primary_image!!.url).into(package_profile_image_compare_new)
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

  fun isItemAddedInCart(bundles: Bundles){
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
            Log.v("isItemAddedInCart", " "+ bundles!!.name + " "+ singleItem.item_id)
//                                for (item in bundles.included_features) {

            if (singleItem.item_id.equals(bundles!!._kid)) {
              Log.v("isItemAddedInCar12", " item_id: "+ singleItem.item_id + " kid: "+ bundles!!._kid + " "+ bundles!!.name)
//                                    package_submit.background = ContextCompat.getDrawable(
//                                            activity.application,
//                                            R.drawable.added_to_cart_grey
//                                    )
              package_addCartNew.setTextColor(Color.parseColor("#bbbbbb"))
              package_addCartNew.setText("Added To Cart")
            }
//                                }
          }
        }, {

        }
        )
    )
  }

}