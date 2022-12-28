package com.boost.dbcenterapi.recycleritem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.infra.api.models.test.StringData
import com.framework.base.BaseActivity

abstract class BaseRecyclerStringAdapter<T: StringData>(
  var activity: BaseActivity<*, *>,
  var list: T,
  private var itemClickListener: RecyclerStringItemClickListener?,
) : RecyclerView.Adapter<BaseRecyclerStringHolder<*>>() {

  protected var isLoaderVisible = false

  override fun onBindViewHolder(viewHolder: BaseRecyclerStringHolder<*>, position: Int) {
    viewHolder.activity = activity
    viewHolder.listener = itemClickListener
    viewHolder.itemCount = itemCount
    viewHolder.bind(position, list.data[position])
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerStringHolder<*> {
    return getViewHolder(parent, viewType)
  }

  abstract fun getViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerStringHolder<*>

  override fun getItemViewType(position: Int): Int {
    return list.getViewType()
  }

  override fun getItemCount(): Int = list.data.size

  fun updateList(newList: ArrayList<String>) {
    this.list.data.clear()
    this.list.data.addAll(newList)
    notifyDataSetChanged()
  }

  fun insertItem(index: Int, item: String) {
    this.list.data.add(index, item)
  }

  fun removeAt(index: Int) {
    this.list.data.removeAt(index)
  }


  protected fun getViewDataBinding(
    inflater: LayoutInflater,
    recyclerStringItemType: Int,
    parent: ViewGroup
  ): ViewDataBinding {
    return DataBindingUtil.inflate(inflater, recyclerStringItemType, parent, false)
  }
}