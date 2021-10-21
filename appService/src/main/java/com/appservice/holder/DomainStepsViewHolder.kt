package com.appservice.holder

import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.appservice.R
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

    getColor(if(model.isTextDark) R.color.black_333333 else R.color.black_4a4a4a)?.let {
      binding.tvDesc.setTextColor(it)
      binding.tvCount.setTextColor(it)
    }
    binding.tvDesc.movementMethod = LinkMovementMethod.getInstance()
    binding.tvDesc.text = Html.fromHtml(model.desc.toString())
    binding.tvCount.text= if(model.isBulletIndicated) "\u2022" else (position.plus(1)).toString()+"."
  }
}
