package com.inventoryorder.holders

import com.inventoryorder.databinding.ItemBottomSheetChoosePurposeBinding
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class ChoosePurposeViewHolder(binding : ItemBottomSheetChoosePurposeBinding) : AppBaseRecyclerViewHolder<ItemBottomSheetChoosePurposeBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as ChoosePurposeModel
        setDataForChoosePurpose(data)
    }

   private fun setDataForChoosePurpose(model : ChoosePurposeModel){
        model.getIcon()?.let { binding.ivOptionSelected.setImageResource(it) }
        binding.tvOptionSelected.text = model.choosePurposeSelectedName

    }

}