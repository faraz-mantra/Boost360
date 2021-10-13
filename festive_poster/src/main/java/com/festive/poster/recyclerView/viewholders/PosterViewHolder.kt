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
       /* model.keys.let {
            binding.ivSvg.loadAndReplace(
                model.variants.firstOrNull()?.svgUrl,
                it
            )
        }*/
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
   /* private fun shareImage(bitmap: Bitmap) {
        val imagesFolder = File(FestivePosterApplication.instance.getExternalFilesDir(null), "shared_images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "svg-conv.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(FestivePosterApplication.instance, "${FestivePosterApplication.instance.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.type = "image/png"
            FestivePosterApplication.instance.startActivity(intent)
        } catch (e: IOException) {
            Log.d("IOException: " , e.message.toString())
        }
    }

    fun loadBitmapFromView(view: View): Bitmap? {
        val returnedBitmap =
            Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(returnedBitmap)
        val bgDrawable = view.getBackground()
        bgDrawable?.draw(canvas)
        *//*  else
                canvas.drawColor(Color.WHITE);*//*
        *//*  else
                canvas.drawColor(Color.WHITE);*//*view.draw(canvas)
        return returnedBitmap
    }
}*/