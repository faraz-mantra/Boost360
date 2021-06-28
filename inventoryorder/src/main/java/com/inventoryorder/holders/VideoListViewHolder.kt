package com.inventoryorder.holders

import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.RecyclerItemListVideosBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewHolder
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.ui.tutorials.model.AllTutorialsItem

class VideoListViewHolder(binding: RecyclerItemListVideosBinding) : AppBaseRecyclerViewHolder<RecyclerItemListVideosBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val data = item as? AllTutorialsItem
        binding.ctvVideoDuration.text = data?.videoLength
//        Glide.with(BaseOrderApplication.instance.applicationContext).load(retrieveVideoFrameFromVideo(data?.videoUrl)).into(binding.videoThumbnails)
        binding.ctvVideoTitle.text = data?.videoTitle
        binding.root.setOnClickListener {
            listener?.onItemClick(position, item, actionType = RecyclerViewActionType.VIDEO_ITEM_CLICK.ordinal)
        }
    }

//    private fun retrieveVideoFrameFromVideo(videoPath: String?): Bitmap? {
//
//        var bitmap: Bitmap? = null
//        var mediaMetadataRetriever: MediaMetadataRetriever? = null
//        try {
//            mediaMetadataRetriever = MediaMetadataRetriever()
//            mediaMetadataRetriever.setDataSource(videoPath, HashMap<String, String>())
//            bitmap = mediaMetadataRetriever.frameAtTime
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            mediaMetadataRetriever?.release()
//        }
//
//        return bitmap
//    }


}