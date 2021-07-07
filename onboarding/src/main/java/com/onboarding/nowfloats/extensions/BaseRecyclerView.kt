package com.onboarding.nowfloats.extensions

import androidx.recyclerview.widget.GridLayoutManager
import com.framework.base.BaseActivity
import com.framework.views.viewgroups.BaseRecyclerView
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

fun <T : AppBaseRecyclerViewItem> BaseRecyclerView.setGridRecyclerViewAdapter(
  activity: BaseActivity<*, *>,
  spanCount: Int,
  list: List<T>
): AppBaseRecyclerViewAdapter<T> {
  val adapter = AppBaseRecyclerViewAdapter(activity, ArrayList(list))
  this.adapter = adapter
  this.layoutManager = GridLayoutManager(activity, spanCount)
  return adapter
}