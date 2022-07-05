package com.framework.imagepicker

import android.content.Context
import android.content.Intent
import com.framework.utils.toArrayList
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Sarvare Alam on 5/21/2018.
 */
internal object ImageProcessing {
  private const val TAG = "ImageProcessing"

  fun processMultiImage(context: Context, data: Intent): ArrayList<String?> {
    val listOfImgs: MutableList<String?> = ArrayList()
    val clipdata = data.clipData
    if (clipdata != null) {
      for (i in 0 until clipdata.itemCount) {
        val selectedImage = clipdata.getItemAt(i).uri
        val selectedImagePath = FileProcessing.getPath(selectedImage)
        listOfImgs.add(selectedImagePath)
      }
    }
    return listOfImgs.toArrayList()
  }
}
