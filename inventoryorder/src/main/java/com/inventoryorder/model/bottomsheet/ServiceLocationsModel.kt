package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class ServiceLocationsModel (val serviceOptionSelectedIcon: Int? = null,
                             val serviceOptionSelectedName: String? = null,
                             var isSelected: Boolean = false
) : AppBaseRecyclerViewItem   {

    override fun getViewType(): Int {
      return RecyclerViewItemType.ITEM_SERVICE_LOCATIONS.getLayout()
    }

    fun getIcon(): Int? {
        return takeIf { isSelected }?.let { R.drawable.ic_option_selected } ?: serviceOptionSelectedIcon
    }

    fun getColor(): Int {
        return takeIf { isSelected }?.let { R.color.khaki_light } ?: R.color.white
    }

    fun getData(): ArrayList<ServiceLocationsModel> {
        val list = ArrayList<ServiceLocationsModel>()
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Option comes here"))
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Option comes here"))
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Subtitle 2", true))
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Delivery Option 4"))
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Option comes here"))
        list.add(ServiceLocationsModel(R.drawable.ic_option_unselected, "Option comes here"))
        return list
    }
}