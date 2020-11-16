package com.dashboard.holder

import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBusinessSetupHighBinding
import com.dashboard.databinding.ItemDetailBusinessBinding
import com.dashboard.model.BusinessSetupHighData
import com.dashboard.model.Specification
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessSetupHighViewHolder(binding: ItemBusinessSetupHighBinding) : AppBaseRecyclerViewHolder<ItemBusinessSetupHighBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? BusinessSetupHighData ?: return
    when (data.type) {
      BusinessSetupHighData.ActiveViewType.IS_BUSINESS_UPDATE.name -> {
        binding.viewReadinessScore.gone()
        binding.viewBusinessCount.visible()
        binding.txtTitle1.text = data.title1
        binding.txtTitle2.text = data.title2
        binding.viewVisitor.setData(data.siteVisitor)
        binding.viewBooking.setData(data.booking)
        binding.viewEnquiry.setData(data.enquiry)
      }
      BusinessSetupHighData.ActiveViewType.IS_PROGRESS.name -> {
        binding.viewReadinessScore.visible()
        binding.viewBusinessCount.gone()
        binding.txtTitle3.text = data.title1
        data.score?.let { binding.progressBar.progress = it }
        binding.viewReadinessScore.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.READING_SCORE_CLICK.ordinal) }
      }
    }
  }
}

private fun ItemDetailBusinessBinding.setData(item: Specification?) {
  txtTitle.text = item?.title
  txtValue.text = item?.value
}
