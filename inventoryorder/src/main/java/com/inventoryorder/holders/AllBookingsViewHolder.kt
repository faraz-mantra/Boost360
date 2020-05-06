package com.inventoryorder.holders

import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBookingsAllOrderBinding
import com.inventoryorder.model.bookingdetails.AllBookingsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class AllBookingsViewHolder (binding : ItemBookingsAllOrderBinding): AppBaseRecyclerViewHolder<ItemBookingsAllOrderBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as AllBookingsModel

        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition,data, RecyclerViewActionType.ALL_BOOKING_ITEM_CLICKED.ordinal)
        }

    }


}