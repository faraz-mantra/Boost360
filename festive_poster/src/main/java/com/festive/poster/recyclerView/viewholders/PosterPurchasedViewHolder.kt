package com.festive.poster.recyclerView.viewholders


import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPurchasedPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.constants.PackageNames
import com.framework.utils.shareAsImage
import com.framework.utils.toBitmap

class PosterPurchasedViewHolder(binding: ListItemPurchasedPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.Keys.let {
            binding.ivSvg.loadAndReplace(
                model.Variants.firstOrNull()?.Link,
                it
            )
        }
        binding.btnTapEdit.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal)
        }
        binding.ivOther.setOnClickListener {
            binding.ivSvg.toBitmap()?.shareAsImage()
        }

        super.bind(position, item)
    }


}