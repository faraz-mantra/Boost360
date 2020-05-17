package com.inventoryorder.model.bottomsheet

import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class GenderSelectionModel(val gender : String? = null) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
     return RecyclerViewItemType.GENDER_SELECTION.getLayout()
    }

    private fun getData() : ArrayList<GenderSelectionModel>{

        val list = ArrayList<GenderSelectionModel>()
        list.add(GenderSelectionModel("Male"))
        list.add(GenderSelectionModel("FeMale"))
        list.add(GenderSelectionModel("Others"))

        return list
    }
}