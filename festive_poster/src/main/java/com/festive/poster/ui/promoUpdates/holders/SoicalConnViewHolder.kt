package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.databinding.ListItemSocialConnBinding
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.WebEngageController
import com.framework.webengageconstant.Promotional_Update_Social_Accounts_Connect_Click

class SoicalConnViewHolder(binding: ListItemSocialConnBinding):
    AppBaseRecyclerViewHolder<ListItemSocialConnBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialConnModel
        binding.tvContent.text = model.content
        binding.tvConn.setOnClickListener {
            WebEngageController.trackEvent(Promotional_Update_Social_Accounts_Connect_Click)

        }
    }
}