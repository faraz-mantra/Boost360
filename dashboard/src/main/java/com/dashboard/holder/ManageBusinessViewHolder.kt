package com.dashboard.holder

import android.view.View
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemManageBusinessDBinding
import com.dashboard.model.live.addOns.ManageBusinessData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad

class ManageBusinessViewHolder(binding: ItemManageBusinessDBinding) : AppBaseRecyclerViewHolder<ItemManageBusinessDBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ManageBusinessData ?: return
    binding.txtTitle.text = data.title
    ManageBusinessData.BusinessType.fromName(data.businessType)?.let { binding.imgIcon.setImageResource(it.icon) }
    binding.imgLock.visibility = if (data.isLock) View.VISIBLE else View.GONE
    binding.imgIcon.apply { if (data.isLock) this.makeGreyscale() else this.removeGreyscale() }
    binding.mainContent.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal) }
  }
}


