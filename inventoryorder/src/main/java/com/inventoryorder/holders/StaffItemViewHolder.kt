package com.inventoryorder.holders

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.google.android.material.textview.MaterialTextView
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemProductsAddedBinding
import com.inventoryorder.databinding.StaffItemBinding
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.model.spaAppointment.bookingslot.response.Staff
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.ui.order.sheetOrder.EditCustomerInfoBottomSheetDialog
import com.squareup.picasso.Picasso

class StaffItemViewHolder(binding: StaffItemBinding) : AppBaseRecyclerViewHolder<StaffItemBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? Staff) ?: return

        if (data?.Image != null && data?.Image?.isNotEmpty()==true) {
            Picasso.get().load(item?.Image).into(binding?.imageStaff)
        }

        if (data?.Name?.equals("anybody", true)==true) {
            Picasso.get().load(R.drawable.ic_anybody).into(binding?.imageStaff)
        }

        binding?.textStaffName?.text = item?.Name
        if (data?.isSelected) {
            binding?.group?.visibility = View.VISIBLE
        } else binding?.group?.visibility = View.GONE

        binding.root.setOnClickListener { listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.STAFF_CLICKED.ordinal) }
    }
}