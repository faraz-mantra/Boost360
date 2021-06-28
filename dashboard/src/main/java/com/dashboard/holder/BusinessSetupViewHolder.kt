package com.dashboard.holder

import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.ItemBusinessManagementBinding
import com.dashboard.model.live.drScore.DrScoreSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessSetupViewHolder(binding: ItemBusinessManagementBinding) : AppBaseRecyclerViewHolder<ItemBusinessManagementBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DrScoreSetupData ?: return
    binding.txtTitle.text = data.type?.title
    binding.txtDes.text = data.getRemainingPercentage()
    if (data.percentage == 100) {
      getColor(R.color.light_green_2)?.let { binding.txtDes.setTextColor(it) }
      binding.viewBtn.gone()
      binding.viewImage.gone()
      binding.lottySyncOk.visible()
      startCheckAnimation(true)
    } else {
      val subTitle = data.getPendingText()
      binding.btnTitle.text = if (subTitle.isNullOrEmpty()) data.type?.title else subTitle
      getColor(R.color.red_light_1)?.let { binding.txtDes.setTextColor(it) }
      binding.viewBtn.visible()
      binding.viewImage.visible()
      binding.lottySyncOk.gone()
      startCheckAnimation(false)
      binding.imgArrowGif.apply { play() }
      binding.progressBar.setProgressWithAnimation((data.percentage ?: 0).toFloat(), 1000)
      data.type?.icon?.let { binding.imgIcon.setImageResource(it) }
    }
    binding.mainContent.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.BUSINESS_SETUP_SCORE_CLICK.ordinal) }
    binding.btnAddItemStart.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.BUSINESS_SETUP_ADD_ITEM_START.ordinal) }
  }

  private fun startCheckAnimation(isAnimate: Boolean) {
    binding.lottySyncOk.apply { if (isAnimate) playAnimation() else pauseAnimation() }
  }
}
