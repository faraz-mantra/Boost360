package com.inventoryorder.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType.ORDER_ITEM
import com.inventoryorder.constant.RecyclerViewItemType.values
import com.inventoryorder.databinding.ItemOrderBinding
import com.inventoryorder.holders.OrderItemViewHolder

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
      ORDER_ITEM -> OrderItemViewHolder(binding as ItemOrderBinding)
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

  fun notify(list: ArrayList<T>?) {
    if (list != null) {
      this.list = list
      notifyDataSetChanged()
    }
  }
}