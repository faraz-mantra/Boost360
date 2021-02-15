package com.inventoryorder.holders

import com.inventoryorder.R
import com.inventoryorder.databinding.ItemProductsAddedBinding
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.squareup.picasso.Picasso

class ProductSelectedItemViewHolder(binding: ItemProductsAddedBinding) : AppBaseRecyclerViewHolder<ItemProductsAddedBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? ItemsItem) ?: return

        if (!data?.productDetails?.imageUri.isNullOrEmpty())
            Picasso.get().load(data?.productDetails?.imageUri).placeholder(R.drawable.placeholder_image).into(binding?.itemImage)

        binding?.tvProductName.text = data?.productDetails?.name ?: ""
        binding?.tvProductPrice.text = "${data?.productDetails?.currencyCode} ${data?.productDetails?.price.toString()}"
        binding?.tvProductQuantity?.text = " X ${data?.quantity}"

        binding?.tvTotalItemPrice?.text = "${data?.productDetails?.currencyCode ?: "INR"} ${data?.productDetails?.price?.times(data?.quantity)}"
    }
}