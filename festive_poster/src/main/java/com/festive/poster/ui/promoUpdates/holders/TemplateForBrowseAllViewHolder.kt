package com.festive.poster.ui.promoUpdates.holders

import androidx.core.view.isVisible
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemTemplateForRvBinding
import com.festive.poster.models.BrowseAllTemplate
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.webengageconstant.*


class TemplateForBrowseAllViewHolder(binding: ListItemTemplateForRvBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForRvBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as BrowseAllTemplate


        binding.isNew.isVisible = model.isFeatured

        if (model.isFavourite){
            binding.ivLove.setTintColor(getColor(R.color.colorEB5757)!!)
        }else{
            binding.ivLove.setTintColor(getColor(R.color.colorDBDBDB)!!)

        }
        SvgUtils.loadImage(model.primarySvgUrl, binding.ivSvg)
        binding.btnShare.setOnClickListener {
            WebEngageController.trackEvent(Browse_All_Promotional_Update_Social_Accounts_Connect_Click)
            listener?.onItemClick(position,item, RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal)
        }

        binding.cardLove.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal)
        }
        binding.tvTemplateDesc.text =model.primaryText

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)
           // activity?.let { it1 -> PostPreviewSocialActivity.launchActivity(it1,null,model) }
           // EditPostActivity.launchActivity(binding.root.context,model)
            listener?.onItemClick(position,item, RecyclerViewActionType.POST_CLICKED.ordinal)


        }
        binding.btnEdit.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Edit_Click)
            WebEngageController.trackEvent(Promotional_Update_Edit_Caption_Click)

            EditPostActivity.launchActivity(binding.root.context,model)
        }
        super.bind(position, item)

    }
}