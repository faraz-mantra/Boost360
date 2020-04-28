package com.inventoryorder.holders

import android.graphics.Paint
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StrikethroughSpan
import com.framework.extensions.invisible
import com.inventoryorder.databinding.ItemOrderDetailsBinding
import com.inventoryorder.model.InventoryOrderDetailsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class OrderItemDetailsViewHolder(binding: ItemOrderDetailsBinding) : AppBaseRecyclerViewHolder<ItemOrderDetailsBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as InventoryOrderDetailsModel

//        var string = data.itemDiscountedPrice
        var spannableString = SpannableString(data.itemDiscountedPrice)

        val strikeThroughSpan = StrikethroughSpan()

        if (adapterPosition == 0) {
            binding.tvDishAmountDiscountedPrice.paintFlags = binding.tvDishAmountDiscountedPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvDishAmountDiscountedPrice.text = data.itemDiscountedPrice

//            activity?.let {
//                spannableString.setSpan(strikeThroughSpan, 0, spannableString.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
//            }
        }else{
            binding.tvDishAmountDiscountedPrice.invisible()
        }

        data.itemImage?.let { binding.ivDishItem.setImageResource(it) }
        binding.tvDishName.text = data.itemName
        binding.tvDishAmount.text = data.itemPrice.toString()
        binding.tvDishQuantity.text = data.itemQuantity
//        binding.tvDishAmountDiscountedPrice.text = data.itemDiscountedPrice



    }

}