package com.onboarding.nowfloats.model.common

import android.graphics.Typeface
import androidx.annotation.ColorInt
import com.framework.enums.TextType
import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

class SectionHeaderModel : AppBaseRecyclerViewItem {

  var text: CharSequence? = null
  @ColorInt
  var textColor: Int? = null
  var typeface: Typeface? = null
  var gravity: Int? = null
  var textType: TextType? = null

  companion object;
  override fun getViewType(): Int {
    return RecyclerViewItemType.SECTION_HEADER_ITEM.getLayout()
  }
}

fun SectionHeaderModel.Companion.getInstance(
    text: CharSequence? = null,
    @ColorInt textColor: Int? = null,
    typeface: Typeface? = null,
    gravity: Int? = null,
    textType: TextType? = null,
): SectionHeaderModel {
  val model = SectionHeaderModel()
  model.text = text
  model.textColor = textColor
  model.typeface = typeface
  model.gravity = gravity
  model.textType = textType
  return model
}