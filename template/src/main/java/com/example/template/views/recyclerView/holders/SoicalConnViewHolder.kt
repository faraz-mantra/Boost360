package com.example.template.views.recyclerView.holders

import com.example.template.databinding.ListItemSocialConnBinding
import com.example.template.recyclerView.AppBaseRecyclerViewHolder
import com.example.template.recyclerView.BaseRecyclerViewItem
import com.example.template.databinding.ListItemTemplateBinding
import com.example.template.models.SocialConnModel
import com.example.template.models.TemplateModel

class SoicalConnViewHolder(binding: ListItemSocialConnBinding):
    AppBaseRecyclerViewHolder<ListItemSocialConnBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialConnModel
        binding.tvContent.text = model.content
    }
}