package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class ChoosePurposeModel(
  val choosePurposeSelectedName: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ITEM_CHOOSE_PURPOSE.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected_orange }
      ?: R.drawable.ic_option_unselected
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.lightest_grey } ?: R.color.white
  }

  fun getData(): ArrayList<ChoosePurposeModel> {
    val list = ArrayList<ChoosePurposeModel>()
    list.add(ChoosePurposeModel("Choose purpose 1"))
    list.add(ChoosePurposeModel("Choose purpose 2"))
    list.add(ChoosePurposeModel("Choose purpose 3", true))
    list.add(ChoosePurposeModel("Choose purpose 4"))
    list.add(ChoosePurposeModel("Choose purpose 5"))
    list.add(ChoosePurposeModel("Choose purpose 6"))
    return list
  }

}