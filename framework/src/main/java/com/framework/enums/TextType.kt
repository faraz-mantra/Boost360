package com.framework.enums

import android.util.TypedValue
import android.widget.TextView
import com.framework.R

enum class TextType(val type: Int) {
  TITLE(0),
  SUBTITLE(1),
  HEADING_1(2),
  HEADING_2(3),
  HEADING_3(4),
  HEADING_4(5),
  HEADING_5(6),
  HEADING_6(7),
  SUBHEADING_1(8),
  SUBHEADING_2(9),
  SUBTITLE_1(10),
  SUBTITLE_2(11),
  BODY_1(12),
  BODY_2(13),
  BODY_3(14),
  CAPTION(15),
  OVERLINE(16),
  HEADING_TEXT_1(17),
  HEADING_TEXT_2(18),
  HEADING_TEXT_3(19),
  HEADING_7(20);
}

fun TextView.setTextStyle(textType: TextType?) {
  if (textType == null) return
  val size = when (textType) {
    TextType.TITLE -> R.dimen.title
    TextType.SUBTITLE -> R.dimen.subtitle
    TextType.HEADING_1 -> R.dimen.heading_1
    TextType.HEADING_2 -> R.dimen.heading_2
    TextType.HEADING_3 -> R.dimen.heading_3
    TextType.HEADING_4 -> R.dimen.heading_4
    TextType.HEADING_5 -> R.dimen.heading_5
    TextType.HEADING_6 -> R.dimen.heading_6
    TextType.SUBHEADING_1 -> R.dimen.subheading_1
    TextType.SUBHEADING_2 -> R.dimen.subheading_2
    TextType.SUBTITLE_1 -> R.dimen.subtitle_1
    TextType.SUBTITLE_2 -> R.dimen.subtitle_2
    TextType.BODY_1 -> R.dimen.body_1
    TextType.BODY_2 -> R.dimen.body_2
    TextType.BODY_3 -> R.dimen.body_3
    TextType.CAPTION -> R.dimen.caption
    TextType.OVERLINE -> R.dimen.overline
    TextType.HEADING_TEXT_1 -> R.dimen.heading_text_1
    TextType.HEADING_TEXT_2 -> R.dimen.heading_text_2
    TextType.HEADING_TEXT_3 -> R.dimen.heading_text_3
    TextType.HEADING_7 -> R.dimen.heading_7
  }
  this.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(size))
}