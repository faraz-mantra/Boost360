package com.festive.poster.recyclerView.viewholders


import android.widget.Toast
import com.festive.poster.FestivePosterApplication
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPurchasedPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.WebEngageController
import com.framework.constants.PackageNames
import com.framework.utils.saveImageToSharedStorage
import com.framework.utils.shareAsImage
import com.framework.utils.toBitmap
import com.framework.webengageconstant.*

class PosterPurchasedViewHolder(binding: ListItemPurchasedPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.keys.let {
            binding.ivSvg.loadAndReplace(
                model.variants.firstOrNull()?.svgUrl,
                it
            )
        }
        binding.tvGreetingMsg.text = model.greeting_message
        binding.tvGreetingMsg.setOnClickListener {
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_GREETING_MSG_CLICKED.ordinal)
        }
        binding.btnTapEdit.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_EDIT_CLICK,event_value = HashMap())
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal)
        }
        binding.ivOther.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_OTHER,event_value = HashMap())
            binding.ivSvg.toBitmap()?.shareAsImage(text = model.greeting_message)
        }

        binding.ivDownload.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_DOWNLOAD,event_value = HashMap())
            binding.ivSvg.toBitmap()?.saveImageToSharedStorage()
            Toast.makeText(FestivePosterApplication.instance, "Image Saved To Storage", Toast.LENGTH_SHORT).show()
        }

        binding.ivWhatsapp.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_WHATSAPP,event_value = HashMap())
            binding.ivSvg.toBitmap()?.shareAsImage(PackageNames.WHATSAPP,text = model.greeting_message)
        }

        binding.ivInstagram.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_INSTAGRAM,event_value = HashMap())
            binding.ivSvg.toBitmap()?.shareAsImage(PackageNames.INSTAGRAM,text = model.greeting_message)
        }

        super.bind(position, item)
    }


}