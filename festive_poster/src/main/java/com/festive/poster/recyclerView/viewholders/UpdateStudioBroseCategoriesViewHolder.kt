package com.festive.poster.recyclerView.viewholders

import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemCategoryBinding
import com.festive.poster.models.BrowseAll
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class UpdateStudioBrowseCategoriesViewHolder(binding: ListItemCategoryBinding) :
    AppBaseRecyclerViewHolder<ListItemCategoryBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as BrowseAll
        when(position){
            0 -> binding.categoryView.setBackgroundResource(R.drawable.card_festive_ocassions)
            1 -> binding.categoryView.setBackgroundResource(R.drawable.card_thought_of_theday)
            2 -> binding.categoryView.setBackgroundResource(R.drawable.card_discount_offers)
        }

        binding.title.text = model.title
        binding.posterCount.text = "("+model.postersCount.toString()+")"

        binding.categoryView.setOnClickListener {
            listener?.onItemClick(position, model, RecyclerViewActionType.BROWSE_ALL_CAT_CLICKED.ordinal)
        }

        super.bind(position, item)
    }


}