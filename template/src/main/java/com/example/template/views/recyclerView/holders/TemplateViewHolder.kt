package com.example.template.views.recyclerView.holders

import android.content.Intent
import com.example.template.recyclerView.AppBaseRecyclerViewHolder
import com.example.template.recyclerView.BaseRecyclerViewItem
import com.example.template.databinding.ListItemTemplateBinding
import com.example.template.models.TemplateModel
import com.example.template.views.edit_post.EditPostActivity

class TemplateViewHolder(binding: ListItemTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TemplateModel
        binding.tvTemplateDesc.text = model.desc
        binding.btnEdit.setOnClickListener {
            binding.root.context.startActivity(Intent(binding.root.context,EditPostActivity::class.java))
        }
    }
}