package com.appservice.holder

import androidx.core.content.ContextCompat
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.ListItemPastTagsBinding
import com.appservice.model.updateBusiness.pastupdates.PastPromotionalCategoryModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.BaseApplication

class PastTagsViewHolder(binding: ListItemPastTagsBinding) :
    AppBaseRecyclerViewHolder<ListItemPastTagsBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val pastTagItem = item as PastPromotionalCategoryModel
        binding.apply {
            if (pastTagItem.isSelected){
                //chkbxTags.buttonDrawable = ContextCompat.getDrawable(FestivePosterApplication.instance, R.drawable.ic_checkbox_chked_fposter_16)
                chkbxTags.isChecked = true
                layoutWrapper.setBackgroundResource(R.drawable.rounded_rectangle_white)
                tvTagsName.setTextColor(ContextCompat.getColor(BaseApplication.instance, R.color.black_4a4a4a))
            }else{
                //chkbxTags.buttonDrawable = ContextCompat.getDrawable(FestivePosterApplication.instance, R.drawable.ic_checkbox_unchked_fposter_16)
                chkbxTags.isChecked = false
                layoutWrapper.setBackgroundResource(R.drawable.rounded_rectangle_grey)
                tvTagsName.setTextColor(ContextCompat.getColor(BaseApplication.instance, R.color.color_888888))
            }

            tvTagsName.text = pastTagItem.name
            root.setOnClickListener {
                pastTagItem.isSelected = pastTagItem.isSelected.not()
                listener?.onItemClick(position, pastTagItem, RecyclerViewActionType.PAST_TAG_CLICKED.ordinal)
            }
            binding.chkbxTags.setOnClickListener {
                pastTagItem.isSelected = pastTagItem.isSelected.not()
                listener?.onItemClick(position, pastTagItem, RecyclerViewActionType.PAST_TAG_CLICKED.ordinal)

            }

        }
        super.bind(position, item)
    }
}