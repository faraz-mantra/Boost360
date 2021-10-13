package com.festive.poster.recyclerView.viewholders


import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PosterViewHolder(binding: ListItemPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.keys.let {
            binding.ivSvg.loadAndReplace(
                model.variants.firstOrNull()?.svgUrl,
                it
            )
        }
        // model.map?.let { binding.ivSvg.replace(it/*mapOf("IMAGE_PATH" to "image_picker.png","Beautiful Smiles" to "Hello Boost","SMILEY DENTAL CLINIC" to "Boost Clinic")*/) }


        binding.btnTapEdit.setOnClickListener {
           /* val bmp = loadBitmapFromView(binding.ivSvg)
            if (bmp != null) {
                shareImage(bmp)
            }*/
            listener?.onItemClick(
                position,
                item,
                RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal
            )
        }

        super.bind(position, item)
    }
}
