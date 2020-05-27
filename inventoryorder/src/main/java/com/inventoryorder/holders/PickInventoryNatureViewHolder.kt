package com.inventoryorder.holders

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
        binding.tvInventoryName.text = data?.inventoryName
        binding.tvInventoryDescription.text = data?.inventoryDescription
        data?.inventoryTypeIcon?.let { binding.ivInventoryType.setImageResource(it) }
        data?.inventoryTypeSelectedIcon?.let { binding.inventorySelectedOrUnselectedIcon.setImageResource(it) }
    }




}