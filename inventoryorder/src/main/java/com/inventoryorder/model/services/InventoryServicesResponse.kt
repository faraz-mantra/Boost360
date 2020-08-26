package com.inventoryorder.model.services

import com.inventoryorder.model.services.InventoryServicesResponseItem


class InventoryServicesResponse : ArrayList<InventoryServicesResponseItem>() {
  enum class IdentifierType {
    SINGLE
  }
}