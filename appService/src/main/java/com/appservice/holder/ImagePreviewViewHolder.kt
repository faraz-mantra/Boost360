package com.appservice.holder

import android.graphics.Bitmap
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemPreviewImageBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.utils.getBitmap
import com.framework.glide.util.glideLoad
import java.io.ByteArrayOutputStream
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
    binding.ctvSize.text = getImageSize(data?.path?.getBitmap())
    binding.cbChange.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.IMAGE_CHANGE.ordinal) }
  }

  private fun getImageSize(f: Bitmap?): String {
    val stream = ByteArrayOutputStream()
    f?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val imageInByte: ByteArray = stream.toByteArray()
    val lengthbmp = imageInByte.size.toLong()
    return "${lengthbmp / 1024} Kb"
  }
}
