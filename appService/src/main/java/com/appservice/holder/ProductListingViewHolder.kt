package com.appservice.holder

import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.RecyclerItemProductListingBinding
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.glide.util.glideLoad
import com.framework.utils.removeENotationAndRoundTo

class ProductListingViewHolder(binding: RecyclerItemProductListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemProductListingBinding>(binding) {
  
  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? CatalogProduct) ?: return
    binding.labelName.text = data.Name
    binding.labelCategory.text = data.getCategoryWithBrand()
    activity?.let { binding.labelCategory.setTextColor(ContextCompat.getColor(it, if (data.category.isNullOrEmpty()) R.color.gray_light_1 else R.color.grey_dark_2)) }
    when {
      data.availableUnits == -1 -> {
        binding.ctvStock.text = activity?.getString(R.string.stock); binding.ctvStock.setTextColor(getColor(R.color.secondary_text)!!)
      }
      data.availableUnits > 0 -> {
        binding.ctvStock.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.root.context, R.drawable.ic_dot_green), null);
        binding.ctvStock.text = "${data.availableUnits} In stock"
        binding.ctvStock.setTextColor(getColor(R.color.secondary_text)!!)
      }
      else -> {
        binding.ctvStock.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(binding.root.context, R.drawable.ic_dot_red), null)
        binding.ctvStock.setTextColor(Color.RED)
        binding.ctvStock.text = activity?.getString(R.string.out_of_stock)
      }
    }
    if (data.DiscountAmount <= 0.0) binding.labelBasePrice.gone() else binding.labelBasePrice.visible()
    binding.labelPrice.text = "${data.CurrencyCode ?: "INR"} ${(data.Price - data.DiscountAmount).removeENotationAndRoundTo(1)}"
    binding.labelBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
    binding.labelBasePrice.text = "${data.CurrencyCode ?: "INR"} ${data.Price.removeENotationAndRoundTo(1)}"
    binding.labelDescription.text = "${data.Description}"
    apply { activity?.glideLoad(binding.cardThumbnail, data.TileImageUri, R.drawable.placeholder_image_n) }
    binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_ITEM_CLICK.ordinal) }
    binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_DATA_SHARE_CLICK.ordinal) }
    binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_WHATS_APP_SHARE.ordinal) }
  }
}