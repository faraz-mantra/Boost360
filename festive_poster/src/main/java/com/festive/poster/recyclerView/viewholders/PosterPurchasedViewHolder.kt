package com.festive.poster.recyclerView.viewholders


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.withContext


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
            getUncompressedSvg(model.variants.firstOrNull()?.svgUrl,model,binding.root.context, "")
        }

        binding.ivDownload.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_DOWNLOAD,event_value = HashMap())
            getUncompressedSvg(model.variants.firstOrNull()?.svgUrl,model,binding.root.context)
            Toast.makeText(FestivePosterApplication.instance, "Image Saved To Storage", Toast.LENGTH_SHORT).show()
        }

        binding.ivWhatsapp.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_WHATSAPP,event_value = HashMap())
            getUncompressedSvg(model.variants.firstOrNull()?.svgUrl,model,binding.root.context, PackageNames.WHATSAPP)
        }

        binding.ivInstagram.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_INSTAGRAM,event_value = HashMap())
            getUncompressedSvg(model.variants.firstOrNull()?.svgUrl,model,binding.root.context, PackageNames.INSTAGRAM)
        }

        super.bind(position, item)
    }

    private fun getUncompressedSvg(
        url: String?,
        model: PosterModel,
        context: Context,
        packageName:String?=null
    ) {
        url?.let {
            CoroutineScope(Dispatchers.IO).launch {
                var svgString = SvgRenderCacheUtil.instance.retrieveFromCache(url)
                if (svgString == null || svgString.isEmpty()) {
                    svgString = SvgUtils.getSvgAsAString(url)
                    svgString?.let { SvgRenderCacheUtil.instance.saveToCache(url, it) }
                }
                if (svgString != null && !svgString.isEmpty()) {
                    svgString = SvgRenderCacheUtil.instance.replace(svgString, model.keys, context)
                    val svg = SVG.getFromString(svgString)
                    svg.renderDPI = getResources()?.displayMetrics?.densityDpi?.toFloat() ?: 480.0f
                    svg.documentWidth = svg.documentWidth*4
                    svg.documentHeight = svg.documentHeight*4
                    val b = Bitmap.createBitmap(
                        svg.documentWidth.toInt(),
                        svg.documentHeight.toInt(), Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(b)
                    svg.renderToCanvas(canvas)
                    withContext(Dispatchers.Main) {
                        when (packageName) {
                            PackageNames.INSTAGRAM -> b.shareAsImage(
                                PackageNames.INSTAGRAM,
                                text = model.greeting_message
                            )
                            PackageNames.WHATSAPP -> b.shareAsImage(
                                PackageNames.WHATSAPP,
                                text = model.greeting_message
                            )
                            "" -> b.shareAsImage(text = model.greeting_message)
                            else -> b.saveImageToSharedStorage()
                        }
                    }
                }
            }

        }
    }

//    fun getBitmap(imageView:ImageView, url: String?, model: PosterModel, context:Context, packageName:String?=null) {
//        url?.let {
//            CoroutineScope(Dispatchers.IO).launch {
//                var svgString = SvgRenderCacheUtil.instance.retrieveFromCache(url)
//                if (svgString == null || svgString.isEmpty()) {
//                    svgString = SvgUtils.getSvgAsAString(url)
//                    svgString?.let { SvgRenderCacheUtil.instance.saveToCache(url, it) }
//                }
//                if (svgString != null && !svgString.isEmpty()) {
//                    svgString = SvgRenderCacheUtil.instance.replace(svgString, model.keys, context)
//                    // val op = RenderOptions().preserveAspectRatio(PreserveAspectRatio.FULLSCREEN)
//                    var drawable = PictureDrawable(SVG.getFromString(svgString).renderToPicture())
//                    Looper.getMainLooper().run {
//
//                        Log.d("SvgLoader", "setSvg() called ${Thread.currentThread()}")
//                        Handler(Looper.getMainLooper()).post {
//                            imageView.let {
//                                Log.d("SvgLoader", "setSvg() called ${Thread.currentThread()}")
//
//                                it.setImageDrawable(drawable)
//                                it.toBitmap()?.shareAsImage(PackageNames.WHATSAPP,text = model.greeting_message)
//                                it.setImageDrawable(null)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

}