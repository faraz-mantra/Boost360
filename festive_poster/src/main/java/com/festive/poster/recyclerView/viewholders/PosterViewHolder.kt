package com.festive.poster.recyclerView.viewholders


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Picture
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.caverock.androidsvg.PreserveAspectRatio
import com.caverock.androidsvg.RenderOptions
import com.caverock.androidsvg.SVG
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ListItemPosterBinding
import com.festive.poster.models.PosterModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.utils.SvgUtils
import com.framework.glide.customsvgloader.PosterKeyModel
import com.framework.glide.customsvgloader.SvgRenderCacheUtil
import com.framework.utils.shareAsImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method


class PosterViewHolder(binding: ListItemPosterBinding) :
    AppBaseRecyclerViewHolder<ListItemPosterBinding>(binding) {


    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as PosterModel
        model.keys.let {
            val url =
                model.variants.firstOrNull()?.svgUrl
                    ?: "https://siriablobstorage.blob.core.windows.net/svg-templates/61644f0c503d2a74e5b68963/DurgaPuja03.svg"
//            val url ="https://siriablobstorage.blob.core.windows.net/svg-templates/61644f0c503d2a74e5b68963/DurgaPuja03.svg" // 63KB
//            val url ="https://siriablobstorage.blob.core.windows.net/svg-templates/61644f3a503d2a74e5b68966/Dussehra01.svg" // 500KB
            SvgUtils.loadImage(url, binding.ivSvg, model.keys)
            Log.d("PosterViewHolder", "bind() called ${model.keys.get(0)}")
        }

//        Glide.with(binding.btnTapEdit.context).load(model.variants.firstOrNull()?.svgUrl?:"").
        // model.map?.let { binding.ivSvg.replace(it/*mapOf("IMAGE_PATH" to "image_picker.png","Beautiful Smiles" to "Hello Boost","SMILEY DENTAL CLINIC" to "Boost Clinic")*/) }


        binding.btnTapEdit.setOnClickListener {
//           val bmp = loadBitmapFromView(binding.ivSvg)
//            if (bmp != null) {
//                shareImage(bmp)
//            }

            listener?.onItemClick(
                position,
                item,
                RecyclerViewActionType.POSTER_TAP_TO_EDIT_CLICK.ordinal
            )
        }

        super.bind(position, item)
    }


}