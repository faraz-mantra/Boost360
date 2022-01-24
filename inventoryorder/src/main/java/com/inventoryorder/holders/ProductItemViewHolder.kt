package com.inventoryorder.holders

import android.view.View
import com.framework.glide.util.glideLoad
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemProductBinding
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class ProductItemViewHolder(binding: ItemProductBinding) : AppBaseRecyclerViewHolder<ItemProductBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = (item as? ProductItem) ?: return

    if (data.ImageUri.isNullOrEmpty().not()) activity?.glideLoad(binding.itemImage, data.ImageUri?:"", R.drawable.placeholder_image_n)
    else binding.itemImage.setImageResource(R.drawable.placeholder_image_n)
    binding.tvProductName.text = data.Name ?: ""
    binding.tvProductPrice.text = "${data.getCurrencyCodeValue()} ${data.getPayablePrice()}"
    binding.layoutButtonAdd.setOnClickListener {
      listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_ITEM_ADD.ordinal)
    }
    if (item.productQuantityAdded > 0) {
      binding.layoutButtonAdd.visibility = View.GONE
      binding.layoutItemCounter.visibility = View.VISIBLE
      binding.tvItemCount.text = item.productQuantityAdded.toString()
    } else {
      binding.layoutButtonAdd.visibility = View.VISIBLE
      binding.layoutItemCounter.visibility = View.GONE
    }
    binding.tvIncrementCount.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.PRODUCT_ITEM_INCREASE_COUNT.ordinal
      )
    }
    binding.tvDecrementCount.setOnClickListener {
      listener?.onItemClick(
        adapterPosition,
        data,
        RecyclerViewActionType.PRODUCT_ITEM_DECREASE_COUNT.ordinal
      )
    }
  }
}