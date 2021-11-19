package com.festive.poster.recyclerView.viewholders


import com.bumptech.glide.Glide
import com.festive.poster.FestivePosterApplication
import com.festive.poster.R
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPurchasedPosterPackBinding
import com.festive.poster.models.PosterPackModel
import com.festive.poster.models.PurchasedPosterPackModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem

class PosterPackPurchasedViewHolder(binding: ListItemPurchasedPosterPackBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterPackBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PurchasedPosterPackModel
        binding.tvPackSize.text = model.posterList?.size.toString()+" Posters"
        binding.tvPackTitle.text = model.tagsModel.name
        Glide.with(FestivePosterApplication.instance).load(model.tagsModel.drawableIcon).placeholder(R.drawable.placeholder_image).into(binding.ivPosterPack)

        binding.root.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_PACK_PURCHASED_CLICK.ordinal)
        }
        super.bind(position, item)
    }


}