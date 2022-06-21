package com.appservice.holder

import androidx.core.content.res.ResourcesCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemGstSlabBinding
import com.appservice.model.panGst.GstDetailModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem

class GstSlabViewHolder(binding: ItemGstSlabBinding) : AppBaseRecyclerViewHolder<ItemGstSlabBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val gstDetailModel = item as? GstDetailModel ?: return
    binding.radioGst.text = gstDetailModel.title
    binding.radioGst.isChecked = gstDetailModel.isSelected
    binding.radioGst.typeface = activity?.let { ResourcesCompat.getFont(it, if (gstDetailModel.isSelected) R.font.semi_bold else R.font.semi_bold) }
    binding.mainContent.setOnClickListener {
      listener?.onItemClick(position, item, RecyclerViewActionType.ON_CLICK_CATALOG_ITEM.ordinal)
    }
  }
}
