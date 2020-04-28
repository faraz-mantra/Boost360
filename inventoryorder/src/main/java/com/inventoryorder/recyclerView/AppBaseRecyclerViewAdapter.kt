package com.inventoryorder.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType.*
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.databinding.ItemOrderTypeBinding
import com.inventoryorder.holders.OrderItemViewHolder
import com.inventoryorder.holders.OrderTypeViewHolder

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
      ORDER_ITEM_TYPE -> OrderTypeViewHolder(binding as ItemOrderTypeBinding)
      INVENTORY_ORDER_ITEM -> OrderItemViewHolder(binding as ItemOrderBinding)
    }
  }

  fun runLayoutAnimation(recyclerView: RecyclerView?, anim: Int = R.anim.layout_animation_fall_down) = recyclerView?.apply {
    layoutAnimation = AnimationUtils.loadLayoutAnimation(context, anim)
    notifyDataSetChanged()
    scheduleLayoutAnimation()
  }

  fun notify(list: ArrayList<T>?) {
    if (list != null) {
      this.list = list
      notifyDataSetChanged()
    }
  }
}