package dev.patrickgold.florisboard.customization.viewholder

import android.graphics.Paint
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.databinding.AdapterItemProductNewBinding

class ProductViewHolder(binding: AdapterItemProductNewBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemProductNewBinding>(binding) {

  override fun bindTo(position: Int, item: BaseRecyclerItem?) {
    val product = item as? Product ?: return
    // bind views with data
    Glide.with(binding.imageView).load(product.imageUri).placeholder(R.drawable.placeholder_image_n).into(binding.imageView)
    binding.tvName.text = product.name
    if (product.getProductDiscountedPrice().isNotEmpty()) {
      binding.tvPrice.text = product.getProductDiscountedPrice()
    } else {
      binding.tvPrice.text = product.getProductPrice()
    }
    if (product.discountAmount?.toDoubleOrNull() ?: 0.0 != 0.0) {
      binding.tvDiscount.visible()
      binding.tvDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
      binding.tvDiscount.text = product.getProductPrice()
      binding.tvOffPercent.visible()
      binding.tvOffPercent.text = product.getProductOffPrice()
    } else {
      binding.tvDiscount.gone()
      binding.tvOffPercent.gone()
    }

    if (product.shipmentDuration ?: 0 <= 0) {
      binding.ctvDuration.gone()
    } else {
      binding.ctvDuration.visible()
      binding.ctvDuration.text = "${product.shipmentDuration} min"
    }

    binding.tvDescription.text = product.description
    binding.btnShare.setOnClickListener {
      listener?.onItemClick(position, product)
    }
  }
}