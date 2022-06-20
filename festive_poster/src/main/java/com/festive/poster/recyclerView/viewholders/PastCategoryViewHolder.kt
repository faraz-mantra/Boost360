package com.festive.poster.recyclerView.viewholders

import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPastCategoryBinding
import com.festive.poster.models.promoModele.PastCategoriesModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.onboarding.nowfloats.bottomsheet.onClickItem
import okhttp3.internal.notify
import okhttp3.internal.notifyAll

class PastCategoryViewHolder(binding: ListItemPastCategoryBinding) :
    AppBaseRecyclerViewHolder<ListItemPastCategoryBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val categoryItem = item as PastCategoriesModel
        binding.tvCategoryName.text = "${categoryItem.categoryTitle} (${categoryItem.categoryCount})"

        binding.root.setOnClickListener {
            listener?.onItemClick(position, categoryItem, RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal)
        }

        super.bind(position, item)
    }
}