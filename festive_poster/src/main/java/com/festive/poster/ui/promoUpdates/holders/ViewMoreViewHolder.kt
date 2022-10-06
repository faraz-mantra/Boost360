package com.festive.poster.ui.promoUpdates.holders

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.LayoutViewMoreTemplateBinding
import com.festive.poster.databinding.ListItemSocialConnBinding
import com.festive.poster.models.ViewMoreTodayPickTemplate
import com.festive.poster.models.promoModele.SocialConnModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.WebEngageController
import com.framework.BaseApplication
import com.framework.utils.fetchString
import com.framework.webengageconstant.Promotional_Update_Social_Accounts_Connect_Click

class ViewMoreViewHolder(binding: LayoutViewMoreTemplateBinding):
    AppBaseRecyclerViewHolder<LayoutViewMoreTemplateBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {

        val model = item as ViewMoreTodayPickTemplate
        binding.btnMore.text = BaseApplication.instance.getString(
            R.string.view_placeholder_more,model.categorySize)

        binding.btnMore.setOnClickListener {
            listener?.onItemClick(position,item,
                RecyclerViewActionType.POSTER_VIEW_MORE_CLICKED.ordinal)
        }
    }
}