package com.onboarding.nowfloats.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity
import com.onboarding.nowfloats.constant.RecyclerViewItemType

abstract class BaseRecyclerViewAdapter<T : BaseRecyclerViewItem>(
  var activity: BaseActivity<*, *>,
  var list: ArrayList<T>,
  var itemClickListener: RecyclerItemClickListener?
) :
  RecyclerView.Adapter<BaseRecyclerViewHolder<*>>() {

  override fun onBindViewHolder(viewHolder: BaseRecyclerViewHolder<*>, position: Int) {
    viewHolder.activity = activity
    viewHolder.listener = itemClickListener
    viewHolder.bind(position, list[position])
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
    return getViewHolder(parent, viewType)
  }

  abstract fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*>

  override fun getItemViewType(position: Int): Int {
    return list[position].getViewType()
  }

  override fun getItemCount(): Int = list.size

  fun updateList(list: List<T>) {
    this.list.clear()
    this.list.addAll(list)
    notifyDataSetChanged()
  }

  fun insertItem(index: Int, item: T) {
    this.list.add(index, item)
  }

  fun removeAt(index: Int) {
    this.list.removeAt(index)
  }

  protected fun getViewDataBinding(
    inflater: LayoutInflater,
    recyclerViewItemType: RecyclerViewItemType,
    parent: ViewGroup
  ): ViewDataBinding {
    return DataBindingUtil.inflate(inflater, recyclerViewItemType.getLayout(), parent, false)
  }
}