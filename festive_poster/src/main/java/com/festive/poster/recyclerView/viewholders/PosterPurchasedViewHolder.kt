package com.festive.poster.recyclerView.viewholders


import android.widget.Toast
import com.festive.poster.FestivePosterApplication
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.ListItemPurchasedPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.constants.PackageNames
import com.framework.utils.saveImageToSharedStorage
import com.framework.utils.shareAsImage
import com.framework.utils.toBitmap

class PosterPurchasedViewHolder(binding: ListItemPurchasedPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
       /* model.keys.let {
            binding.ivSvg.loadAndReplace(
                model.variants.firstOrNull()?.svgUrl,
                it
            )
        }*/
        binding.tvGreetingMsg.text = model.greeting_message
        binding.tvGreetingMsg.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_GREETING_MSG_CLICKED.ordinal)
        }
        binding.btnTapEdit.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal)
        }
        binding.ivOther.setOnClickListener {
            binding.ivSvg.toBitmap()?.shareAsImage(text = model.greeting_message)
        }

        binding.ivDownload.setOnClickListener {
            binding.ivSvg.toBitmap()?.saveImageToSharedStorage()
            Toast.makeText(FestivePosterApplication.instance, "Image Saved To Storage", Toast.LENGTH_SHORT).show()
        }

        binding.ivWhatsapp.setOnClickListener {
            binding.ivSvg.toBitmap()?.shareAsImage(PackageNames.WHATSAPP,text = model.greeting_message)
        }

        binding.ivInstagram.setOnClickListener {
            binding.ivSvg.toBitmap()?.shareAsImage(PackageNames.INSTAGRAM,text = model.greeting_message)
        }

        super.bind(position, item)
    }


}