package com.dashboard.holder

import android.view.View
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemCustomerPatientItemBinding
import com.dashboard.model.live.customerItem.CustomerActionItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem

class CustomerPatientItemViewHolder(binding: ItemCustomerPatientItemBinding) : AppBaseRecyclerViewHolder<ItemCustomerPatientItemBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? CustomerActionItem ?: return
    binding.textViewName.text = data.title
    val iconType = data.type?.let { CustomerActionItem.IconType.fromName(it) }
    iconType?.let { binding.imageViewIcon.setImageResource(iconType.icon) }
    binding.viewLock.visibility = if (data.isLock == true) View.VISIBLE else View.GONE
    binding.mainContent.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.CUSTOMER_PATIENT_ITEM_CLICK.ordinal) }
  }
}
