package com.example.template.views.recyclerView.holders

import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.example.template.databinding.ListItemTemplateBinding
import com.example.template.models.TemplateModel

class TemplateViewHolder(binding: ListItemTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TemplateModel
        binding.tvTemplateDesc.text = model.desc
    }
}