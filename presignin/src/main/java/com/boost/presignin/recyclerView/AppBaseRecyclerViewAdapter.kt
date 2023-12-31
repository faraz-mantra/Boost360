package com.boost.presignin.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewItemType.*
import com.boost.presignin.databinding.*
import com.boost.presignin.holder.*
import com.boost.presignin.holder.common.SectionHeaderRecyclerViewHolder
import com.boost.presignin.holder.common.IntroSlidesViewHolder
import com.framework.base.BaseActivity
import java.util.*

open class AppBaseRecyclerViewAdapter<T : AppBaseRecyclerViewItem>(
  activity: BaseActivity<*, *>,
  list: ArrayList<T>,
  itemClickListener: RecyclerItemClickListener? = null
) : BaseRecyclerViewAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerViewItemType = values().first { it.getLayout() == viewType }
    val binding = getViewDataBinding(inflater, recyclerViewItemType, parent)
    return when (recyclerViewItemType) {
      PAGINATION_LOADER -> PagingViewHolder(binding as PaginationLoaderBinding)
      SECTION_HEADER_ITEM -> SectionHeaderRecyclerViewHolder(binding as ItemSectionHeaderLayoutBinding)
      CATEGORY_ITEM -> CategoryRecyclerViewHolder(binding as ItemCategoryLayoutBinding)
      CATEGORY_ITEM_OV2 -> CategoryOv2RecyclerViewHolder(binding as ItemWebsiteCategoriesBinding)
      BUSINESS_LIST_ITEM -> BusinessListViewHolder(binding as RecyclerItemFpInfoBinding)
      INTRO_NEW_SLIDES -> IntroSlidesViewHolder(binding as ItemIntroNewSlidesBinding)
      CATEGORY_SUGGESTION_ITEM -> CategorySuggestionViewHolder(binding as ListItemCategorySuggestionBinding)
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
      return if (position == list.size - 1) PAGINATION_LOADER.getLayout() else super.getItemViewType(
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