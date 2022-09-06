package com.boost.dbcenterapi.recycleritem

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.framework.base.BaseActivity

abstract class BaseRecyclerStringHolder<Binding : ViewDataBinding> constructor(var binding: Binding) :
  RecyclerView.ViewHolder(binding.root), View.OnClickListener {

  var listener: RecyclerStringItemClickListener? = null
  var activity: BaseActivity<*, *>? = null
  var itemCount: Int? = null

  open fun bind(position: Int, item: String) {

  }

  override fun onClick(v: View?) {

  }



}