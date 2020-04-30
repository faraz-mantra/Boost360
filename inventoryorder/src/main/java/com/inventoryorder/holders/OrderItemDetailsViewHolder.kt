package com.inventoryorder.holders

import com.inventoryorder.databinding.ItemOrderDetailsBinding
import com.inventoryorder.model.ordersdetails.ItemX
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemDetailsViewHolder(binding: ItemOrderDetailsBinding) : AppBaseRecyclerViewHolder<ItemOrderDetailsBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as ItemX

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
    }

}
