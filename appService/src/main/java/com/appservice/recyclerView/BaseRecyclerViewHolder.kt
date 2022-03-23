package com.appservice.recyclerView

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity

abstract class BaseRecyclerViewHolder<Binding : ViewDataBinding> constructor(
  var binding: Binding) :
  RecyclerView.ViewHolder(binding.root), View.OnClickListener {

  var listener: RecyclerItemClickListener? = null
  var activity: BaseActivity<*, *>? = null
  var list:List<BaseRecyclerViewItem>?=null
  var adapter:BaseRecyclerViewAdapter<*>?=null

  open fun bind(position: Int, item: BaseRecyclerViewItem) {

  }

  override fun onClick(v: View?) {

  }
}