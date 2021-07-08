package com.boost.presignin.holder.common

import android.view.Gravity
import androidx.core.content.res.ResourcesCompat.getFont
import com.boost.presignin.R
import com.boost.presignin.databinding.ItemSectionHeaderLayoutBinding
import com.boost.presignin.model.common.SectionHeaderModel
import com.boost.presignin.model.common.getInstance
import com.boost.presignin.recyclerView.AppBaseRecyclerViewHolder
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.framework.enums.TextType

class SectionHeaderRecyclerViewHolder constructor(binding: ItemSectionHeaderLayoutBinding) :
  AppBaseRecyclerViewHolder<ItemSectionHeaderLayoutBinding>(binding) {

  private var model: SectionHeaderModel? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = SectionHeaderModel.getInstance(
      text = getResources()?.getString(R.string._or_),
      textColor = getColor(R.color.warm_grey),
      typeface = activity?.let { getFont(it, R.font.italic) },
      textType = TextType.SUBTITLE_2,
      gravity = Gravity.CENTER
    )
    setViews(model)
  }

  private fun setViews(model: SectionHeaderModel?) {
    binding.textView.text = model?.text
    model?.typeface?.let { binding.textView.typeface = it }
    model?.textColor?.let { binding.textView.setTextColor(it) }
    model?.gravity?.let { binding.textView.gravity = it }
    // model?.textType?.let { binding.textView.setTextStyle(it) }
  }
}