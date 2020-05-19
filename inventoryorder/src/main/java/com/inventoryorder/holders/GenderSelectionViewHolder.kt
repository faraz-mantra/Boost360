package com.inventoryorder.holders

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.ItemBottomSheetSelectGenderBinding
import com.inventoryorder.model.bottomsheet.GenderSelectionModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem

class GenderSelectionViewHolder (binding : ItemBottomSheetSelectGenderBinding): AppBaseRecyclerViewHolder<ItemBottomSheetSelectGenderBinding> (binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        val data = item as GenderSelectionModel
        setData(data)
    }

    private fun setData(data: GenderSelectionModel) {
        binding.ivGenderSelected.setImageResource(R.drawable.ic_option_unselected)
        binding.tvSelectGender.text = data.gender
        binding.mainView.setOnClickListener {
            listener?.onItemClick(adapterPosition, data, RecyclerViewActionType.CHOOSE_PURPOSE_ITEM.ordinal)
        }
    }

}