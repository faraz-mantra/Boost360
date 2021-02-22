package com.inventoryorder.holders

import android.os.Build
import android.view.View
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

        binding?.layoutButtonAdd?.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_ITEM_ADD.ordinal)
        }

        if (item.productQuantityAdded > 0) {
            binding?.layoutButtonAdd?.visibility = View.GONE
            binding?.layoutItemCounter?.visibility = View.VISIBLE
            binding?.tvItemCount?.text = item.productQuantityAdded.toString()
        } else {
            binding?.layoutButtonAdd?.visibility = View.VISIBLE
            binding?.layoutItemCounter?.visibility = View.GONE
        }

        binding?.tvIncrementCount.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_ITEM_INCREASE_COUNT.ordinal)
        }

        binding?.tvDecrementCount.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.PRODUCT_ITEM_DECREASE_COUNT.ordinal)
        }
    }
}