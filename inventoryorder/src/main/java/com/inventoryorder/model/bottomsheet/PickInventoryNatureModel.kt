package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem

class PickInventoryNatureModel(var inventoryTypeIcon: Int? = null,
                               var inventoryName: String? = null,
                               var inventoryDescription: String? = null,
                               var inventoryTypeSelectedIcon: Int? = null,
                               var isInventorySelected: Boolean = false) : AppBaseRecyclerViewItem {

    override fun getViewType(): Int {
        return RecyclerViewItemType.PICK_INVENTORY_NATURE.getLayout()
    }

    fun getData(): ArrayList<PickInventoryNatureModel> {
        val list = ArrayList<PickInventoryNatureModel>()
        list.add(PickInventoryNatureModel(R.drawable.ic_physical_product, "Physical product",
                "Can be packaged and delivered to buyer, eg. book,watch,toy,garment,etc.", R.drawable.ic_selected, true))
        list.add(PickInventoryNatureModel(R.drawable.ic_service_offering, "Service offering",
                "Can be packaged and delivered to buyer, eg. book,watch,toy,garment,etc.", R.drawable.ic_option_unselected, false))
        list.add(PickInventoryNatureModel(R.drawable.ic_booking_based_inventory, "Booking based inventory",
                "Can be packaged and delivered to buyer, eg. book,watch,toy,garment,etc.", R.drawable.ic_option_unselected, false))
        list.add(PickInventoryNatureModel(R.drawable.ic_digital_asset, "Digital asset",
                "Can be packaged and delivered to buyer, eg. book,watch,toy,garment,etc.", R.drawable.ic_option_unselected, false))

        return list
    }


}