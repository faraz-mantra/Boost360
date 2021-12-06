package com.festive.poster.ui.promoUpdates.holders

import android.content.Intent
import com.festive.poster.databinding.ListItemTemplateForRvBinding
import com.festive.poster.databinding.ListItemTemplateForVpBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils

class TemplateForRVViewHolder(binding: ListItemTemplateForRvBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForRvBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel

        SvgUtils.loadImage(model.variants.firstOrNull()?.svgUrl!!, binding.ivSvg, model.keys,model.isPurchased)


        binding.btnEdit.setOnClickListener {
            binding.root.context.startActivity(Intent(binding.root.context, EditPostActivity::class.java))
        }
    }
}