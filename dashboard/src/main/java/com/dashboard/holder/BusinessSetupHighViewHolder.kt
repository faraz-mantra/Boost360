package com.dashboard.holder

import androidx.core.content.ContextCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBusinessSetupHighBinding
import com.dashboard.databinding.ItemDetailBusinessBinding
import com.dashboard.model.BusinessSetupHighData
import com.dashboard.model.Specification
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
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
        binding.viewVisitor.setData(listener, position, data.siteVisitor)
        binding.viewBooking.setData(listener, position, data.booking)
        binding.viewEnquiry.setData(listener, position, data.enquiry)
      }
      BusinessSetupHighData.ActiveViewType.IS_PROGRESS.name -> {
        binding.viewReadinessScore.visible()
        binding.viewBusinessCount.gone()
        binding.txtTitle3.text = data.title1
        binding.txtPercentage.text = "${data.score}%"
        val isHigh = (data.score ?: 0 >= 85)
//        binding.txtPercentage.setTextColor(ContextCompat.getColor(activity!!, if (isHigh) R.color.light_green_3 else R.color.accent_dark))
//        binding.progressBar.progressDrawable = ContextCompat.getDrawable(activity!!, if (isHigh) R.drawable.ic_progress_bar_high_grey else R.drawable.progress_bar_horizontal)
        data.score?.let { binding.progressBar.progress = it }
        binding.viewReadinessScore.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.READING_SCORE_CLICK.ordinal) }
      }
    }
  }
}

private fun ItemDetailBusinessBinding.setData(listener: RecyclerItemClickListener?, position: Int, item: Specification?) {
  txtTitle.text = item?.title
  txtValue.text = item?.value
  viewBusinessCount.setOnClickListener { listener?.onItemClick(position, item, RecyclerViewActionType.BUSINESS_UPDATE_CLICK.ordinal) }
}
