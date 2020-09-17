package com.appservice.holder

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemPreviewImageBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.utils.getBitmap
import com.framework.glide.util.glideLoad
import java.util.*

class ImagePreviewViewHolder(binding: ItemPreviewImageBinding) : AppBaseRecyclerViewHolder<ItemPreviewImageBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? FileModel
    if (data?.pathUrl.isNullOrEmpty()) {
      if (data?.getExt()?.toLowerCase(Locale.ROOT) == "pdf") {
        binding.image.setImageResource(R.drawable.ic_pdf_placholder)
      } else binding.image.setImageBitmap(data?.path?.getBitmap())
    } else {
      if (data?.getExtUrl()?.toLowerCase(Locale.ROOT) == "jpg" || data?.getExtUrl()?.toLowerCase(Locale.ROOT) == "png") {
        data.pathUrl?.let { activity?.glideLoad(binding.image, it, R.drawable.placeholder_image) }
      } else binding.image.setImageResource(R.drawable.ic_pdf_placholder)
    }
    binding.crossIcon.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.IMAGE_CLEAR_CLICK.ordinal) }
  }
}
