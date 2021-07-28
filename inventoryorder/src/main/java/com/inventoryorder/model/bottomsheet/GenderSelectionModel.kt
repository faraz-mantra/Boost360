package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class GenderSelectionModel(
  val genderType: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.GENDER_SELECTION.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected_orange }
      ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.lightest_grey } ?: R.color.white
  }

  fun getData(): ArrayList<GenderSelectionModel> {
    val list = ArrayList<GenderSelectionModel>()
    list.add(GenderSelectionModel("Male", true))
    list.add(GenderSelectionModel("Female"))
    list.add(GenderSelectionModel("Other"))
    return list
  }
}