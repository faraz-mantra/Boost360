package com.dashboard.holder

import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBusinessManagementBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.model.BusinessSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessSetupViewHolder(binding: ItemBusinessManagementBinding) : AppBaseRecyclerViewHolder<ItemBusinessManagementBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? BusinessContentSetupData ?: return
    binding.txtTitle.text = data.title
    binding.txtDes.text = data.subTitle
    if (data.type == BusinessSetupData.ActiveViewType.ONLINE_SYNC.name) {
      getColor(R.color.light_green_2)?.let { binding.txtDes.setTextColor(it) }
      binding.viewBtn.gone()
      binding.viewImage.gone()
      binding.lottySyncOk.visible()
      startCheckAnimation(true)
    } else {
      getColor(R.color.light_grey_3)?.let { binding.txtDes.setTextColor(it) }
      binding.viewBtn.visible()
      binding.viewImage.visible()
      binding.lottySyncOk.gone()
      startCheckAnimation(false)
      val btnTxt = data.getPendingText()
      binding.btnTitle.text = if (btnTxt.isNullOrEmpty().not()) "Add $btnTxt" else data.getCompleteText()
      binding.imgArrowGif.apply {
        data.gifIcon?.let { gifResource = it }
        play()
      }
      binding.progressBar.setProgressWithAnimation((100 - (data.percentage ?: 0)).toFloat(), 1000)
      data.icon2?.let { binding.imgIcon.setImageResource(it) }
    }
    binding.mainContent.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal) }
  }

  private fun startCheckAnimation(isAnimate: Boolean) {
    binding.lottySyncOk.apply { if (isAnimate) playAnimation() else pauseAnimation() }
  }
}
