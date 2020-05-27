package com.inventoryorder.holders

import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetPickInventoryNatureBinding
import com.inventoryorder.model.bottomsheet.PickInventoryNatureModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class PickInventoryNatureViewHolder( binding : ItemBottomSheetPickInventoryNatureBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetPickInventoryNatureBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as PickInventoryNatureModel
        setData(data)
    }

    private fun setData( data : PickInventoryNatureModel?){
        data?.setIcon()?.let { binding.inventorySelectedOrUnselectedIcon.setImageResource(it) }
        binding.tvInventoryName.text = data?.inventoryName
        binding.tvInventoryDescription.text = data?.inventoryDescription
//        data?.inventoryTypeIcon?.let { binding.ivInventoryType.setImageResource(it) }
        data?.inventoryTypeIcon?.let { binding.ivInventoryType.setImageResource(it) }
        binding?.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition,data,RecyclerViewActionType.PICK_INVENTORY_NATURE.ordinal)
        }

    }




}