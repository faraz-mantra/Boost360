package dev.patrickgold.florisboard.customization.viewholder

import android.graphics.Paint
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.views.customViews.CustomTextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.databinding.AdapterItemProductNewBinding

class ProductViewHolder(binding: AdapterItemProductNewBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemProductNewBinding>(binding) {

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val product = item as Product
        // bind views with data
        Glide.with(binding.imageView).load(product.imageUri).placeholder(R.drawable.placeholder_image_n).into(binding.imageView)
        if (product.price?.toDouble() ?: 0.0 <= 0) {
            binding.tvPrice.gone()
        } else {
            binding.tvPrice.visible()
        }
        if (product.discountAmount?.toDouble() ?: 0.0 <= 0) {
            binding.tvDiscount.gone()
        } else {
            binding.tvDiscount.visible()
        }
        if (product.shipmentDuration ?: 0 <= 0) {
            binding.ctvDuration.gone()
        } else {
            binding.ctvDuration.visible()
        }
        binding.ctvDuration.text = "${product.shipmentDuration} min"
        binding.tvOffPercent.text = product.getProductOffPrice()
        binding.tvName.text = product.name
        binding.tvPrice.text = product.getProductPrice()
        binding.tvDiscount.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvDiscount.text = product.getProductDiscountPrice()
        binding.tvDescription.text = product.description
        binding.btnShare.setOnClickListener {
            listener?.onItemClick(position, product)
        }
    }
}