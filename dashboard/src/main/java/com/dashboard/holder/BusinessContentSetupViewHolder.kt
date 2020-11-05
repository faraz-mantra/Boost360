package com.dashboard.holder

import com.dashboard.databinding.ItemBusinessContentSetupBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.model.BusinessSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessContentSetupViewHolder(binding: ItemBusinessContentSetupBinding) : AppBaseRecyclerViewHolder<ItemBusinessContentSetupBinding>(binding), RecyclerItemClickListener {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? BusinessContentSetupData ?: return
    binding.txtTitle.text = data.title
    binding.txtDes.text = data.subTitle
    if (data.type == BusinessSetupData.ActiveViewType.ONLINE_SYNC.name) {
      binding.viewImage.gone()
      binding.gifSyncOk.visible()
      binding.gifSyncOk.apply {
        data.gifIcon?.let { gifResource = it }
        play()
      }
    } else {
      binding.viewImage.visible()
      binding.gifSyncOk.gone()
      data.icon1?.let { binding.imgCircle.setImageResource(it) }
      data.icon2?.let { binding.imgIcon.setImageResource(it) }
    }
    if (data.businessData.isNullOrEmpty().not()) {
      binding.rvBusinessItemState.apply {
        val adapter1 = activity?.let { AppBaseRecyclerViewAdapter(it, data.businessData!!, this@BusinessContentSetupViewHolder) }
        adapter = adapter1
      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
  }
}
