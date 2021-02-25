package com.inventoryorder.holders

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemTimeSlotAppointmentBinding
import com.inventoryorder.model.spaAppointment.bookingslot.response.Slots
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class SlotsItemViewHolder(binding: ItemTimeSlotAppointmentBinding) : AppBaseRecyclerViewHolder<ItemTimeSlotAppointmentBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? Slots) ?: return

        val splitStr: ArrayList<String> = data?.StartTime?.split(" ")?.toCollection(ArrayList())!!

        binding?.textTime?.text = splitStr[0]
        binding?.textAmPm?.text = splitStr[1]

        if (data?.IsAvailable == true) {
            if (data?.isSelected) {
                binding?.linearRoot?.setBackgroundResource(R.drawable.bg_rect_blue)
            } else {
                binding?.linearRoot?.setBackgroundResource(R.drawable.bg_rect_edit_txt)
            }
        } else {
            binding?.linearRoot?.setBackgroundResource(R.drawable.bg_rect_slotinactive)
            binding?.textTime?.setTextColor(getColor(R.color.light_grey_2)!!)
            binding?.textAmPm?.setTextColor(getColor(R.color.light_grey_2)!!)
        }

        binding?.linearRoot?.setOnClickListener {
            if (data?.IsAvailable == true) {
                listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.TIME_SLOT_CLICKED.ordinal)
            }
        }
    }
}