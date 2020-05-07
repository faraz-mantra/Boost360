package com.inventoryorder.holders

import android.graphics.Paint
import com.inventoryorder.R
import com.inventoryorder.databinding.ItemBookingDetailsBinding
import com.inventoryorder.model.bookingdetails.BookingDetailsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class BookingDetailsViewHolder(binding : ItemBookingDetailsBinding): AppBaseRecyclerViewHolder<ItemBookingDetailsBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as BookingDetailsModel

        setDataResponseForOrderDetails(data)
    }

    private fun setDataResponseForOrderDetails(bookingDetailsModel: BookingDetailsModel?) {
        binding.tvDishName.text = bookingDetailsModel?.itemName?.trim()
        binding.tvDishQuantity.text = null
        binding.tvDishAmount.text = bookingDetailsModel?.itemPrice.toString()
        binding.tvActualPrice.paintFlags = binding?.tvActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        binding.tvActualPrice.text = bookingDetailsModel?.itemActualPrice
        binding.ivDishItem.setImageResource(R.drawable.ic_mutton_rogan_josh)

    }
}