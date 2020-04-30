package com.inventoryorder.holders

import android.graphics.Paint
import com.framework.glide.GlideImageLoader
import com.inventoryorder.databinding.ItemOrderDetailsBinding
import com.inventoryorder.model.ordersdetails.ItemX
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemDetailsViewHolder(binding: ItemOrderDetailsBinding) : AppBaseRecyclerViewHolder<ItemOrderDetailsBinding>(binding) {

    var order : OrderItem? = null

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as ItemX

//        order = item as OrderItem

/*//        var string = data.itemDiscountedPrice
        var spannableString = SpannableString(data.itemDiscountedPrice)
        val strikeThroughSpan = StrikethroughSpan()
        if (adapterPosition == 0) {
            binding.tvDishAmountDiscountedPrice.paintFlags = binding.tvDishAmountDiscountedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvDishAmountDiscountedPrice.text = "₹${data.itemDiscountedPrice}"
//            activity?.let {
//                spannableString.setSpan(strikeThroughSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//            }
        } else {
            binding.tvDishAmountDiscountedPrice.invisible()
        }
        data.itemImage?.let { binding.ivDishItem.setImageResource(it) }
        binding.tvDishName.text = data.itemName
        binding.tvDishAmount.text = "₹${data.itemPrice.toString()}"
        binding.tvDishQuantity.text = data.itemQuantity*/

//        override fun bind(position: Int, item: BaseRecyclerViewItem) {
//            super.bind(position, item)
//
//            val data = item as InventoryOrderDetailsModel
//
//            if (adapterPosition == 0) {
//                binding.tvDishAmountDiscountedPrice.paintFlags = binding.tvDishAmountDiscountedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
//                binding.tvDishAmountDiscountedPrice.text = data.itemDiscountedPrice
//
//            } else {
//                binding.tvDishAmountDiscountedPrice.invisible()
//            }
//
//            data.itemImage?.let { binding.ivDishItem.setImageResource(it) }
//            binding.tvDishName.text = data.itemName
//            binding.tvDishAmount.text = data.itemPrice.toString()
//            binding.tvDishQuantity.text = data.itemQuantity
////        binding.tvDishAmountDiscountedPrice.text = data.itemDiscountedPrice
//        }


        setDataResponseForOrderDetails(order)

    }

    private fun setDataResponseForOrderDetails(order: OrderItem?) {

        binding?.tvDishName?.text = order?.Items?.get(0)?.Product?.Name
        binding?.tvDishQuantity.text = order?.Items?.get(0)?.Quantity.toString()
        binding?.tvDishAmount.text = order?.Items?.get(0)?.Product?.DiscountAmount.toString()
        binding?.tvDishAmountDiscountedPrice.paintFlags = binding.tvDishAmountDiscountedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvDishAmountDiscountedPrice.text = order?.Items?.get(0)?.Product?.DiscountAmount.toString()


        val url : String = order?.Items?.get(0)?.Product?.ImageUri.toString()

//        binding.ivDishItem.setImageResource()



    }

}
