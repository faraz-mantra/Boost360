package com.boost.cart.adapter

import android.app.Activity
import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.boost.cart.R
import com.boost.cart.interfaces.ActionRequiredListener
import com.boost.cart.interfaces.CartFragmentListener
import com.boost.cart.utils.SharedPrefs
import com.boost.cart.utils.Utils
import com.boost.cart.utils.Utils.priceCalculatorForYear
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.IncludedFeature
import com.boost.dbcenterapi.upgradeDB.local.AppDatabase
import com.boost.dbcenterapi.upgradeDB.model.CartModel
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.utils.WebEngageController
import com.bumptech.glide.Glide
import com.framework.utils.RootUtil
import com.framework.webengageconstant.ADDONS_MARKETPLACE
import com.framework.webengageconstant.ADDONS_MARKETPLACE_PACKAGE_CROSSED_DELETED_FROM_CART
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.text.NumberFormat
import java.util.*


class CartPackageAdaptor(
        list: List<CartModel>?,
        val listener: CartFragmentListener,
        val listener1: ActionRequiredListener,
        cryptoCurrencies: List<FeaturesModel>?,
        val activity: Activity
) : RecyclerView.Adapter<CartPackageAdaptor.upgradeViewHolder>() {

  private var bundlesList = ArrayList<CartModel>()
  private lateinit var context: Context
  private var upgradeList = ArrayList<FeaturesModel>()
  var minMonth = 1
  var selectedDomainName = ""
  var selectedVMN = ""


  init {
    this.bundlesList = list as ArrayList<CartModel>
    this.upgradeList = cryptoCurrencies as ArrayList<FeaturesModel>
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
    val price = RootUtil.round(priceCalculatorForYear(selectedBundle.price, "", activity),2)
    val MRPPrice = RootUtil.round(priceCalculatorForYear(selectedBundle.MRPPrice, "", activity),2)
    holder.price.text = "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(price) + Utils.yearlyOrMonthlyOrEmptyValidity("", activity, selectedBundle.min_purchase_months)

    if (selectedBundle.link != null) {
      Glide.with(context).load(selectedBundle.link!!)//.placeholder(R.drawable.boost_360_insignia)
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
      if(selectedDomainName.isNotEmpty()){
        val prefs = SharedPrefs(activity)
        prefs.storeSelectedDomainName(null)
      }
      if(selectedVMN.isNotEmpty()){
        val prefs = SharedPrefs(activity)
        prefs.storeSelectedVMNName(null)
      }
      listener.deleteCartAddonsItem(bundlesList.get(position))
    }

    if(selectedDomainName.isNotEmpty()){
      holder.edge_cases_layout.visibility = View.GONE
    }else{
      holder.edge_cases_layout.visibility = View.VISIBLE
    }

    if(selectedVMN.isNotEmpty()){
      holder.edge_cases_layout.visibility = View.GONE
    }else{
      holder.edge_cases_layout.visibility = View.VISIBLE
    }

    holder.edge_cases_layout.setOnClickListener {
      listener1.actionClick(bundlesList.get(position))
    }

    updateFeatures(bundlesList.get(position), holder)

//    holder.view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//    if (bundlesList.size - 1 == position) {
//      holder.view.visibility = View.GONE
//    }

    //showing package details
//    holder.itemView.setOnClickListener {
//      listener.showBundleDetails(bundlesList.get(position).item_id)
//    }

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

  fun addupdate(upgradeModel: List<FeaturesModel>) {
    val initPosition = upgradeList.size
    upgradeList.clear()
    upgradeList.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, upgradeList.size)
  }

  fun selectedDomain(domainName: String){
    this.selectedDomainName = domainName
    notifyDataSetChanged()
  }

  fun selectedVmn(VMN: String){
    this.selectedVMN = VMN
    notifyDataSetChanged()
  }

  class upgradeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name = itemView.findViewById<TextView>(R.id.package_title)
    val price = itemView.findViewById<TextView>(R.id.package_price)
    val orig_cost = itemView.findViewById<TextView>(R.id.package_orig_cost)
//    val used_by = itemView.findViewById<TextView>(R.id.used_by)

    //  val discount = itemView.findViewById<TextView>(R.id.package_discount)
    val image = itemView.findViewById<ImageView>(R.id.package_profile_image)
    val removePackage = itemView.findViewById<ImageView>(R.id.package_close)
    val ChildRecyclerView = itemView.findViewById<RecyclerView>(R.id.child_recyclerview)
    val addon_amount = itemView.findViewById<TextView>(R.id.tv_Addons)
    val edge_cases_layout = itemView.findViewById<ConstraintLayout>(R.id.edge_cases_layout)
    var adapter:NewAddonsAdapter? = null
    //   var view = itemView.findViewById<View>(R.id.cart_single_package_bottom_view)!!
  }

  fun spannableString(holder: upgradeViewHolder, value: Double, minMonth: Int) {
    val origCost: SpannableString
      origCost = SpannableString(
        "₹" + NumberFormat.getNumberInstance(Locale.ENGLISH).format(value) + Utils.yearlyOrMonthlyOrEmptyValidity("", activity, minMonth)
      )

    origCost.setSpan(
      StrikethroughSpan(),
      0,
      origCost.length,
      0
    )
    holder.orig_cost.text = origCost
  }

  fun updateFeatures(bundleItem: CartModel, holder: upgradeViewHolder) {
    CompositeDisposable().add(
      AppDatabase.getInstance(activity.application)!!
        .bundlesDao()
        .getBundleItemById(bundleItem.item_id)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val temp = Gson().fromJson<List<IncludedFeature>>(it.included_features, object : TypeToken<List<IncludedFeature>>() {}.type)
          var tempFeatures = ArrayList<FeaturesModel>()
          for(singleFeaturesCode in temp){
            for(singleFeature in upgradeList) {
              if (singleFeaturesCode.feature_code.equals(singleFeature.feature_code!!)) {
                if(singleFeaturesCode.feature_code.equals("DOMAINPURCHASE") && selectedDomainName.isNotEmpty()){
                  val tempItem = singleFeature
                  tempItem.name = selectedDomainName
                  tempFeatures.add(tempItem)
                }else if ((singleFeaturesCode.feature_code.equals("IVR")||(singleFeaturesCode.feature_code.equals("CALLTRACKER"))) && selectedVMN.isNotEmpty()) {
                  val tempItem = singleFeature
                  tempItem.name = selectedVMN
                  tempFeatures.add(tempItem)
                }else {
                    tempFeatures.add(singleFeature)
                  }
                }
              }
          }
          holder.addon_amount.text = "Includes "+tempFeatures.size+" addons"
          val linearLayoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
          holder.adapter = NewAddonsAdapter(tempFeatures, listener, bundleItem, activity)
          holder.ChildRecyclerView.adapter = holder.adapter
          holder.ChildRecyclerView.layoutManager = linearLayoutManager
//          holder.used_by.setText("Used by "+it.+"+ businesses")
        }, {
//                            updatesError.postValue(it.message)
        })
    )
  }
}
