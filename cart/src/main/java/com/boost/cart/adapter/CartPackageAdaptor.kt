package com.boost.cart.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.interfaces.CartFragmentListener
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.utils.WebEngageController
import com.bumptech.glide.Glide
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_PACKAGE_CROSSED_DELETED_FROM_CART
import java.text.NumberFormat
import java.util.*


class CartPackageAdaptor(
  list: List<CartModel>?,
  val listener: CartFragmentListener
) : RecyclerView.Adapter<CartPackageAdaptor.upgradeViewHolder>() {

  private var bundlesList = ArrayList<CartModel>()
  private lateinit var context: Context

  init {
    this.bundlesList = list as ArrayList<CartModel>
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): upgradeViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(
      R.layout.item_cart_pack, parent, false
    )
    context = itemView.context

    return upgradeViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return bundlesList.size
  }

  override fun onBindViewHolder(holder: upgradeViewHolder, position: Int) {
    val selectedBundle = bundlesList.get(position)

    holder.name.text = selectedBundle.item_name
    val price = selectedBundle.price
    val MRPPrice = selectedBundle.MRPPrice
    if (selectedBundle.min_purchase_months > 1) {
      holder.price.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH)
        .format(price) + "/" + selectedBundle.min_purchase_months + "mths"
    } else {
      holder.price.text =
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + "/mth"
    }

    if (selectedBundle.link != null) {
      Glide.with(context).load(selectedBundle.link!!).placeholder(R.drawable.boost_360_insignia)
        .into(holder.image)
    } else {
      holder.image.setImageResource(R.drawable.rectangle_copy_18)
    }

    if (price != MRPPrice) {
      spannableString(holder, MRPPrice, selectedBundle.min_purchase_months)
      holder.orig_cost.visibility = View.VISIBLE
    } else {
      holder.orig_cost.visibility = View.GONE
    }

    holder.removePackage.setOnClickListener {
      selectedBundle.item_name?.let { it1 ->
        WebEngageController.trackEvent(
          ADDONS_MARKETPLACE_PACKAGE_CROSSED_DELETED_FROM_CART,
          ADDONS_MARKETPLACE,
          it1
        )
      }
      listener.deleteCartAddonsItem(bundlesList.get(position).item_id)
    }
//    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//    if (bundlesList.size - 1 == position) {
//      holder.view.visibility = View.GONE
//    }

    //showing package details
    holder.itemView.setOnClickListener {
      listener.showBundleDetails(bundlesList.get(position).item_id)
    }

//    val features = arrayListOf<CartModel>()
//    val bundles = arrayListOf<CartModel>()
//    for (items in it) {
//      if (items.item_type.equals("features")) {
//        features.add(items)
//      } else if (items.item_type.equals("bundles")) {
//        bundles.add(items)
//      }
//    }

  //  val listSamp = ArrayList<String>()
//    for( item in parentItem.included_features){
////            Log.v("onBindViewHolder", " "+ item.feature_code)
//      listSamp.add(item.feature_code)
//    }
//    val listSamp = ArrayList<String>()
//    val distinct: List<String> = LinkedHashSet(listSamp).toMutableList()
//    val layoutManager1 = LinearLayoutManager(context)
//
//    CompositeDisposable().add(
//      AppDatabase.getInstance(Application())!!
//        .cartDao()
//        .getCartItems()
//        .subscribeOn(Schedulers.io())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//          {
//            bundlesList = it as ArrayList<CartModel>
//            val features = arrayListOf<CartModel>()
//            val bundles = arrayListOf<CartModel>()
//
//            val itemIds = java.util.ArrayList<String?>()
//
//            for(listItems in it){
//              CompositeDisposable().add(
//                AppDatabase.getInstance(Application())!!
//                  .featuresDao()
////                                                        .getFeatureListTargetBusiness(listItems.target_business_usecase,itemIds)
//                  .getAllFeatures()
//                  .subscribeOn(Schedulers.io())
//                  .observeOn(AndroidSchedulers.mainThread())
//                  .subscribe({
//
//                    for (item in it) {
//                      itemIds.add(item.feature_code)
//                    }
//
//
//                    if (it != null) {
//
//                      Log.v("getFeatureListTarget", " "+ itemIds )
//
//                      val sectionLayout = ChildPackAdapter(it)
//                      holder.ChildRecyclerView
//                        .setAdapter(sectionLayout)
//                      holder.ChildRecyclerView
//                        .setLayoutManager(layoutManager1)
//
//
//                    } else {
////                                                                Toasty.error(requireContext(), "Bundle Not Available To This Account", Toast.LENGTH_LONG).show()
//                    }
//                  }, {
//                    it.printStackTrace()
//                  })
//              )
//            }
//          },
//          {
//            it.printStackTrace()
//
//          }
//        )
//    )
  }


  fun addupdates(upgradeModel: List<CartModel>) {
    val initPosition = bundlesList.size
    bundlesList.clear()
    bundlesList.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, bundlesList.size)
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.package_title)
    val price = itemView.findViewById<TextView>(R.id.package_price)
    val orig_cost = itemView.findViewById<TextView>(R.id.package_orig_cost)

    //  val discount = itemView.findViewById<TextView>(R.id.package_discount)
    val image = itemView.findViewById<ImageView>(R.id.package_profile_image)
    val removePackage = itemView.findViewById<ImageView>(R.id.package_close)
    val ChildRecyclerView = itemView.findViewById<RecyclerView>(R.id.child_recyclerview)
    //   var view = itemView.findViewById<View>(R.id.cart_single_package_bottom_view)!!
  }

  fun spannableString(holder: upgradeViewHolder, value: Double, minMonth: Int) {
    val origCost: SpannableString
    if (minMonth > 1) {
      origCost = SpannableString(
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/" + minMonth + "mths"
      )
    } else {
      origCost =
        SpannableString("₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + "/mth")
    }

    origCost.setSpan(
      StrikethroughSpan(),
      0,
      origCost.length,
      0
    )
    holder.orig_cost.text = origCost
  }
}