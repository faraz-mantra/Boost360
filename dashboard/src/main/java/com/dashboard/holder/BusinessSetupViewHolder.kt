package com.dashboard.holder

import com.dashboard.databinding.ItemBusinessManagementBinding
import com.dashboard.model.BusinessSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessSetupViewHolder(binding: ItemBusinessManagementBinding) : AppBaseRecyclerViewHolder<ItemBusinessManagementBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? BusinessSetupData ?: return
    binding.txtTitle.text = data.title
    binding.txtDes.text = data.subTitle
    if (data.type == BusinessSetupData.ActiveViewType.ONLINE_SYNC.name) {
      binding.viewBtn.gone()
      binding.viewImage.gone()
      binding.gifSyncOk.visible()
      binding.gifSyncOk.apply {
        data.gifIcon?.let { gifResource = it }
        play()
      }

    } else {
      binding.viewBtn.visible()
      binding.viewImage.visible()
      binding.gifSyncOk.gone()
      binding.btnTitle.text = data.btnTitle
      binding.imgArrowGif.apply {
        data.btnGifIcon?.let { gifResource = it }
        play()
      }
      data.icon1?.let { binding.imgCircle.setImageResource(it) }
      data.icon2?.let { binding.imgIcon.setImageResource(it) }
    }
  }
}
