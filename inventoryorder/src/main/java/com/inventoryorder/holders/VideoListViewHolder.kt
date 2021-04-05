package com.inventoryorder.holders

import com.inventoryorder.R
import com.inventoryorder.databinding.RecyclerItemListVideosBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.ui.tutorials.model.AllTutorialsItem

class VideoListViewHolder(binding: RecyclerItemListVideosBinding) : AppBaseRecyclerViewHolder<RecyclerItemListVideosBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as? AllTutorialsItem
        binding.ctvVideoDuration.text = "4:30"
        binding.videoThumbnails.setImageResource(R.drawable.placeholder_image)
        binding.ctvVideoTitle.text = data?.videoTitle
    }
}