package com.appservice.holder

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemUpdatesListBinding
import com.appservice.model.updateBusiness.UpdateFloat
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.utils.FileUtils
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.setNoDoubleClickListener

const val BASE_IMAGE_URL = "https://content.withfloats.com"

class UpdateBusinessViewHolder(binding: ItemUpdatesListBinding) :
  AppBaseRecyclerViewHolder<ItemUpdatesListBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? UpdateFloat ?: return
    binding.imageCard.visible()
    if (data.imageUri?.contains("deal.png") == true) {
      binding.imageCard.gone()
    } else if (data.imageUri?.contains("BizImages") == true) {
      val baseName = BASE_IMAGE_URL + data.imageUri
      activity?.glideLoad(binding.imageCard, baseName, R.drawable.placeholder_image_n)
    } else if (data.imageUri?.contains("/storage/emulated") == true || data.imageUri?.contains("/mnt/sdcard") == true) {
      activity?.let { FileUtils(it).getBitmap(data.imageUri, 720) }
    } else if (data.imageUri.isNullOrEmpty().not()) {
      activity?.glideLoad(binding.imageCard, data.imageUri ?: "", R.drawable.placeholder_image_n)
    } else binding.imageCard.gone()

    binding.title.text = data.message
    binding.dateTxt.text = data.getDateValue()
    binding.shareMore.setNoDoubleClickListener({
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.UPDATE_OTHER_SHARE.ordinal
      )
    })
    binding.shareWhatsapp.setNoDoubleClickListener({
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.UPDATE_WHATS_APP_SHARE.ordinal
      )
    })
    binding.shareFp.setNoDoubleClickListener({
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.UPDATE_FP_APP_SHARE.ordinal
      )
    })
    binding.root.setNoDoubleClickListener({
      listener?.onItemClick(
        position,
        item,
        RecyclerViewActionType.UPDATE_BUSINESS_CLICK.ordinal
      )
    })
  }
}
