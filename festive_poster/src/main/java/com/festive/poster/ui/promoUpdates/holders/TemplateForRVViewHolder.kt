package com.festive.poster.ui.promoUpdates.holders

import android.content.Intent
import com.festive.poster.databinding.ListItemTemplateForRvBinding
import com.festive.poster.databinding.ListItemTemplateForVpBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.constants.PackageNames
import com.framework.webengageconstant.Post_Promotional_Update_Click
import com.framework.webengageconstant.Promotional_Update_Edit_Click
import com.framework.webengageconstant.Promotional_Update_Post_Click
import com.framework.webengageconstant.Promotional_Update_WhatsApp_Share_Click

class TemplateForRVViewHolder(binding: ListItemTemplateForRvBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForRvBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        val variant = model.variants.firstOrNull()

        SvgUtils.loadImage(model.variants.firstOrNull()?.svgUrl!!, binding.ivSvg, model.keys,model.isPurchased)
        binding.btnShare.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_WhatsApp_Share_Click)

            SvgUtils.shareUncompressedSvg(variant?.svgUrl,model,
                binding.root.context, PackageNames.WHATSAPP)
        }

        binding.tvTemplateDesc.text =model.greeting_message

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)
           // activity?.let { it1 -> PostPreviewSocialActivity.launchActivity(it1,null,model) }
            EditPostActivity.launchActivity(binding.root.context,model)

        }
        binding.btnEdit.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Edit_Click)

            EditPostActivity.launchActivity(binding.root.context,model)
        }
    }
}