package com.festive.poster.recyclerView.viewholders


import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterShareBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.festive.poster.utils.WebEngageController
import com.framework.constants.PackageNames
import com.framework.webengageconstant.*


class PosterShareViewHolder(binding: ListItemPosterShareBinding):
    AppBaseRecyclerViewHolder<ListItemPosterShareBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.keys.let {
            val url = model.variants?.firstOrNull()?.svgUrl ?: "https://siriablobstorage.blob.core.windows.net/svg-templates/61644f0c503d2a74e5b68963/DurgaPuja03.svg"
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
            SvgUtils.shareUncompressedSvg(model.variants?.firstOrNull()?.svgUrl
                ,model)
        }

        binding.ivDownload.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_DOWNLOAD,event_value = HashMap())
            listener?.onItemClick(position,item,RecyclerViewActionType.POSTER_DOWNLOAD_CLICKED.ordinal)
        }

        binding.ivWhatsapp.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_WHATSAPP,event_value = HashMap())
            SvgUtils.shareUncompressedSvg(model.variants?.firstOrNull()?.svgUrl,model, PackageNames.WHATSAPP)
        }

        binding.ivInstagram.setOnClickListener {
            WebEngageController.trackEvent(FESTIVAL_POSTER_SHARE_INSTAGRAM,event_value = HashMap())
            SvgUtils.shareUncompressedSvg(model.variants?.firstOrNull()?.svgUrl,model,PackageNames.INSTAGRAM)
        }

        super.bind(position, item)
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