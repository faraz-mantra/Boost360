package com.appservice.holder

import com.appservice.databinding.ItemPdfFileBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class AdditionalFileViewHolder(binding: ItemPdfFileBinding) :
  AppBaseRecyclerViewHolder<ItemPdfFileBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? FileModel
    binding.bankAdditionalDoc.text = data?.getFileName()
  }
}
