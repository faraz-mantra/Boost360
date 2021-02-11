package com.inventoryorder.holders

import android.os.Build
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemOrderTypeBinding
import com.inventoryorder.databinding.ItemProductBinding
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.squareup.picasso.Picasso

class ProductItemViewHolder(binding: ItemProductBinding) : AppBaseRecyclerViewHolder<ItemProductBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? ProductItem) ?: return

        if (!data?.ImageUri.isNullOrEmpty())
            Picasso.get().load(data?.ImageUri).placeholder(R.drawable.placeholder_image).into(binding?.itemImage)

        binding?.tvProductName.text = data.Name ?: ""
        binding?.tvProductPrice.text = "${data.CurrencyCode} ${data.Price.toString()}"
    }
}