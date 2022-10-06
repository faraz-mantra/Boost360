package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.LayoutViewMoreTemplateBinding
import com.festive.poster.databinding.ListItemSocialConnBinding
import com.festive.poster.databinding.ListItemUpdateStudioIntroBinding
import com.festive.poster.models.IntroUpdateStudioItem
import com.festive.poster.models.ViewMoreTodayPickTemplate
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.WebEngageController
import com.framework.BaseApplication
import com.framework.utils.fetchString
import com.framework.webengageconstant.Promotional_Update_Social_Accounts_Connect_Click

class IntroScreenUpdateStudioViewHolder(binding: ListItemUpdateStudioIntroBinding):
    AppBaseRecyclerViewHolder<ListItemUpdateStudioIntroBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {

        val model = item as IntroUpdateStudioItem
        binding.tvDesc.text  = model.desc
        binding.tvTitle.text =model.title
        binding.ivIcon.setImageResource(model.icon)
    }
}