package com.framework.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.framework.models.BaseRecyclerViewItem
import com.framework.viewholders.BaseViewHolder

abstract class BaseRecyclerViewAdapter<T : BaseRecyclerViewItem>(
  context: Context,
  protected var list: ArrayList<T>
) : androidx.recyclerview.widget.RecyclerView.Adapter<BaseViewHolder>() {
  private var inflater: LayoutInflater = LayoutInflater.from(context)

  var itemClickListener: RecyclerViewItemClickListener? = null
    private set

  fun setOnItemClickListener(clickInterface: RecyclerViewItemClickListener) {
    this.itemClickListener = clickInterface
  }

  override fun onCreateViewHolder(parent: ViewGroup, type: Int): BaseViewHolder {
    val view = inflater.inflate(getRecyclerViewLayout(type), parent, false)
    val viewHolder = getRecyclerViewHolder(view, type)
    viewHolder?.listener = itemClickListener
    return viewHolder ?: throw IllegalArgumentException("Invalid View Type")
  }

  override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
    holder.onBind(position, this.list[position])
  }

  override fun getItemViewType(position: Int): Int {
    return list[position].getRecyclerViewType()
  }

  override fun getItemCount(): Int {
    return list.size
  }

  @LayoutRes
  abstract fun getRecyclerViewLayout(viewType: Int): Int
  abstract fun getRecyclerViewHolder(view: View, type: Int): BaseViewHolder?
}

interface RecyclerViewItemClickListener {
  fun onItemClick(itemView: View, position: Int, obj: Any, actionType: Int)
}
