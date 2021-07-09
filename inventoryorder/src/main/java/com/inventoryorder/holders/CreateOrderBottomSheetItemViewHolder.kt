package com.inventoryorder.holders

import android.os.Build
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.BottomSheetOrderOptionBinding
import com.inventoryorder.databinding.ItemOrderTypeBinding
import com.inventoryorder.databinding.ItemProductBinding
import com.inventoryorder.model.order.ProductItem
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.squareup.picasso.Picasso

class CreateOrderBottomSheetItemViewHolder(binding: BottomSheetOrderOptionBinding) : AppBaseRecyclerViewHolder<BottomSheetOrderOptionBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = (item as? BottomSheetOptionsItem) ?: return

        binding?.optionTitle?.text = data?.title
        binding?.optionDesc?.text = data?.description
        binding?.radioBtn?.isChecked = data?.isChecked
        binding?.linearLayout?.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.ORDER_OPTION_SELECTED.ordinal)
        }
        /*binding?.radioBtn?.setOnCheckedChangeListener { p0, p1 ->
        }*/
    }
}