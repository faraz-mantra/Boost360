package com.inventoryorder.model.bottomsheet

import com.inventoryorder.R
import com.inventoryorder.constant.RecyclerViewItemType
import com.inventoryorder.recyclerView.AppBaseRecyclerViewItem
import java.util.*
import kotlin.collections.ArrayList

class PickInventoryNatureModel(
  var inventoryName: String? = null,
  var inventoryDescription: String? = null,
  var type: String? = null,
  var isSelected: Boolean = false
) : AppBaseRecyclerViewItem {

  override fun getViewType(): Int {
    return RecyclerViewItemType.PICK_INVENTORY_NATURE.getLayout()
  }

  fun getSelectIcon(): Int {
    return takeIf { isSelected }?.let { R.drawable.ic_selected } ?: R.drawable.ic_option_unselected
  }

  fun getColorTitle(): Int {
    return takeIf { isSelected }?.let { R.color.black_two } ?: R.color.warm_grey
  }

  fun getColorDesc(): Int {
    return takeIf { isSelected }?.let { R.color.greyish_brown } ?: R.color.warm_grey
  }

  fun getData(): ArrayList<PickInventoryNatureModel> {
    val list = ArrayList<PickInventoryNatureModel>()
    list.add(
      PickInventoryNatureModel(
        "Physical product", "Can be packaged and shipped to buyer. E.g. book, watch, toy, garment.",
        InventoryType.PRODUCTS.name, true
      )
    )

    list.add(
      PickInventoryNatureModel(
        "Service offering",
        "Tasks that are performed by individuals for the benefit of others. E.g. therapy, training, financial consultation.",
        InventoryType.SERVICES.name
      )
    )

    list.add(
      PickInventoryNatureModel(
        "Booking based inventory",
        "Cases where payment is made against time. e.g.: hotel room, studio on rent, doctor consultation.",
        InventoryType.BOOKINGS.name
      )
    )

    list.add(
      PickInventoryNatureModel(
        "Digital asset",
        "Can be downloaded as a digital file. e.g.: PDF, e-book, digital course, documents, design files.",
        InventoryType.DIGITAL.name
      )
    )
    return list
  }

  fun getIconType(): Int? {
    return when (this.type?.let { InventoryType.fromName(it) }) {
      InventoryType.PRODUCTS -> if (this.isSelected) R.drawable.ic_physical_product_check else R.drawable.ic_physical_product_uncheck
      InventoryType.SERVICES -> if (this.isSelected) R.drawable.ic_service_offering_check else R.drawable.ic_service_offering_uncheck
      InventoryType.BOOKINGS -> if (this.isSelected) R.drawable.ic_booking_based_inventory_check else R.drawable.ic_booking_based_inventory_uncheck
      InventoryType.DIGITAL -> if (this.isSelected) R.drawable.ic_digital_asset_check else R.drawable.ic_digital_asset_uncheck
      else -> null
    }
  }

  enum class InventoryType(name: String) {
    PRODUCTS("products"), SERVICES("services"), BOOKINGS("bookings"), DIGITAL("digital");

    companion object {
      fun fromName(name: String): InventoryType? =
        values().firstOrNull { it.name.toLowerCase(Locale.ROOT) == name.toLowerCase(Locale.ROOT) }
    }
  }

}