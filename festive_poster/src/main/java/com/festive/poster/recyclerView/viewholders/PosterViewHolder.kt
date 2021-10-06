package com.festive.poster.recyclerView.viewholders


import android.net.Uri
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterBinding
import com.festive.poster.databinding.ListItemPosterPackBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.models.PosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.base.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class PosterViewHolder(binding: ListItemPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.Keys?.let {
            binding.ivSvg.loadAndReplace("https://2015.filemail.com/api/file/get?filekey=ZiqN8Sn9fudJ0vCV6Hscr2WFGwTXae21Uz2wVXUYWUh9C7a6tXvJnA8KnjwW0Q",
                it
            )
        }
       // model.map?.let { binding.ivSvg.replace(it/*mapOf("IMAGE_PATH" to "image_picker.png","Beautiful Smiles" to "Hello Boost","SMILEY DENTAL CLINIC" to "Boost Clinic")*/) }


        binding.btnTapEdit.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal)
        }

        super.bind(position, item)
    }
}