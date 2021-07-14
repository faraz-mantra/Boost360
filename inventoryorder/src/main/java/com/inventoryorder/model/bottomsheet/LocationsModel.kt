package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class LocationsModel(
  val serviceOptionSelectedIcon: Int? = null,
  val serviceOptionSelectedName: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.ITEM_SERVICE_LOCATIONS.getLayout()
  }

  fun getIcon(): Int? {
    return takeIf { isSelected }?.let { R.drawable.ic_option_selected } ?: serviceOptionSelectedIcon
  }

  fun getColor(): Int {
    return takeIf { isSelected }?.let { R.color.khaki_light } ?: R.color.white
  }

  fun getData(): ArrayList<LocationsModel> {
    val list = ArrayList<LocationsModel>()
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 1"))
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 2"))
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 3", true))
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 4"))
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 5"))
    list.add(LocationsModel(R.drawable.ic_option_unselected, "Service Location 6"))
    return list
  }
}