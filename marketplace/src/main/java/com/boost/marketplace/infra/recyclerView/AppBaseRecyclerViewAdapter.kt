package com.boost.marketplace.infra.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewItem
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewAdapter
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.RecyclerItemClickListener
import com.boost.marketplace.R
import com.boost.dbcenterapi.recycleritem.RecyclerViewItemType.*
import com.boost.marketplace.databinding.*
import com.boost.marketplace.holder.*
import com.boost.marketplace.infra.constant.getLayout
import com.framework.base.BaseActivity
import java.util.*

open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
  activity: BaseActivity<*, *>,
  list: ArrayList<T>,
  itemClickListener: RecyclerItemClickListener? = null
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.ordinal == viewType }
    val binding = getViewDataBinding(inflater,  getLayout(recyclerViewItemType), parent)
    return when (recyclerViewItemType) {
      PAGINATION_LOADER -> PagingViewHolder(binding as PaginationLoaderBinding)
      BUSINESS_SETUP_ITEM_VIEW -> BusinessSetupViewHolder(binding as ItemBusinessManagementDBinding)
      PROMO_BANNER ->MarketPlacePromoBannerViewHolder(binding as ItemPromoBannerBinding)
      FEATURES_BY_CATEGORY->FeaturesByCategoryViewHolder(binding as ItemFeaturesByCategoryBinding)
      MARKETPLACE_OFFERS->MarketPlaceOfferDetailsViewHolder(binding as ItemMarketplaceoffersInfoBinding)
      MARKETPLACE_OFFERS->MarketPlaceOfferTermsViewHolder(binding as ItemMarketplaceoffersInfoBinding)
      FEATURES_MODEL->MyPlanPremiumFeaturesViewHolder(binding as ItemMyplanFeaturesBinding)
      FEATURES_MODEL->MyPlanFreeFeaturesViewHolder(binding as ItemMyplanFeaturesBinding)
      RESULT->MyPlanHistoryOrdersViewHolder(binding as ItemOrderHistoryBinding)
      PACKS_BUNDLES->Packs_ViewHolder(binding as ItemPacksListBinding)
      PACKS_BUNDLES->ComparePacksViewHolder(binding as ActivityPacksBinding)



    }
  }



  fun runLayoutAnimation(
    recyclerView: RecyclerView?,
    anim: Int = R.anim.layout_animation_fall_down
  ) = recyclerView?.apply {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, anim)
    notifyDataSetChanged()
    scheduleLayoutAnimation()
  }

  override fun getItemViewType(position: Int): Int {
    return if (isLoaderVisible) {
      return if (position == list.size - 1) PAGINATION_LOADER.ordinal else super.getItemViewType(
        position
      )
    } else super.getItemViewType(position)
  }

  fun notify(list: ArrayList<T>?) {
    list?.let { updateList(it) }
  }

  open fun addItems(addList: ArrayList<T>?) {
    addList?.let { list.addAll(it) }
    notifyDataSetChanged()
  }

   fun addupdates(purchaseResult: List<T>) {
    val initPosition = list.size
    list.clear()
    list.addAll(purchaseResult)
    notifyItemRangeInserted(initPosition, list.size)
  }

  override fun getItemCount(): Int {
    return if (list.isNotEmpty()) list.size else 0
  }


  open fun remove(item: T) {
    val position = list.indexOf(item)
    if (position > -1) {
      list.removeAt(position)
      notifyItemRemoved(position)
    }
  }

  open fun clear() {
    isLoaderVisible = false
    while (itemCount > 0) {
      getItem(0)?.let { remove(it) }
    }
  }

  open fun isEmpty(): Boolean {
    return itemCount == 0
  }

  open fun addLoadingFooter(t: T) {
    isLoaderVisible = true
    list.add(t)
    notifyItemInserted(list.size - 1)
  }

  open fun removeLoadingFooter() {
    isLoaderVisible = false
    val position = list.size - 1
    if (position > -1) {
      val item: T? = getItem(position)
      if (item != null) {
        list.removeAt(position)
        notifyItemRemoved(position)
      }
    }
  }

  open fun getItem(position: Int): T? {
    return list[position]
  }

  open fun list(): ArrayList<T> {
    return list
  }

  // New Function
  open fun clearAllItem() {
    val size = itemCount
    list.clear()
    notifyItemRangeRemoved(0, size)
  }

  open fun insertItem(`object`: T, index: Int) {
    list.add(index, `object`)
    notifyItemInserted(index)
  }

  open fun positionItem(item: T): Int {
    return list.indexOf(item)
  }

  open fun addItem(`object`: T) {
    list.add(`object`)
    notifyItemInserted(itemCount - 1)
  }

  open fun removeItem(`object`: T) {
    val position: Int = positionItem(`object`)
    list.remove(`object`)
    notifyItemRemoved(position)
  }

  open fun sortItem(comparator: Comparator<in T?>?) {
    Collections.sort(list, comparator)
    notifyItemRangeChanged(0, itemCount)
  }
  // New Function




}