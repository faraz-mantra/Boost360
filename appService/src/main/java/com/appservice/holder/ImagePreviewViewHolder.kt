package com.appservice.holder

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemPreviewImageBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.utils.getBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.ByteArrayOutputStream
import java.util.*

class ImagePreviewViewHolder(binding: ItemPreviewImageBinding) : AppBaseRecyclerViewHolder<ItemPreviewImageBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? FileModel
    when {
      data?.pathUrl.isNullOrEmpty() -> {
        if (data?.getExt()?.toLowerCase(Locale.ROOT) == "pdf") {
          binding.image.setImageResource(R.drawable.ic_pdf_placholder)
        } else binding.image.setImageBitmap(data?.path?.getBitmap())
        binding.ctvSize.text =
          "${data?.getFileName() ?: ""}(${getImageSize(data?.path?.getBitmap())})"
      }
      data?.getExtUrl()?.toLowerCase(Locale.ROOT).equals("pdf") -> {
        binding.ctvSize.text = "${data?.getFileName()}"
        binding.image.setImageResource(R.drawable.ic_pdf_placholder)
      }
      else -> {
        activity?.let {
          binding.ctvSize.text = activity?.getString(R.string.loading_)
          Glide.with(it)
            .asBitmap().load(data?.pathUrl ?: "")
            .placeholder(R.drawable.placeholder_image_n)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Bitmap> {
              override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                return false
              }

              @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
              override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                if (resource != null) binding.ctvSize.text =
                  "${data?.getFileName() ?: ""}(${getImageSize(resource)})"
                return false
              }
            }).into(binding.image)
        }
      }
    }
    binding.cbChange.setOnClickListener {
      listener?.onItemClick(position, data, RecyclerViewActionType.IMAGE_CHANGE.ordinal)
    }
  }

  private fun getImageSize(f: Bitmap?): String {
    val stream = ByteArrayOutputStream()
    f?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val imageInByte: ByteArray = stream.toByteArray()
    val lengthbmp = imageInByte.size.toLong()
    return "${lengthbmp / 1024} Kb"
  }
}
