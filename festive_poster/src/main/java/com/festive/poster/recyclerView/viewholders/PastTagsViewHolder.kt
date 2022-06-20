package com.festive.poster.recyclerView.viewholders

import androidx.core.content.ContextCompat
import com.festive.poster.FestivePosterApplication
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPastTagsBinding
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.PastCategoriesModel
import com.festive.poster.models.promoModele.PastTagModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PastTagsViewHolder(binding: ListItemPastTagsBinding) :
    AppBaseRecyclerViewHolder<ListItemPastTagsBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val pastTagItem = item as PastTagModel
        binding.apply {
            if (pastTagItem.isSelected){
                chkbxTags.isChecked = true
                layoutWrapper.setBackgroundResource(R.drawable.rounded_rectangle_white)
                tvTagsName.setTextColor(ContextCompat.getColor(FestivePosterApplication.instance, R.color.black_4a4a4a))
            }else{
                chkbxTags.isChecked = false
                layoutWrapper.setBackgroundResource(R.drawable.rounded_rectangle_grey)
                tvTagsName.setTextColor(ContextCompat.getColor(FestivePosterApplication.instance, R.color.color_888888))
            }

            tvTagsName.text = pastTagItem.tag
            root.setOnClickListener {
                pastTagItem.isSelected = pastTagItem.isSelected.not()
                listener?.onItemClick(position, pastTagItem, RecyclerViewActionType.PAST_TAG_CLICKED.ordinal)
            }
        }
        super.bind(position, item)
    }
}