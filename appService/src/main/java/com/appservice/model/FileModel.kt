package com.appservice.model

import com.appservice.constant.RecyclerViewItemType
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.utils.getExtension
import java.io.File
import java.io.Serializable

data class FileModel(
    var path: String? = null,
    var recyclerViewItem: Int = RecyclerViewItemType.IMAGE_PREVIEW.getLayout()
) : AppBaseRecyclerViewItem, Serializable {

  override fun getViewType(): Int {
    return recyclerViewItem
  }


  fun getExt(): String? {
    return path?.getExtension()
  }

  fun getFile(): File? {
    return File(path)
  }
}