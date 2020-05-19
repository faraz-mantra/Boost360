package com.inventoryorder.model.bottomsheet

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class GenderSelectionModel(val gender : String? = null,
                           var selectedGender : String? = null,
                           var isSelected : Boolean = false) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
     return RecyclerViewItemType.GENDER_SELECTION.getLayout()
    }

    fun getData() : ArrayList<GenderSelectionModel>{
        val list = ArrayList<GenderSelectionModel>()
        list.add(GenderSelectionModel("Male"))
        list.add(GenderSelectionModel("Female"))
        list.add(GenderSelectionModel("Other"))
        return list
    }
}