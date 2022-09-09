package com.boost.marketplace.infra.recyclerView

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.infra.api.models.test.StringData
import com.boost.dbcenterapi.recycleritem.*
import com.boost.marketplace.R
import com.boost.dbcenterapi.recycleritem.RecyclerStringItemType.*
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.marketplace.databinding.*
import com.boost.marketplace.holder.*
import com.boost.marketplace.holder.stringHolder.SampleStringViewHolder
import com.boost.marketplace.infra.constant.getStringLayout
import com.bumptech.glide.Glide
import com.framework.base.BaseActivity
import java.util.*
import kotlin.collections.ArrayList

open class AppBaseRecyclerStringAdapter<T: StringData>(
  activity: BaseActivity<*, *>,
  list: T,
  itemClickListener: RecyclerStringItemClickListener? = null
) : BaseRecyclerStringAdapter<T>(activity, list, itemClickListener) {

  override fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerStringHolder<*> {
    val inflater = LayoutInflater.from(parent.context)
    val recyclerStringItemType = values().first { it.ordinal == viewType }
    val binding = getViewDataBinding(inflater,  getStringLayout(recyclerStringItemType), parent)
    return when (recyclerStringItemType) {
      PAGINATION_LOADER -> PagingStringHolder(binding as PaginationStringLoaderBinding)
      STRING_LIST -> SampleStringViewHolder(binding as SecondaryStringItemBinding)
    }
  }

@SuppressLint("NotifyDataSetChanged")
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
      return if (position == list.data.size - 1) PAGINATION_LOADER.ordinal else super.getItemViewType(
        position
      )
    } else super.getItemViewType(position)
  }

  fun notify(list: ArrayList<String>?) {
    list?.let { updateList(it) }
  }

  @SuppressLint("NotifyDataSetChanged")
  open fun addItems(addList: ArrayList<String>?) {
    addList?.let { list.data.addAll(it) }
    notifyDataSetChanged()
  }

   fun addupdates(purchaseResult: List<String>) {
    val initPosition = list.data.size
    list.data.clear()
    list.data.addAll(purchaseResult)
    notifyItemRangeInserted(initPosition, list.data.size)
  }

  fun addupdates(upgradeModel: List<String>, noOfMonth: Int) {
    var minMonth = 1
    minMonth = noOfMonth
    val initPosition = list.data.size
    list.data.clear()
    list.data.addAll(upgradeModel)
    notifyItemRangeInserted(initPosition, list.data.size)
  }



  override fun getItemCount(): Int {
    return if (list.data.isNotEmpty()) list.data.size else 0
  }


  open fun remove(item: String) {
    val position = list.data.indexOf(item)
    if (position > -1) {
      list.data.removeAt(position)
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

  open fun addLoadingFooter(t: String) {
    isLoaderVisible = true
    list.data.add(t)
    notifyItemInserted(list.data.size - 1)
  }

  open fun removeLoadingFooter() {
    isLoaderVisible = false
    val position = list.data.size - 1
    if (position > -1) {
      val item: String? = getItem(position)
      if (item != null) {
        list.data.removeAt(position)
        notifyItemRemoved(position)
      }
    }
  }

  open fun getItem(position: Int): String? {
    return list.data[position]
  }

  open fun list(): ArrayList<String> {
    return list.data
  }

  // New Function
  open fun clearAllItem() {
    val size = itemCount
    list.data.clear()
    notifyItemRangeRemoved(0, size)
  }

  open fun insertItem(`object`: String, index: Int) {
    list.data.add(index, `object`)
    notifyItemInserted(index)
  }

  open fun positionItem(item: T,position: Int): Int {
    return list.data.indexOf(item.data[position])
  }

//  open fun addItem(`object`: T) {
//    list.data.add(`object`)
//    notifyItemInserted(itemCount - 1)
//  }
//
//  open fun removeItem(`object`: T) {
//    val position: Int = positionItem(`object`)
//    list.data.remove(`object`.data)
//    notifyItemRemoved(position)
//  }

  open fun sortItem(comparator: Comparator<in String?>?) {
    Collections.sort(list.data, comparator)
    notifyItemRangeChanged(0, itemCount)
  }
  // New Function

}