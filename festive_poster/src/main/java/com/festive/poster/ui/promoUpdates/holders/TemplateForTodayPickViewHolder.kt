package com.festive.poster.ui.promoUpdates.holders

import android.content.Intent
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemTemplateForVpBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.TodayPickTemplate
import com.festive.poster.models.promoModele.TemplateModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity
import com.festive.poster.ui.promoUpdates.edit_post.EditPostActivity
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.isPromoWidgetActive
import com.framework.constants.PackageNames
import com.framework.webengageconstant.Promotional_Update_Edit_Click
import com.framework.webengageconstant.Promotional_Update_Post_Click
import com.framework.webengageconstant.Promotional_Update_WhatsApp_Share_Click

class TemplateForTodayPickViewHolder(binding: ListItemTemplateForVpBinding):
    AppBaseRecyclerViewHolder<ListItemTemplateForVpBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as TodayPickTemplate
        binding.btnShare.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_WhatsApp_Share_Click)

            listener?.onItemClick(position,item,RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal)


        }

        if (model.isFavourite){
            binding.ivLove.setTintColor(getColor(R.color.colorEB5757)!!)
        }else{
            binding.ivLove.setTintColor(getColor(R.color.colorDBDBDB)!!)

        }
        binding.cardLove.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_LOVE_CLICKED.ordinal)
        }

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)

        }
        binding.tvTemplateDesc.text =model.primaryText

        binding.btnPost.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Post_Click)
          //  activity?.let { it1 -> PostPreviewSocialActivity.launchActivity(it1,null,model) }
          //  EditPostActivity.launchActivity(binding.root.context,model)
            listener?.onItemClick(position,item, RecyclerViewActionType.POST_CLICKED.ordinal)

        }
        SvgUtils.loadImage(model.primarySvgUrl, binding.ivSvg)
        binding.btnEdit.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Edit_Click)

            EditPostActivity.launchActivity(binding.root.context,model)
        }
        super.bind(position, item)

    }
}