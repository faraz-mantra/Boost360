package com.festive.poster.ui.promoUpdates.holders

import android.content.Intent
import com.festive.poster.databinding.ListItemTemplateBinding
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity

class TemplateViewHolder(binding: ListItemTemplateBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TemplateModel
        binding.tvTemplateDesc.text = model.desc
        binding.btnEdit.setOnClickListener {
            binding.root.context.startActivity(Intent(binding.root.context, EditPostActivity::class.java))
        }
    }
}