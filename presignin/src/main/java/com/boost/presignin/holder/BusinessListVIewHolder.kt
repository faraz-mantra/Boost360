package com.boost.presignin.holder

import android.content.res.ColorStateList
import com.boost.presignin.R
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.RecyclerItemFpInfoBinding
import com.boost.presignin.model.authToken.AuthTokenDataItem
import com.boost.presignin.model.fpList.ResultItem
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad

class BusinessListViewHolder(binding: RecyclerItemFpInfoBinding) : AppBaseRecyclerViewHolder<RecyclerItemFpInfoBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? AuthTokenDataItem ?: return
    if (data.description == null || data.description == "") binding.ctvBusinessDesc.gone() else binding.ctvBusinessDesc.visible()
    binding.ctvBusinessDesc.text = data.description
    binding.ctvBusinessName.text = data.name.toString()
    binding.ctvWebLink.text = data.rootAliasUri
    apply { activity?.glideLoad(binding.imageIcon, data.logoUrl, R.drawable.placeholder_image_n) }
    binding.customRadioButton.setOnCheckedChangeListener(null)
    if (data.isItemSelected == true) {
      binding.llRootBusinessItem.setBackgroundResource(R.drawable.bg_business_item_selected)
      binding.customRadioButton.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.orange)!!)
      binding.customRadioButton.isChecked = true
    } else {
      binding.llRootBusinessItem.setBackgroundResource(R.drawable.bg_business_item_unselected)
      binding.customRadioButton.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.greyish_brown)!!)
      binding.customRadioButton.isChecked = false
    }
    binding.root.setOnClickListener { onItemClick(position, item) }
    binding.customRadioButton.setOnCheckedChangeListener { _, _ -> onItemClick(position, item) }
  }

  private fun onItemClick(position: Int, item: BaseRecyclerViewItem) {
    listener?.onItemClick(position, item, RecyclerViewActionType.BUSINESS_LIST_ITEM_CLICK.ordinal)
  }
}