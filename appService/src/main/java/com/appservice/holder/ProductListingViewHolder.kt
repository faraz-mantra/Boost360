package com.appservice.holder

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

class ProductListingViewHolder(binding: RecyclerItemProductListingBinding) : AppBaseRecyclerViewHolder<RecyclerItemProductListingBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? CatalogProduct) ?: return
        binding.labelName.text = data.brandName
        binding.labelCategory.text = data.category
        when {
          data.availableUnits == -1 -> {binding.ctvStock.text="Stock"}
          data.availableUnits > 0 -> { binding.ctvStock.setCompoundDrawables(null,null,ContextCompat.getDrawable(binding.root.context,R.drawable.ic_dot_green),null);
              binding.ctvStock.text="${data.availableUnits} In stock"}
          else -> {
              binding.ctvStock.setCompoundDrawables(null,null,ContextCompat.getDrawable(binding.root.context,R.drawable.ic_dot_red),null)
              binding.ctvStock.text = "Out of stock" }
        }
        binding.ctvStock.text = "${data.availableUnits ?: 0}"
        if (data.Price ?: 0.0 <= data.DiscountAmount ?: 0.0) binding.labelBasePrice.gone() else binding.labelBasePrice.visible()
        binding.labelPrice.text = "${data.CurrencyCode ?: "INR"} ${data.DiscountAmount}"
        binding.labelBasePrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        binding.labelBasePrice.text = "${data.CurrencyCode ?: "INR"} ${data.Price}"
        binding.labelDescription.text = "${data.Description}"
        apply { activity?.glideLoad(binding.cardThumbnail, data.TileImageUri, R.drawable.placeholder_image_n) }
        binding.root.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_ITEM_CLICK.ordinal) }
        binding.shareData.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_DATA_SHARE_CLICK.ordinal) }
        binding.shareWhatsapp.setOnClickListener { listener?.onItemClick(position, data, RecyclerViewActionType.PRODUCT_WHATS_APP_SHARE.ordinal) }
    }
}