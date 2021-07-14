package com.inventoryorder.ui.tutorials.model

import com.google.gson.annotations.SerializedName
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.io.Serializable

data class AllVideoResponse(

  @field:SerializedName("Contents")
  var contents: VideoContents? = null,

  @field:SerializedName("Fragment Data")
  var fragmentData: FragmentData? = null,
)

data class AllTutorialsItem(

  @field:SerializedName("video url")
  var videoUrl: String? = null,
  @field:SerializedName("video thumbnails")
  var videoThumbnails: String? = null,
  @field:SerializedName("video title")
  var videoTitle: String? = null,
  @field:SerializedName("video length")
  var videoLength: String? = null,

  ) : Serializable, AppBaseRecyclerViewItem {
  var recyclerViewType = RecyclerViewItemType.ITEM_VIDEO.getLayout()
  override fun getViewType(): Int {
    return recyclerViewType
  }
}

data class VideoContents(

  @field:SerializedName("Heading")
  var heading: String? = null,

  @field:SerializedName("All tutorials")
  var allTutorials: ArrayList<AllTutorialsItem>? = null,

  @field:SerializedName("video title")
  var videoTitle: String? = null,
)


