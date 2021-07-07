package com.onboarding.nowfloats.bottomsheet

import android.view.View
import android.widget.TextView
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.bottomsheet.inerfaces.ContentBuilder
import com.onboarding.nowfloats.bottomsheet.util.listenToUpdate

class MessageContentBuilder(text: String, selectable: Boolean) : ContentBuilder() {
  var text: String by listenToUpdate(text, this, -2)
  var selectable: Boolean by listenToUpdate(selectable, this, 2)

  private lateinit var textView: TextView
  override val layoutRes: Int
    get() = R.layout.feature_content_sheet


  override fun init(view: View) {
//        textView = view.message_text
  }

  override fun updateContent(type: Int, data: Any?) {
  }
}