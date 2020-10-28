package com.dashboard.holder

import android.view.View
import com.dashboard.databinding.ItemManageBusinessDBinding
import com.dashboard.model.ManageBusinessData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class ManageBusinessViewHolder(binding: ItemManageBusinessDBinding) : AppBaseRecyclerViewHolder<ItemManageBusinessDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ManageBusinessData ?: return
    binding.txtTitle.text = data.title
    data.icon1?.let { binding.imgIcon.setImageResource(it) }
    binding.imgLock.visibility = if (data.isLock) View.VISIBLE else View.GONE
    binding.imgIcon.apply { if (data.isLock) this.makeGreyscale() else this.removeGreyscale() }
  }
}


