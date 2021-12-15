package com.boost.marketplace.holder

import android.view.View
import com.boost.marketplace.constant.RecyclerViewActionType
import com.boost.marketplace.databinding.ItemManageBusinessBinding
import com.boost.marketplace.infra.api.models.live.addOns.ManageBusinessData
import com.boost.marketplace.infra.recyclerView.AppBaseRecyclerViewHolder
import com.boost.marketplace.recyclerView.BaseRecyclerViewItem

class ManageBusinessViewHolder(binding: ItemManageBusinessBinding) :
  AppBaseRecyclerViewHolder<ItemManageBusinessBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? ManageBusinessData ?: return
    binding.txtTitle.text = data.title
    ManageBusinessData.BusinessType.fromName(data.businessType)
      ?.let { binding.imgIcon.setImageResource(it.icon) }
    binding.imgLock.visibility = if (data.isLock) View.VISIBLE else View.GONE
    binding.imgIcon.apply { if (data.isLock) this.makeGreyscale() else this.removeGreyscale() }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(
        position,
        data,
        RecyclerViewActionType.BUSINESS_ADD_ONS_CLICK.ordinal
      )
    }
  }
}


