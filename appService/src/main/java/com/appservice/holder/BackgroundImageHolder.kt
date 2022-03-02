package com.appservice.holder

import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.PaginationLoaderBinding
import com.appservice.databinding.RecyclerviewBackgroundImagesBinding
import com.appservice.model.ImageData
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.glide.util.glideLoad

class BackgroundImageHolder(binding: RecyclerviewBackgroundImagesBinding) :
        AppBaseRecyclerViewHolder<RecyclerviewBackgroundImagesBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val data = item as? ImageData
        binding.image.let {
            activity?.glideLoad(
                    it,
                    data?.url,
                    R.drawable.placeholder_image_n
            )
        }
        binding.root.setOnClickListener { listener?.onItemClick(position,item,RecyclerViewActionType.ON_CLICK_BACKGROUND_IMAGE.ordinal) }
    }
}
