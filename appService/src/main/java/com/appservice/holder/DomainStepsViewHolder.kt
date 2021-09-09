package com.appservice.holder

import android.text.method.LinkMovementMethod
import com.appservice.databinding.ItemPdfFileBinding
import com.appservice.databinding.ListItemStepsDomainBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.domainbooking.model.DomainStepsModel

class DomainStepsViewHolder(binding: ListItemStepsDomainBinding) :
  AppBaseRecyclerViewHolder<ListItemStepsDomainBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model = item as DomainStepsModel
    binding.tvDesc.movementMethod = LinkMovementMethod.getInstance()
    binding.tvDesc.text = model.desc
    binding.tvCount.text=(position.plus(1)).toString()+"."
  }
}
