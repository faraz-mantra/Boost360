package com.dashboard.holder

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import com.dashboard.R
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.constant.RecyclerViewActionType.PRIMARY_FONT_SELECTED
import com.dashboard.databinding.RecyclerItemSelectFontBinding
import com.dashboard.model.websitetheme.PrimaryItem
import com.dashboard.model.websitetheme.SecondaryItem
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible

class WebSiteFontViewHolder(binding: RecyclerItemSelectFontBinding) : AppBaseRecyclerViewHolder<RecyclerItemSelectFontBinding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    when (item) {
      is PrimaryItem -> {
        binding.ctvFontRadio.text = if (item.defaultFont == true) "${item.getDescriptionN(position)}*" else item.getDescriptionN(position)
        if (item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = false
          binding.ctvFontCurrent.text = "CURRENT"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.black_4a4a4a)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.black_4a4a4a)!!)
        } else {
          binding.ctvFontCurrent.gone()
          binding.ctvFontRadio.isChecked = false
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.black_4a4a4a)!!)
        }
        if (item.isNewSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = true
          binding.ctvFontCurrent.text = "NEW"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.colorAccentLight)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.colorAccentLight)!!)

        }
        if (item.isNewSelected == true && item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = true
          binding.ctvFontCurrent.text = "CURRENT"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.black_4a4a4a)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.colorAccentLight)!!)

        }
        binding.ctvFontRadio.setOnClickListener { primaryItemClick(item, position) }
        binding.root.setOnClickListener { primaryItemClick(item, position) }
      }
      is SecondaryItem -> {
        binding.ctvFontRadio.text = if (item.defaultFont == true) "${item.getDescriptionN(position)}*" else item.getDescriptionN(position)
        if (item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = false
          binding.ctvFontCurrent.text = "CURRENT"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.black_4a4a4a)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.black_4a4a4a)!!)

        } else {
          binding.ctvFontCurrent.gone()
          binding.ctvFontRadio.isChecked = false
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.black_4a4a4a)!!)
        }
        if (item.isNewSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = true
          binding.ctvFontCurrent.text = "NEW"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.colorAccent)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.colorAccent)!!)

        }
        if (item.isNewSelected == true && item.isSelected == true) {
          binding.ctvFontCurrent.visible()
          binding.ctvFontRadio.isChecked = true
          binding.ctvFontCurrent.text = "CURRENT"
          binding.ctvFontCurrent.setTextColor(getResources()?.getColor(R.color.black_4a4a4a)!!)
          binding.ctvFontRadio.buttonTintList = ColorStateList.valueOf(getResources()?.getColor(R.color.colorAccent)!!)

        }
        binding.ctvFontRadio.setOnClickListener {
          secondaryItemClick(item, position)
        }
        binding.root.setOnClickListener {
          secondaryItemClick(item, position)
        }
      }
    }
  }

  private fun primaryItemClick(item: PrimaryItem, position: Int) {
    item.isNewSelected = true
    binding.ctvFontRadio.isChecked = true
    listener?.onItemClick(position, item, PRIMARY_FONT_SELECTED.ordinal)
  }

  private fun secondaryItemClick(item: SecondaryItem, position: Int) {
    item.isNewSelected = true
    binding.ctvFontRadio.isChecked = true
    listener?.onItemClick(position, item, RecyclerViewActionType.SECONDARY_FONT_SELECTED.ordinal)
  }
}