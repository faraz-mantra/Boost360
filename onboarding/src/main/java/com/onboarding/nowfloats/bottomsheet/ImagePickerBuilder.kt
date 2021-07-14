package com.onboarding.nowfloats.bottomsheet

import android.app.Dialog
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate
import kotlinx.android.synthetic.main.image_picker_content.view.*

class ImagePickerBuilder(text: String, val onClick: onClickItem) : ContentBuilder() {
  var text: String by listenToUpdate(text, this, -2)

  private lateinit var gallery: LinearLayoutCompat
  private lateinit var camera: LinearLayoutCompat
  private lateinit var close: LinearLayoutCompat
  override val layoutRes: Int
    get() = R.layout.image_picker_content

  override fun init(view: View) {
    gallery = view.gallery
    camera = view.camera
    close = view.close
    gallery.setOnClickListener { onClick.invoke(dialog, 1, false) }
    camera.setOnClickListener { onClick.invoke(dialog, 2, false) }
    close.setOnClickListener { onClick.invoke(dialog, 3, false) }
  }

  override fun updateContent(type: Int, data: Any?) {
  }
}

typealias onClickItem = (dialog: Dialog, type: Int, isLongClick: Boolean) -> Unit