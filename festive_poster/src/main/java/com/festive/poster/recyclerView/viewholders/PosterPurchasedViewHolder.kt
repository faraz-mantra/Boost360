package com.festive.poster.recyclerView.viewholders


import android.content.Context
import android.graphics.drawable.PictureDrawable
import android.widget.Toast
import com.caverock.androidsvg.SVG
import com.festive.poster.FestivePosterApplication
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPurchasedPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.constants.PackageNames
import com.framework.glide.customsvgloader.PosterKeyModel
import com.framework.glide.customsvgloader.SvgRenderCacheUtil
import com.framework.utils.saveImageToSharedStorage
import com.framework.utils.shareAsImage
import com.framework.utils.toBitmap
import com.framework.webengageconstant.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.Canvas

import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.caverock.androidsvg.PreserveAspectRatio
import com.caverock.androidsvg.RenderOptions


class PosterPurchasedViewHolder(binding: ListItemPurchasedPosterBinding):
    AppBaseRecyclerViewHolder<ListItemPurchasedPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.keys.let {
            val url = model.variants.firstOrNull()?.svgUrl ?: "https://siriablobstorage.blob.core.windows.net/svg-templates/61644f0c503d2a74e5b68963/DurgaPuja03.svg"
//            binding.ivSvg.loadAndReplace(
//                model.variants.firstOrNull()?.svgUrl,
//                it
//            )
            SvgUtils.loadImage(url, binding.ivSvgPurchased, model.keys)
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
            binding.ivSvgPurchased.toBitmap()?.shareAsImage(text = model.greeting_message)
        }

        binding.ivDownload.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_DOWNLOAD,event_value = HashMap())
            binding.ivSvgPurchased.toBitmap()?.saveImageToSharedStorage()
            Toast.makeText(FestivePosterApplication.instance, "Image Saved To Storage", Toast.LENGTH_SHORT).show()
        }

        binding.ivWhatsapp.setOnClickListener {
            getUncompressedSvg(binding.ivSvgPurchased,model.variants.firstOrNull()?.svgUrl,model.keys,binding.root.context)
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_WHATSAPP,event_value = HashMap())
           // binding.ivSvgPurchased.toBitmap()?.shareAsImage(PackageNames.WHATSAPP,text = model.greeting_message)
        }

        binding.ivInstagram.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_INSTAGRAM,event_value = HashMap())
            binding.ivSvgPurchased.toBitmap()?.shareAsImage(PackageNames.INSTAGRAM,text = model.greeting_message)
        }

        super.bind(position, item)
    }

    fun getUncompressedSvg(imageView:ImageView,url: String?, keys: List<PosterKeyModel>,context:Context){
        url?.let {
            CoroutineScope(Dispatchers.IO).launch {
                var svgString = SvgRenderCacheUtil.instance.retrieveFromCache(url)
                if (svgString == null || svgString.isEmpty()) {
                    svgString = SvgUtils.getSvgAsAString(url)
                    svgString?.let { SvgRenderCacheUtil.instance.saveToCache(url, it) }
                }
                if (svgString != null && !svgString.isEmpty()) {
                    svgString = SvgRenderCacheUtil.instance.replace(svgString, keys, context)
                   // val op = RenderOptions().preserveAspectRatio(PreserveAspectRatio.FULLSCREEN)
                    PictureDrawable(SVG.getFromString(svgString).renderToPicture()).toBitmap().shareAsImage()

                }
            }
        }


    }

}