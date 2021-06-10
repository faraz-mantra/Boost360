package dev.patrickgold.florisboard.customization.viewholder

import com.framework.extensions.gone
import com.framework.extensions.visible
import com.squareup.picasso.Picasso
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.databinding.AdapterItemPhotosBinding

class PhotoViewHolder(binding: AdapterItemPhotosBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemPhotosBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val photo = item as Photo
    Picasso.get().load(photo.imageUri).placeholder(R.drawable.placeholder_ic_image_padded).fit().centerCrop().into(binding.imageView)
    if (photo.selected) {
      binding.overlay.visible()
      binding.ivChecked.visible()
    } else {
      binding.overlay.gone()
      binding.ivChecked.gone()
    }
    binding.root.setOnClickListener {
      photo.selected = !photo.selected
      listener?.onItemClick(position, photo)
    }
  }
}