package com.appservice.holder

import android.text.Html
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
    binding.tvDesc.text = Html.fromHtml(model.desc.toString())
    binding.tvCount.text= if(model.isBulletIndicated) "\u2022" else (position.plus(1)).toString()+"."
  }
}
