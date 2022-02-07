package com.festive.poster.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.*
import com.festive.poster.recyclerView.viewholders.*
import com.festive.poster.ui.promoUpdates.holders.SoicalConnViewHolder
import com.festive.poster.ui.promoUpdates.holders.TemplateForRVViewHolder
import com.festive.poster.ui.promoUpdates.holders.TemplateForVPViewHolder
import com.festive.poster.ui.promoUpdates.holders.TodaysPickTemplateListViewHolder
import com.framework.base.BaseActivity
import java.util.*
import kotlin.collections.ArrayList

open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
  activity: BaseActivity<*, *>,
  list: ArrayList<T>,
  itemClickListener: RecyclerItemClickListener? = null
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = RecyclerViewItemType.values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      RecyclerViewItemType.POSTER_PACK->PosterPackViewHolder(binding as ListItemPosterPackBinding)
      RecyclerViewItemType.POSTER->PosterViewHolder(binding as ListItemPosterBinding)
      RecyclerViewItemType.POSTER_PACK_PURCHASED->PosterPackPurchasedViewHolder(binding as ListItemPurchasedPosterPackBinding) // Not currently used
      RecyclerViewItemType.POSTER_SHARE-> PosterShareViewHolder(binding as ListItemPosterShareBinding)
      RecyclerViewItemType.TODAYS_PICK_TEMPLATE_VIEW-> TodaysPickTemplateListViewHolder(binding as ListItemTodaysPickTemplateBinding)
      RecyclerViewItemType.TEMPLATE_VIEW_FOR_VP-> TemplateForVPViewHolder(binding as ListItemTemplateForVpBinding)
      RecyclerViewItemType.TEMPLATE_VIEW_FOR_RV-> TemplateForRVViewHolder(binding as ListItemTemplateForRvBinding)

      RecyclerViewItemType.SOCIAL_CONN-> SoicalConnViewHolder(binding as ListItemSocialConnBinding)
      RecyclerViewItemType.BROWSE_TAB_TEMPLATE_CAT->BrowseTabPosterCatViewHolder(binding as ListItemBrowseTabTemplateCatBinding)
      RecyclerViewItemType.BROWSE_ALL_TEMPLATE_CAT->BrowseAllPosterCatViewHolder(binding as ListItemBrowseAllCatBinding)
      RecyclerViewItemType.SOCIAL_PLATFORM_POST_OPTIONS_LIST->SocialPlatformOptionListViewHolder(binding as ItemSocialPlatformPromoAdapBinding)
      RecyclerViewItemType.VIEWPAGER_TWITTER_PREVIEW->TwitterPreviewViewHolder(binding as SocialPreviewTwitterBinding)
      RecyclerViewItemType.FB_PREVIEW->FBPreviewViewHolder(binding as SocialPreviewFbBinding)
      RecyclerViewItemType.INSTAGRAM_PREVIEW->InstagramPreviewViewHolder(binding as SocialPreviewInstagramBinding)
      RecyclerViewItemType.GMB_PREVIEW->GMBPreviewViewHolder(binding as SocialPreviewGmbBinding)
      RecyclerViewItemType.WEBSITE_PREVIEW->WebsitePreviewViewHolder(binding as SocialPreviewWebsiteBinding)
      RecyclerViewItemType.EMAIL_PREVIEW->EmailPreviewViewHolder(binding as SocialPreviewEmailBinding)

    }
  }



  override fun getItemViewType(position: Int): Int {
    return super.getItemViewType(position)
  }

  fun notify(list: ArrayList<T>?) {
    list?.let { updateList(it) }
  }

  open fun addItems(addList: ArrayList<T>?) {
    addList?.let { list.addAll(it) }
    notifyDataSetChanged()
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