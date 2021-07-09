package com.appservice.holder

import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ItemExperienceDetailsBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.model.staffModel.ExperienceModel

class StaffExperienceViewHolder(binding: ItemExperienceDetailsBinding) : AppBaseRecyclerViewHolder<ItemExperienceDetailsBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as ExperienceModel
        binding.crbTitle.text = "${data.title}"
        binding.crbTitle.isChecked = data.isSelected
        binding.crbTitle.setOnClickListener {
            data.isSelected=true
            listener?.onItemClick(position, data, RecyclerViewActionType.STAFF_EXPERIENCE_CLICK.ordinal)
        }
    }
}
